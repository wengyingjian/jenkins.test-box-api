package com.store59.box.service;

import com.store59.base.common.api.RepoApi;
import com.store59.base.common.model.Repo;
import com.store59.box.canstants.OrderStatus;
import com.store59.box.canstants.PayType;
import com.store59.box.canstants.RetCode;
import com.store59.box.exception.ServiceException;
import com.store59.box.exception.ServiceExceptionHandler;
import com.store59.box.exceptions.BoxException;
import com.store59.box.model.Box;
import com.store59.box.model.Order;
import com.store59.box.model.OrderItem;
import com.store59.box.model.OrderItemAdd;
import com.store59.box.remoting.BoxService;
import com.store59.box.util.ResultUtil;
import com.store59.box.util.SignCheckUtils;
import com.store59.box.viewmodel.*;
import com.store59.coupon.model.CouponUse;
import com.store59.coupon.remoting.CouponService;
import com.store59.creditmall.enums.CreditOrderType;
import com.store59.creditmall.enums.CreditRecordAction;
import com.store59.creditmall.enums.CreditType;
import com.store59.creditmall.model.CreditRecord;
import com.store59.creditmall.remoting.CreditRecordService;
import com.store59.kylin.common.Util;
import com.store59.kylin.common.cache.ICache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangwangyong on 15/7/29.
 */
