package com.store59.box.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.store59.base.common.api.RepoApi;
import com.store59.base.common.model.Repo;
import com.store59.box.canstants.RetCode;
import com.store59.box.logic.UserToken;
import com.store59.box.model.Box;
import com.store59.box.model.BoxItem;
import com.store59.box.util.ResultUtil;
import com.store59.box.viewmodel.BoxCart;
import com.store59.box.viewmodel.BoxCartItem;
import com.store59.box.viewmodel.Result;
import com.store59.coupon.model.Coupon;
import com.store59.coupon.model.query.CouponQuery;
import com.store59.coupon.remoting.CouponService;
import com.store59.dorm.common.api.DormitemApi;
import com.store59.dorm.common.model.Dormitem;
import com.store59.kylin.common.Util;
import com.store59.kylin.common.cache.ICache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhangwangyong on 15/7/21.
 */
@Service
public class BoxCartService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ICache ICacheClient;
    @Autowired
    private RepoApi repoApi;
    @Autowired
    private com.store59.box.remoting.BoxService boxService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private DormitemApi dormitemApi;
    @Value("${spring.redis.prefix}")
    private String redisPre;
    @Value("${box.image.url.pre}")
    private String BOX_IMAGE_PREX;
    @Value("${box.image.url.suf}")
    private String BOX_IMAGE_SUFX;


    private static final int COUPON_BOX_SCOPE = 3;
    private static final String CART_LIST = "cartlist.";
    private static final String USER_COUPON_LIST = "uclist.";
    private static final int REDIS_EXPIRE = 172800;

    /**
     * 根据盒子id获取购物车信息
     * */
    public BoxCart findBoxCartByBoxId(String token,Integer boxId) {
        String boxCartJson = ICacheClient.get(redisPre+token+String.valueOf(boxId));
        if(null == boxCartJson){
            return new BoxCart();
        }
        return Util.getObjectFromJson(boxCartJson, BoxCart.class);
    }

    /**
     * 找到最适合的优惠券
     * @param coupons
     * @return
     */
    private Coupon findOptimal(List<Coupon> coupons) {
        Comparator<Coupon> c = new Comparator<Coupon>() {
            @Override
            public int compare(Coupon o1, Coupon o2) {
                int value = o2.getDiscount().compareTo(o1.getDiscount());
                if (value == 0) {
                    int expireValue = o1.getExpireDate().compareTo(o2.getExpireDate());
                    if(expireValue == 0){
                        int thresholdValue = o2.getThreshold().compareTo(o1.getThreshold());
                        if(thresholdValue == 0){
                            return o2.getScope().compareTo(o1.getScope());
                        }
                        return thresholdValue;
                    }
                    return expireValue;
                }
                return value;
            }
        };
        coupons.sort(c);
        return coupons.size() == 0 ? null : coupons.get(0);
    }

    private List<Coupon> getUserCouponsFromRedis(String token) {
        String userCouponsJson = ICacheClient.get(redisPre + USER_COUPON_LIST + token);
        if (null != userCouponsJson) {
            return Util.getObjectFromJson(userCouponsJson, new TypeReference<ArrayList<Coupon>>() {
            });
        }
        return null;
    }