@Service
public class OrderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private com.store59.box.remoting.OrderService orderServiceApi;
    @Autowired
    private BoxCartService boxCartService;
    @Autowired
    private BoxService boxServiceApi;
    @Autowired
    private RepoApi repoApi;
    @Autowired
    private ICache iCacheClient;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CreditRecordService creditRecordService;
    @Autowired
    private ActivityService activityService;


    @Value("${box.image.url.pre}")
    private String BOX_IMAGE_PREX;
    @Value("${box.image.url.suf}")
    private String BOX_IMAGE_SUFX;


    public final static Integer ORDER_TYPE = 5;
    private final static String REDIS_ID_LIST_PRE = "boxapi.order_id_list.";
    private final static int COUPON_ORDER_TYPE = 5;
    private final static String TIPS = "提示，未支付订单，15分钟之后将被取消";
    private final static String CREDIT_BAIHUAHUA_TITLE = "零食盒子订单(白花花支付)";
    private final static String CREDIT_BOXORDER_TITLE = "零食盒子订单";
    private static final int REDIS_EXPIRE = 172800;

    /**
     * 新增盒子订单
     * */
    public Result addOrder(String token,Integer boxId,String couponCode,Long uid,Integer source,String ip,String appVersion) {
        Result result;
        BoxCart boxCart;
        //判断是否实用优惠券
        if(StringUtils.isEmpty(couponCode)) {
            boxCart = boxCartService.findBoxCartByBoxId(token, boxId);
        }else{
            Result result1 = boxCartService.useCoupon(token,boxId,couponCode);
            if(null != result1.getData()){
                boxCart = (BoxCart)result1.getData();
            }else{
                return result1;
            }
        }
        try {
            if (null != boxCart && !CollectionUtils.isEmpty(boxCart.getItemList())) {
                Order order = createOrderByCart(boxCart, uid, source,ip);
                com.store59.kylin.common.model.Result serviceResult = orderServiceApi.addOrder(order);
                if(!StringUtils.isEmpty(couponCode)) {
                    Order orderForCoupon = (Order)serviceResult.getData();
                    //使用优惠券
                    useCoupon(couponCode,orderForCoupon.getOrderId(),orderForCoupon.getUid());
                }

                result = ResultUtil.resultService2View(serviceResult);
                if (null != serviceResult && null != serviceResult.getData()) {
                    order = (Order) serviceResult.getData();
                    if (null == order) {
                        return ResultUtil.genResult(RetCode.NORMAL_ERROR,"新增订单失败");
                    }
                    Box box =  boxServiceApi.findBoxById(order.getBoxId(), false).getData();
                    result.setData(changeServiceOrder2ViewOrder(order, box,appVersion));
                }else {
                    return result;
                }
                //如果用户时匿名用户，保存订单id到redis
                if(uid == null || uid == 0) {
                    String orderIdListJson = iCacheClient.get(REDIS_ID_LIST_PRE + token);
                    List<Long> orderIdList;
                    if(StringUtils.isEmpty(orderIdListJson)){
                        orderIdList = new ArrayList<>();
                    }else {
                        orderIdList = Util.getObjectFromJson(orderIdListJson, ArrayList.class);
                    }
                    orderIdList.add(order.getOrderId());
                    iCacheClient.setex(REDIS_ID_LIST_PRE+token,REDIS_EXPIRE,Util.getJsonFromObject(orderIdList));
                }
            } else {
                return ResultUtil.genResult(RetCode.NORMAL_ERROR,"购物车中不存在零食,请选择零食。");
            }
            //清空购物车
            boxCartService.clearBoxCart(token, boxId);
        }catch (BoxException be){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,be.getMsg());
        }

        return result;
    }

    /**
     * 使用优惠券
     * */
    private void useCoupon(String code,Long orderId,long uid){
        CouponUse couponUse = new CouponUse();
        couponUse.setUseUid(uid);
        couponUse.setUseOrderType(COUPON_ORDER_TYPE);
        couponUse.setUseOrderId(orderId);
        couponUse.setCode(code);
        couponService.useCoupon(couponUse);
    }

    /**
     * 根据用户uid获取用户订单列表
     * */
    public Result getOrderListByUid(Long uid,String token,String appVersion) {
        Map<String,List<ViewOrder>> map = new HashMap<>();
        map.put("orders", null);

        com.store59.kylin.common.model.Result serviceResult;
        if(uid == null || uid == 0){
            //如果时匿名用户查询redis中是否存在订单ID列表
            String orderIdListJson = iCacheClient.get(REDIS_ID_LIST_PRE + token);
            if(StringUtils.isEmpty(orderIdListJson)){
                return ResultUtil.genSuccessResult(map);
            }
            List<Long> orderIdList = Util.getObjectFromJson(orderIdListJson, ArrayList.class);
            if(CollectionUtils.isEmpty(orderIdList)){
                return ResultUtil.genSuccessResult(map);
            }
            serviceResult = orderServiceApi.getOrderList(orderIdList);
        }else {
            //非匿名用户直接根据uid获取用户订单信息
            serviceResult = orderServiceApi.getOrderList(uid);
        }
        Result result = ResultUtil.resultService2View(serviceResult);

        if(null != serviceResult && null != serviceResult.getData()){
            List<Order> orderList = (List<Order>)serviceResult.getData();
            if (!CollectionUtils.isEmpty(orderList)){
                //查询订单对应到盒子列表
                Set<Integer> boxIdSet = new HashSet<>();
                for(Order order : orderList){
                    //解析盒子明细信息
                    order.setList(getOrderItemList(order.getDetailJson()));
                    boxIdSet.add(order.getBoxId());
                }
                List<Box> boxList = boxServiceApi.findBoxList(new ArrayList<>(boxIdSet)).getData();
                List<ViewOrder> viewOrderList = new ArrayList<>();
                for(Order order : orderList){
                      Box box = null;
                      for(Box box1 : boxList){
                          if (order.getBoxId().compareTo(box1.getId()) == 0){
                              box = box1;
                          }
                      }
                    viewOrderList.add(changeServiceOrder2ViewOrder(order,box,appVersion));
                }
                map.put("orders", viewOrderList);
            }
        }
        result.setData(map);
        return result;
    }

    /**
     * 根据订单sn获取订单信息
     * */
    public Result getOrderInfoBySn(String orderSn,String appVersion) {
        com.store59.kylin.common.model.Result serviceResult = orderServiceApi.getOrderInfo(orderSn);
        Result result = ResultUtil.resultService2View(serviceResult);
        if(null != serviceResult && null != serviceResult.getData()){
            Order order = (Order) serviceResult.getData();
            if (null == order){
                return ResultUtil.genResult(RetCode.NORMAL_ERROR,"所查询到订单不存在");
            }

            order.setList(getOrderItemList(order.getDetailJson()));
            Box box =  boxServiceApi.findBoxById(order.getBoxId(),false).getData();
            ViewOrder viewOrder = changeServiceOrder2ViewOrder(order, box,appVersion);

            if(viewOrder.getStatus() != null && viewOrder.getStatus().equals(OrderStatus.PAID.ordinal())) {
                List<Activity> list = new ArrayList<>();
                list.add(activityService.createQuestionActivity());
                viewOrder.setActivities(list);
            }
            result.setData(viewOrder);
        }
        return result;
    }

    /**
     * 订单取消
     * */
    public Result cancelOrder(String orderSn) {
        Order orderInfo = orderServiceApi.getOrderInfo(orderSn).getData();
        if(orderInfo == null){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR, "所要取消的订单不存在");
        }
        if(orderInfo.getStatus().compareTo(OrderStatus.NEW.ordinal()) != 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR, "所要取消订单状态不可取消");
        }

        Order order = new Order();
        order.setSn(orderSn);
        order.setStatus(OrderStatus.CANCEL.ordinal());
        order.setCancelTime((System.currentTimeMillis() / 1000));

        com.store59.kylin.common.model.Result<Boolean> serviceResult = orderServiceApi.update(order);
        if(null == serviceResult.getData() || !serviceResult.getData()){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"订单取消失败");
        }
        try {
            couponService.returnCoupon(orderInfo.getCouponCode());
        }catch (Exception e){
            //失败打印日志
            logger.error(ServiceExceptionHandler.getTrace(e));
        }
        return ResultUtil.resultService2View(serviceResult);
    }

    /**
     * 订单支付
     * */
    public PayResult payOrder(String orderSn, Integer payType, String payTradeNo,double money,Long payTime) {
        //判断订单金额是否正确，如果正确则订单支付
        Order order = orderServiceApi.getOrderInfo(orderSn).getData();
        if(null == order){
            return ResultUtil.genPayResult(RetCode.NORMAL_ERROR, "所要支付的订单不存在");
        }
        if(order.getStatus() == OrderStatus.PAID.ordinal()){
            return ResultUtil.genPayResult(RetCode.NORMAL_ERROR, "所要支付的订单为已支付状态");
        }
        if(order.getStatus() == OrderStatus.CANCEL.ordinal()){
            return ResultUtil.genPayResult(RetCode.NORMAL_ERROR, "所要支付的订单为已取消状态");
        }
        if(order.getStatus() != OrderStatus.NEW.ordinal()){
            return ResultUtil.genPayResult(RetCode.NORMAL_ERROR, "所要支付的订单状态不正确");
        }
        //安卓app bug需要实现 (付款金额+0.01 == 订单金额)
        if((null == order.getOrderAmount())
                || order.getOrderAmount().compareTo(money) != 0){
            return ResultUtil.genPayResult(RetCode.NORMAL_ERROR, "支付金额与订单金额不一致");
        }

        //更新订单的状态、支付方式、支付时间、支付账号信息
        Order updateOrder = new Order();
        updateOrder.setSn(orderSn);
        updateOrder.setPayType(payType);
        updateOrder.setPayTradeNo(payTradeNo);
        updateOrder.setPayTime(payTime);
        updateOrder.setUid(order.getUid());
        updateOrder.setStatus(OrderStatus.PAID.ordinal());

        com.store59.kylin.common.model.Result<Boolean> serviceResult = orderServiceApi.update(updateOrder);
        if(null == serviceResult.getData() || !serviceResult.getData()){
            order = orderServiceApi.getOrderInfo(orderSn).getData();
            //如果所要支付的订单为已取消，则需要纪录退款信息
            if(order.getStatus() == OrderStatus.CANCEL.ordinal()){
                orderServiceApi.orderRefunding(updateOrder);
            }
            return ResultUtil.genPayResult(RetCode.NORMAL_ERROR, "订单支付失败");
        }

        Map map = new HashMap<>();
        map.put("result", "success");

        //支付完成后增加积分
        addCredit(updateOrder, order.getOrderAmount());
        return ResultUtil.genSuccessPayResult(map);
    }

    /**
     * 积分增加
     * */
    private void addCredit(Order updateOrder,double amount){
        CreditRecord creditRecord = new CreditRecord();
        creditRecord.setOrderSn(updateOrder.getSn());
        creditRecord.setUid(updateOrder.getUid());
        creditRecord.setAction(CreditRecordAction.ADD.ordinal());
        creditRecord.setOrderType(CreditOrderType.BOX.ordinal());
        creditRecord.setMoney(amount);
        if(updateOrder.getPayType().compareTo(PayType.SPEND.ordinal()) == 0){
            creditRecord.setCredit((int)(amount*20));
            creditRecord.setType(CreditType.BAIHUAHUA.ordinal());
            creditRecord.setRemark(CREDIT_BAIHUAHUA_TITLE);
        }else {
            creditRecord.setCredit((int)(amount*10));
            creditRecord.setType(CreditType.CONSUMPTION.ordinal());
            creditRecord.setRemark(CREDIT_BOXORDER_TITLE);
        }
        try {
            creditRecordService.addCreditRecord(creditRecord);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            logger.info("盒子订单积分商城新增积分失败:[orderSn:"+updateOrder.getSn()+",payType:"+updateOrder.getPayType()+"," +
                    "money:"+amount);
        }

    }


    /**
     * 根据订单的明细JSON获取明细列表
     * */
    private List<OrderItem> getOrderItemList(String detailJson){
        List<OrderItem> list = new ArrayList<>();
        if(StringUtils.isEmpty(detailJson)){
            return list;
        }
        List<LinkedHashMap> maps = null;
        try{
            maps = Util.getObjectFromJson(detailJson, ArrayList.class);
        }catch (Exception e){
            //如果转换失败则不显示明细信息
            logger.info("订单明细信息转换失败");
        }
        if(!CollectionUtils.isEmpty(maps)){
            for(LinkedHashMap map : maps){
                OrderItem orderItem = new OrderItem();
                orderItem.setAmount((double)map.get("amount"));
                orderItem.setPrice((double) map.get("price"));
                orderItem.setQuantity((int) map.get("quantity"));
                orderItem.setRid((int)map.get("rid"));
                Repo repo = repoApi.findRepoByRid(orderItem.getRid()).getData();
                if(null != repo) {
                    orderItem.setImg(BOX_IMAGE_PREX + repo.getDefaultImage() + BOX_IMAGE_SUFX);
                    orderItem.setRname(repo.getName());
                }
                list.add(orderItem);
            }
        }

        return list;
    }


    /**
     * 根据购物车信息生成盒子订单
     * */
    private Order createOrderByCart(BoxCart boxCart,Long uid,Integer source,String ip){
        Order order = new Order();
        order.setStatus(OrderStatus.NEW.ordinal());
        order.setBoxId(boxCart.getBoxId());
        order.setCouponCode(boxCart.getCouponCode());
        order.setCouponDiscount(boxCart.getCouponDiscount());
        order.setOrderAmount(boxCart.getAmount().doubleValue());
        order.setFoodAmount(boxCart.getFoodAmount().doubleValue());
        order.setUid(uid);
        order.setSource(source);
        order.setSn(makeCode());
        order.setCreateTime(System.currentTimeMillis() / 1000);
        order.setIp(ip);
        //如果订单金额为0则默认支付完成
        if(boxCart.getAmount().compareTo(BigDecimal.ZERO) == 0){
            order.setStatus(OrderStatus.PAID.ordinal());
            order.setPayTime(System.currentTimeMillis() / 1000);
            order.setPayType(PayType.CASH_ON_DELIVERY.getType());
        }
        if(!CollectionUtils.isEmpty(boxCart.getItemList())){
            List<OrderItemAdd> list = new ArrayList<>();
            for(BoxCartItem boxCartItem : boxCart.getItemList()){
                OrderItemAdd orderItem = new OrderItemAdd();
                orderItem.setAmount(boxCartItem.getAmount().doubleValue());
                orderItem.setPrice(boxCartItem.getPrice().doubleValue());
                orderItem.setQuantity(boxCartItem.getQuantity());
                orderItem.setRid(boxCartItem.getRid());
                list.add(orderItem);
            }
            order.setOrderItemAddList(list);
        }
        return order;
    }

    /**
     * 生成code
     * code生成规则：1、12位数的时间字符串YYYYMMDDHHMM
     *              2、后面增加8位随机数
    * */
    private String makeCode(){
        StringBuffer sb = new StringBuffer();
        sb.append(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        String seed = "0123456789";
        int resultLength = 8;
        for(int i=0; i<resultLength; i++){
            double dbRandom = Math.random()*seed.length();
            int intRandom = (int)Math.floor(dbRandom);
            char c = seed.charAt(intRandom);
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 数据库订单信息与盒子信息组合成显示的订单信息
     * */
    private ViewOrder changeServiceOrder2ViewOrder(Order order,Box box,String appVersion) {
        ViewOrder viewOrder = new ViewOrder();
        if (null != order) {
            viewOrder.setStatus(order.getStatus());
            viewOrder.setBoxId(order.getBoxId());

            viewOrder.setCreateTime(order.getCreateTime());
            viewOrder.setDormId(order.getDormId());
            viewOrder.setFoodAmount(order.getFoodAmount());
            viewOrder.setIp(order.getIp());
            viewOrder.setOrderAmount(order.getOrderAmount());
            viewOrder.setOrderSn(order.getSn());
            if(null != order.getPayType() && "3.0.0".compareTo(appVersion) <= 0){
                viewOrder.setPayType(6 == order.getPayType()?2:order.getPayType());
            }else {
                viewOrder.setPayType(order.getPayType());
            }
            viewOrder.setType(ORDER_TYPE);
            viewOrder.setSource(order.getSource());
            viewOrder.setUid(order.getUid());
            viewOrder.setCreateTime(order.getCreateTime());
            viewOrder.setCancelTime(order.getCancelTime());
            viewOrder.setPayTime(order.getPayTime());
            viewOrder.setRefundStatusCode(order.getRefundStatusCode());
            viewOrder.setRefundStatusMsg(order.getRefundStatusMsg());
            //订单未支付时提示
            if(null != viewOrder.getStatus() && viewOrder.getStatus().intValue() == OrderStatus.NEW.ordinal()) {
                viewOrder.setTips(TIPS);
            }

            if (null != box) {
                viewOrder.setRoom(box.getRoom());
                viewOrder.setBoxCode(box.getCode());
            }
            if(!StringUtils.isEmpty(order.getCouponCode())){
                DiscountInfo discountInfo = new DiscountInfo();
                discountInfo.setDiscountTitle("优惠券折扣");
                discountInfo.setDiscount(order.getCouponDiscount());
                //总折扣数
                viewOrder.setTotalDiscount(order.getCouponDiscount());
                if(null == viewOrder.getDiscountInfos()){
                    viewOrder.setDiscountInfos(new ArrayList<>());
                }
                viewOrder.getDiscountInfos().add(discountInfo);
            }
            int foodNum = 0;
            if (!CollectionUtils.isEmpty(order.getList())) {
                for (OrderItem orderItem : order.getList()) {
                    ViewOrderItem viewOrderItem = new ViewOrderItem();
                    viewOrderItem.setName(orderItem.getRname());
                    viewOrderItem.setQuantity(orderItem.getQuantity());
                    viewOrderItem.setRid(orderItem.getRid());
                    viewOrderItem.setPrice(orderItem.getPrice());
                    viewOrderItem.setAmount(orderItem.getAmount());
                    viewOrderItem.setImg(orderItem.getImg());
                    if (null == viewOrder.getItems()) {
                        viewOrder.setItems(new ArrayList<>());
                    }
                    viewOrder.getItems().add(viewOrderItem);
                    foodNum += viewOrderItem.getQuantity();
                }
            }
            viewOrder.setFoodNum(foodNum);
        }
        return viewOrder;
    }
}