//    private void putUserCouponsIntoRedis(String token, List<Coupon> coupons) {
//        if (coupons != null) {
//            ICacheClient.setex(redisPre + USER_COUPON_LIST + token, 1800, Util.getJsonFromObject(coupons));
//        }
//    }

    /**
     * 查找用户的所有可用优惠券
     * @param token
     * @return
     */
    private List<Coupon> findUserCoupons(String token) {
//        List<Coupon> userCoupon = getUserCouponsFromRedis(token);
//        if(userCoupon != null && userCoupon.size()>0) {return userCoupon;}
        
        UserToken userToken = tokenService.getUserToken(token);
        if (userToken == null || userToken.getUid() == null)
            return null;

        CouponQuery query = new CouponQuery();
        query.setUid(userToken.getUid());
        query.setStatus(0);
        query.setActiveStatus(1);
        query.setExpireStatus(0);
        query.setScopes(Arrays.asList(new Integer[] { 0, 3 }));
        com.store59.kylin.common.model.Result<List<Coupon>> couponResult = couponService.getCouponList(query);
        if (couponResult.getStatus() == 0 || couponResult.getData() != null) {
//            putUserCouponsIntoRedis(token, couponResult.getData());
            return couponResult.getData();
        } else {
            return new ArrayList<Coupon>();
        }

    }

    /**
     * 找到最适合的优惠券的code
     * @param token
     * @param boxCart
     * @return
     */
    private Coupon getOptimalCouponCode(String token, BoxCart boxCart) {
        List<Coupon> idoneity = new ArrayList<Coupon>();
        List<Coupon> userCoupons = findUserCoupons(token);

        // 找出最优实物优惠券
        List<BoxCartItem> items = boxCart.getItemList();
//        for (BoxCartItem item : items) {
//            Integer rid = item.getRid();
//            for (Coupon coupon : userCoupons) {
//                if (coupon.getDiscountApplyRids() != null && (coupon.getDiscount() <= boxCart.getAmount().doubleValue() || (
//                        coupon.getThreshold() != null && coupon.getThreshold() != 0)) && coupon.getDiscountApplyRids().contains(rid)) {
//
//                    idoneity.add(coupon);
//                }
//            }
//        }

        for(Coupon coupon : userCoupons){
            if(null != coupon.getDiscountApplyRids() && (coupon.getDiscount() <= boxCart.getAmount().doubleValue() || (
                    coupon.getThreshold() != null && coupon.getThreshold() != 0))){
                //  计算购物车中存在优惠券中价格的商品总金额
                BigDecimal amount = BigDecimal.ZERO;
                for (BoxCartItem item : items){
                    if(coupon.getDiscountApplyRids().contains(item.getRid())){
                        amount = amount.add(item.getAmount());
                    }
                }

                //如果不存在商品或者商品总额小于门槛时不可使用该优惠券
                if(amount.intValue() != 0 && amount.doubleValue() >= coupon.getThreshold().doubleValue()){
                    idoneity.add(coupon);
                }
            }
        }

        // 找出最优满减券
        if (idoneity.size() == 0) {
            for (Coupon coupon : userCoupons) {
                if (null == coupon.getDiscountApplyRids() && boxCart.getAmount().doubleValue() >= coupon.getThreshold() && (coupon.getDiscount() <= boxCart.getAmount().doubleValue() || (
                        coupon.getThreshold() != null && coupon.getThreshold() != 0))) {
                    idoneity.add(coupon);
                }
            }
        }

        Coupon optimal = findOptimal(idoneity);
        return optimal;
    }
    
    /**
     * 更新购物车信息
     * */
    public Result updateBoxCart(String token,Integer boxId, Integer rid, Integer quantity) {
        Result result = new Result();

        //参数校验
        if(StringUtils.isEmpty(token)){
            return ResultUtil.genResult(RetCode.TOKEN_EMPTY,"empty token");
        }
        if(quantity.compareTo(0) < 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"商品数量必须大于等于0");
        }

        Box box = boxService.findBoxById(boxId,true).getData();
        if(null == box){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择的盒子不存在");
        }
        //获取产品信息
        com.store59.kylin.common.model.Result<Repo> repoResult = repoApi.findRepoByRid(rid);
        if(null == repoResult.getData()){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择的产品不存在",findBoxCartByBoxId(token,boxId));
        }
        //获取楼主商品信息
        List<Dormitem> dormitems = dormitemApi.getDormitemListByDormId(box.getDormId()).getData();
        if(CollectionUtils.isEmpty(dormitems)){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"楼主未采购该商品");
        }
        //获取该商品的采购价，用于盒子价格计算
        BigDecimal price = BigDecimal.ZERO;
        boolean isExist = false;
        for (Dormitem dormitem : dormitems){
            if(dormitem.getRid().compareTo(rid) == 0){
                price = new BigDecimal(dormitem.getPurchasePrice());
                isExist = true;
            }
        }
        //判断楼主是否采购该商品
        if(!isExist){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"楼主未采购该商品");
        }

        Repo repo = repoResult.getData();

        //获取用户购物车信息
        BoxCart boxCart;
        String boxCartJson = ICacheClient.get(redisPre + token + String.valueOf(boxId));
        if(null == boxCartJson){
            boxCart = new BoxCart();
            boxCart.setBoxId(boxId);
        }else{
            boxCart = Util.getObjectFromJson(boxCartJson, BoxCart.class);
        }
        List<BoxCartItem> list = boxCart.getItemList();
        boolean is_new = true;      //判断是否需要新增

        if(!CollectionUtils.isEmpty(list)){
            for(BoxCartItem boxCartItem : list){
                if(boxCartItem.getRid().compareTo(rid) == 0){
                    BigDecimal amount = price.multiply(new BigDecimal(quantity))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);

                    //更新购物车的总数量和总金额
                    boxCart.setAmount(boxCart.getAmount().add(amount.subtract(boxCartItem.getAmount()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                    boxCart.setFoodAmount(boxCart.getFoodAmount().add(amount.subtract(boxCartItem.getAmount()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                    boxCart.setTotalNum(null == boxCart.getTotalNum()?0:boxCart.getTotalNum()
                            + (quantity - boxCartItem.getQuantity()));

                    boxCartItem.setQuantity(quantity);
                    boxCartItem.setAmount(amount);
                    //如果数量为0时将该数据清除
                    if(quantity.compareTo(0) == 0){
                        list.remove(boxCartItem);
                    }
                    is_new = false;
                    break;
                }
            }
        }

        //新增购物车明细
        if(is_new && (quantity.compareTo(0) != 0)){

            //新增购物车明细信息
            BigDecimal amount = price.multiply(new BigDecimal(quantity))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
            BoxCartItem boxCartItem = new BoxCartItem();
            boxCartItem.setAmount(amount);
            boxCartItem.setQuantity(quantity);
            boxCartItem.setRid(rid);
            boxCartItem.setImg(BOX_IMAGE_PREX + repo.getDefaultImage() + BOX_IMAGE_SUFX);
            boxCartItem.setName(repo.getName());
            boxCartItem.setPrice(price.setScale(2, BigDecimal.ROUND_HALF_UP));

            //更新购物车的总数量和总金额
            if(null == boxCart.getAmount()){
                boxCart.setAmount(BigDecimal.ZERO);
            }
            if(null == boxCart.getFoodAmount()){
                boxCart.setFoodAmount(BigDecimal.ZERO);
            }
            boxCart.setAmount(boxCart.getAmount().add(boxCartItem.getAmount()));
            boxCart.setFoodAmount(boxCart.getFoodAmount().add(boxCartItem.getAmount()));
            boxCart.setTotalNum((null == boxCart.getTotalNum() ? 0 : boxCart.getTotalNum()) + quantity);

            if(null == list){
                list = new ArrayList<>();
            }
            list.add(boxCartItem);
        }

        boxCart.setItemList(list);


        //用户购物车列表更新
        String cartKeysJson = ICacheClient.get(redisPre+CART_LIST+token);
        List<String> cartKeyList = null;
        if(StringUtils.isEmpty(cartKeysJson)){
            cartKeyList = new ArrayList<>();
        }else {
            cartKeyList = Util.getObjectFromJson(cartKeysJson,ArrayList.class);
        }
        if(!cartKeyList.contains(redisPre + token + String.valueOf(boxId))){
            cartKeyList.add(redisPre + token + String.valueOf(boxId));
            ICacheClient.setex(redisPre + CART_LIST + token, REDIS_EXPIRE, Util.getJsonFromObject(cartKeyList));
        }

        ICacheClient.setex(redisPre + token + String.valueOf(boxId), REDIS_EXPIRE, Util.getJsonFromObject(boxCart));
        result.setData(boxCart);
        return result;
    }

    /**
     * 清空购物车信息
     * */
    public BoxCart clearBoxCart(String token,Integer boxId) {
        BoxCart boxCart = null;
        if(null == boxId){
            String cartKeysJson = ICacheClient.get(redisPre+CART_LIST+token);
            if(!StringUtils.isEmpty(cartKeysJson)){
                List<String> cartKeyList = Util.getObjectFromJson(cartKeysJson,ArrayList.class);
                if(!CollectionUtils.isEmpty(cartKeyList)){
                    for(String key : cartKeyList){
                        boxCart = clearBoxCart(key);
                    }
                }
            }
            if(null == boxCart){
                boxCart = clearBoxCart("");
            }
        }else {
            boxCart = clearBoxCart(redisPre + token + String.valueOf(boxId));
        }

        return boxCart;
    }

    private BoxCart clearBoxCart(String key){
        String boxCartJson = ICacheClient.get(key);
        if(null == boxCartJson){
            return new BoxCart();
        }
        BoxCart boxCart = Util.getObjectFromJson(boxCartJson, BoxCart.class);
        boxCart.setAmount(BigDecimal.ZERO);
        boxCart.setFoodAmount(BigDecimal.ZERO);
        boxCart.setCouponCode("");
        boxCart.setTotalNum(0);
        boxCart.setCouponDiscount(0d);
        boxCart.setItemList(new ArrayList<>());
        ICacheClient.setex(key, REDIS_EXPIRE, Util.getJsonFromObject(boxCart));

        return boxCart;
    }

    /**
     * 使用优惠券
     * */
    public Result useCoupon(String token,Integer boxId,String couponCode){
        //获取购物车信息
        String boxCartJson = ICacheClient.get(redisPre + token + String.valueOf(boxId));
        if(null == boxCartJson){
            return ResultUtil.genSuccessResult(new BoxCart());
        }
        BoxCart boxCart = Util.getObjectFromJson(boxCartJson, BoxCart.class);

        Coupon coupon = null;
        if(org.apache.commons.lang3.StringUtils.isEmpty(couponCode)) {
            // 找到最优的优惠券
            coupon = getOptimalCouponCode(token, boxCart);
            if(coupon == null){
                return ResultUtil.genSuccessResult(boxCart);
            }
        }else {
            //获取优惠券信息
            CouponQuery couponQuery = new CouponQuery();
            couponQuery.setCode(couponCode);
            List<Coupon> coupons = couponService.getCouponList(couponQuery).getData();
            if(!CollectionUtils.isEmpty(coupons)){
                coupon = coupons.get(0);
                if (null != coupon.getScope()
                        && (coupon.getScope().intValue() != 0 && coupon.getScope().intValue() != COUPON_BOX_SCOPE)) {
                    return ResultUtil.genResult(RetCode.NORMAL_ERROR, "该优惠券非零食盒子可用优惠券");
                }
                if (null != coupon.getUseUid() || (null != coupon.getUseTime() && coupon.getUseTime().compareTo(0) != 0)
                        || !StringUtils.isEmpty(coupon.getUseOrderId())) {
                    return ResultUtil.genResult(RetCode.NORMAL_ERROR, "该优惠券已被使用");
                }
                if (null != coupon.getActiveDate() && coupon.getActiveDate().compareTo(0l) != 0
                        && coupon.getActiveDate() > todayEnd()) {
                    return ResultUtil.genResult(RetCode.NORMAL_ERROR, "该优惠券还未到启用时间");
                }
                if (null != coupon.getExpireDate() && coupon.getExpireDate().compareTo(0l) != 0
                        && coupon.getExpireDate() < todayStart()) {
                    return ResultUtil.genResult(RetCode.NORMAL_ERROR, "该优惠券已经过期");
                }
            }else {
                return ResultUtil.genResult(RetCode.NORMAL_ERROR, "该优惠券不存在");
            }
        }

        //校验优惠券结果
        if (coupon.getThreshold() != null && coupon.getThreshold().compareTo(null == boxCart.getAmount()?0:boxCart.getAmount().doubleValue()) <= 0) {
            if (!CollectionUtils.isEmpty(coupon.getDiscountApplyRids()) && !CollectionUtils.isEmpty(boxCart.getItemList())) {
                //使用优惠券逻辑，优惠券优惠的金额与优惠券对应商品的总金额比较，取其中数值小的数据
                boolean beUsed = false;
                BigDecimal amount = new BigDecimal("0");
                for (BoxCartItem boxCartItem : boxCart.getItemList()) {
                    for (Integer applyRid : coupon.getDiscountApplyRids()) {
                        if (applyRid.compareTo(boxCartItem.getRid()) == 0) {
                            amount = amount.add(boxCartItem.getPrice().multiply(new BigDecimal(boxCartItem.getQuantity())));
                            beUsed = true;
                        }
                    }
                }
                if (beUsed) {
                    if(amount.intValue() < coupon.getThreshold()){
                        return ResultUtil.genResult(RetCode.NORMAL_ERROR, "不满足优惠券使用条件");
                    }
                    useCoupon(boxCart, coupon, amount);
                }
                if (!StringUtils.isEmpty(couponCode) && !beUsed) {
                    return ResultUtil.genResult(RetCode.NORMAL_ERROR, "不满足优惠券使用条件");
                }
            } else {
                useCoupon(boxCart, coupon, null);
            }
        } else {
            if(!StringUtils.isEmpty(couponCode)) {
                return ResultUtil.genResult(RetCode.NORMAL_ERROR, "不满足优惠券使用条件");
            }
        }

        return ResultUtil.genSuccessResult(boxCart);
    }

    /**
     * 当天时间的结束时间
     * */
    private Long todayEnd(){
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY,23);
        todayEnd.set(Calendar.MINUTE,59);
        todayEnd.set(Calendar.SECOND,59);
        return todayEnd.getTime().getTime()/1000;
    }

    /**
     * 当天时间的开始时间
     * */
    private Long todayStart(){
        Calendar todayStrat = Calendar.getInstance();
        todayStrat.set(Calendar.HOUR_OF_DAY, 0);
        todayStrat.set(Calendar.MINUTE, 0);
        todayStrat.set(Calendar.SECOND, 0);
        return todayStrat.getTime().getTime()/1000;
    }

    /**
     * 根据优惠券更新购物车信息
     * */
    private void useCoupon(BoxCart boxCart,Coupon coupon,BigDecimal amount){
        boxCart.setCouponCode(coupon.getCode());
        if(amount == null || coupon.getDiscount() < amount.doubleValue()) {
            boxCart.setCouponDiscount(coupon.getDiscount());
            boxCart.setAmount(boxCart.getFoodAmount().subtract(new BigDecimal(coupon.getDiscount())));
        }else {
            boxCart.setCouponDiscount(amount.doubleValue());
            boxCart.setAmount(boxCart.getFoodAmount().subtract(amount));
        }

        if(boxCart.getAmount().compareTo(BigDecimal.ZERO) < 0){
            boxCart.setAmount(BigDecimal.ZERO);
            boxCart.setCouponDiscount(boxCart.getFoodAmount().doubleValue());
        }
    }
}
