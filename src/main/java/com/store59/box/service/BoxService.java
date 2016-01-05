package com.store59.box.service;

import com.store59.base.common.api.*;
import com.store59.base.common.model.*;
import com.store59.base.common.model.City;
import com.store59.base.common.model.Dormentry;
import com.store59.base.common.model.Site;
import com.store59.box.canstants.*;
import com.store59.box.model.Box;
import com.store59.box.model.BoxItem;
import com.store59.box.model.DislikeRepo;
import com.store59.box.model.distribution.DistributionRecord;
import com.store59.box.model.distribution.DistributionRecordAddRequest;
import com.store59.box.model.query.BoxQuery;
import com.store59.box.model.query.DistributionRecordQuery;
import com.store59.box.remoting.DislikeRepoService;
import com.store59.box.remoting.DistributionRecordService;
import com.store59.box.util.ResultUtil;
import com.store59.box.viewmodel.*;
import com.store59.box.viewmodel.Result;
import com.store59.dorm.common.api.DormitemApi;
import com.store59.dorm.common.model.Dormitem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhangwangyong on 15/7/29.
 */
@Service
public class BoxService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private com.store59.box.remoting.BoxService boxServiceApi;
    @Autowired
    private DormentryApi dormentryApi;
    @Autowired
    private SiteApi siteApi;
    @Autowired
    private ZoneApi zoneApi;
    @Autowired
    private CityApi cityApi;
    @Autowired
    private RepoApi repoApi;
    @Autowired
    private DormitemApi dormitemApi;
    @Autowired
    private DislikeRepoService dislikeRepoService;
    @Autowired
    private DistributionRecordService distributionRecordService;

    @Value("${box.image.url.pre}")
    private String BOX_IMAGE_PREX;
    @Value("${box.image.url.suf}")
    private String BOX_IMAGE_SUFX;
    @Value("${box.item.limit}")
    private Integer BOX_ITEM_LIMIT;

    private final int REPLENISHING_STATUS = 1;
    private final int REPO_NOSALE_STATUS = 1;
    private final int REPO_YEMAO_TYPE = 0;

    /**
     * 根据盒子id获取盒子信息
     * */
    public Result findBoxById(Integer boxId) {
        com.store59.kylin.common.model.Result serviceResult = boxServiceApi.findBoxById(boxId, true);
        Result result = ResultUtil.resultService2View(serviceResult);
        DistributionRecordQuery distributionRecordQuery = new DistributionRecordQuery();
        distributionRecordQuery.setBoxId(boxId);
        distributionRecordQuery.setDistributionRecordStatus(DistributionRecordStatus.UNDELIVERED);
        com.store59.kylin.common.model.Result<List<DistributionRecord>> distributionRecordList =
                distributionRecordService.findDistributionRecordList(distributionRecordQuery);
        List<DistributionRecord> distributionRecords = distributionRecordList.getData();
        if(null != serviceResult && null != serviceResult.getData()){
            ViewBox viewBox = changeServiceBox2ViewBox((Box) serviceResult.getData());
            if(!CollectionUtils.isEmpty(viewBox.getItems()) && !CollectionUtils.isEmpty(distributionRecords)){
                for(ViewBoxItem viewBoxItem : viewBox.getItems()){
                    if(viewBoxItem.getStock().compareTo(0) == 0){
                        for(DistributionRecord distributionRecord : distributionRecords){
                            if(viewBoxItem.getRid().compareTo(distributionRecord.getRid()) == 0){
                                //如果是补货中提示正在补货中
                                viewBoxItem.setReplenishing(REPLENISHING_STATUS);
                            }
                        }
                    }
                }
            }
            //限制数量变更为楼主商品数量
            com.store59.kylin.common.model.Result<List<Dormitem>> repoResult = dormitemApi.getDormitemListByDormId(((Box) serviceResult.getData()).getDormId());
            if(repoResult != null && null != repoResult.getData()){
                viewBox.setCapacity(repoResult.getData().size());
            }else {
                viewBox.setCapacity(BOX_ITEM_LIMIT);
            }
            result.setData(viewBox);
        }
        return result;
    }

    /**
     * 根据楼栋id和宿舍号获取盒子列表
     * */
    public Result findBoxList(Integer dormentryId, String room) {
        Map<String,List<ViewBox>> map = new HashMap<>();
        map.put("boxes", null);

        com.store59.kylin.common.model.Result serviceResult = boxServiceApi.findBoxList(dormentryId, room);
        Result result = ResultUtil.resultService2View(serviceResult);

        if(null != serviceResult && null != serviceResult.getData()){
            List<Box> boxList = (List<Box>)serviceResult.getData();
            boxList.parallelStream().sorted((p1,p2) -> p1.getCode().compareTo(p2.getCode())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(boxList)){
                List<ViewBox> viewBoxList = new ArrayList<>();
                for(Box box : boxList){
                    viewBoxList.add(changeServiceBox2ViewBox(box));
                }

                map.put("boxes", viewBoxList);
            }
        }
        result.setData(map);
        return result;
    }

    /**
     * 根据楼栋id获取盒子列表
     * */
    public Result findBoxList(Integer dormentryId) {
        com.store59.kylin.common.model.Result serviceResult = boxServiceApi.findBoxList(dormentryId,null);
        Result result = ResultUtil.resultService2View(serviceResult);
        if(null != serviceResult && null != serviceResult.getData()){
            List<Box> boxList = (List<Box>)serviceResult.getData();

            if(!CollectionUtils.isEmpty(boxList)){
                List<ViewBox> viewBoxList = new ArrayList<>();
                for(Box box : boxList){
                    viewBoxList.add(changeServiceBox2ViewBox(box));
                }
                result.setData(viewBoxList);
            }
        }
        return result;
    }

    /**
     * 方法的功能描述：获取登录人盒子信息
     *
     * @param   uid     用户id
     * @return  Result
     * @author  zhangwangyong
     * */
    public Result getLoginUserBox(Long uid){
        if(uid.compareTo(0l) == 0){
            return ResultUtil.genSuccessResult(null);
        }
        BoxQuery boxQuery = new BoxQuery();
        boxQuery.setUid(uid);
        com.store59.kylin.common.model.Result<List<Box>> commonResult = boxServiceApi.findBoxList(boxQuery,false);

        if(null == commonResult || CollectionUtils.isEmpty(commonResult.getData())){
            return ResultUtil.genSuccessResult(null);
        }

        //取出正常使用的盒子
        Box box = null;
        for(Box commonbox : commonResult.getData()){
            if(commonbox.getBoxStatus().ordinal() == BoxStatus.APPROVED.ordinal()){
                box = commonbox;
                break;
            }
        }
        if (box == null) {
            return ResultUtil.genSuccessResult(null);
        }

        LoginUserBox loginUserBox = new LoginUserBox();
        UserBox userBox = new UserBox();
        userBox.setBoxId(box.getId());
        userBox.setRoom(box.getRoom());
        userBox.setCode(box.getCode());
        userBox.setStatus(box.getBoxStatus().ordinal());
        loginUserBox.setUserBox(userBox);
        //获取楼栋信息
        com.store59.kylin.common.model.Result<Dormentry> dormentryResult = dormentryApi.getDormentry(box.getDormentryId());
        Dormentry dormentry = dormentryResult.getData();
        if(null == dormentry ){
            return  ResultUtil.genSuccessResult(loginUserBox);
        }
        com.store59.box.viewmodel.Dormentry dormentry1 = new com.store59.box.viewmodel.Dormentry();
        dormentry1.setDormentryId(dormentry.getDormentryId());
        dormentry1.setAddress(dormentry.getAddress2() + " " + dormentry.getAddress3());
        dormentry1.setFloorAddress(dormentry.getAddress3());
        dormentry1.setStatus(dormentry.getStatus());
        loginUserBox.setDormentry(dormentry1);
        Building building = new Building();
        building.setBuildingName(dormentry.getAddress2());
        loginUserBox.setBuilding(building);
        loginUserBox.setGroupsName(dormentry.getAddress1());
        //获取站点信息
        com.store59.kylin.common.model.Result<Site> siteResult = siteApi.getSite(dormentry.getSiteId());
        Site site = siteResult.getData();
        if(null == site){
            return ResultUtil.genSuccessResult(loginUserBox);
        }
        com.store59.box.viewmodel.Site site1 = new com.store59.box.viewmodel.Site();
        site1.setSiteId(site.getSiteId());
        site1.setSite(site.getSiteName());
        loginUserBox.setSite(site1);
        //获取城区信息
        com.store59.kylin.common.model.Result<Zone> zoneResult =  zoneApi.getZone(site.getZoneId());
        Zone zone = zoneResult.getData();
        if(null == zone){
            return ResultUtil.genSuccessResult(loginUserBox);
        }
        //获取城市信息
        com.store59.kylin.common.model.Result<City> cityResult = cityApi.getCity(zone.getCityId());
        City city = cityResult.getData();
        if(null == city){
            return ResultUtil.genSuccessResult(loginUserBox);
        }
        com.store59.box.viewmodel.City city1 = new com.store59.box.viewmodel.City();
        city1.setCityId(city.getCityId());
        city1.setCity(city.getName());
        loginUserBox.setCity(city1);
        return ResultUtil.genSuccessResult(loginUserBox);
    }

    /**
     * 方法的功能描述：获取可新增商品列表
     * 
     * @param       boxId   盒子id
     *              uid     用户id
     * @return      Result
     * @author  zhangwangyong
     * */
    public Result getExcludeItems(Integer boxId,Long uid){
        com.store59.kylin.common.model.Result<Box> boxResult = boxServiceApi.findBoxById(boxId, true);
        if(null == boxResult && null == boxResult.getData()){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择的盒子不存在");
        }

        if(boxResult.getData().getUid().compareTo(uid) != 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"您操作的不是属于您的盒子");
        }
        ExcludeRepoItems excludeRepoItems = new ExcludeRepoItems();
        BoxQuery boxQuery = new BoxQuery();
        boxQuery.setBoxId(boxId);
        com.store59.kylin.common.model.Result<List<Box>> commonResult = boxServiceApi.findBoxList(boxQuery, true);
        if(null != commonResult && !CollectionUtils.isEmpty(commonResult.getData())){
            Box box = commonResult.getData().get(0);
            excludeRepoItems.setBoxId(box.getId());
            excludeRepoItems.setCode(box.getCode());
            excludeRepoItems.setOwner(box.getOwner());
            excludeRepoItems.setRoom(box.getRoom());
            excludeRepoItems.setStatus(box.getBoxStatus().ordinal());
            //可选商品数量
            excludeRepoItems.setCapacityLeft(CollectionUtils.isEmpty(box.getItemList()) ? BOX_ITEM_LIMIT
                    : BOX_ITEM_LIMIT - box.getItemList().size());
            com.store59.kylin.common.model.Result<List<Dormitem>> repoResult = dormitemApi.getDormitemListByDormId(box.getDormId());
            if(null != repoResult && !CollectionUtils.isEmpty(repoResult.getData())){
                List<ExcludeRepoItem> excludeRepoItemList = new ArrayList<>();
                List<Dormitem> list = repoResult.getData();
                int subnum  = 0;

                List<BoxItem> boxItemList = box.getItemList();
                //商品排序
                int order = 0;
                dormLoop:for(Dormitem dormitem : list){
                    if(!CollectionUtils.isEmpty(boxItemList)){
                        for (BoxItem boxItem : boxItemList){
                            if(dormitem.getRid().compareTo(boxItem.getRid()) == 0){
                                continue dormLoop;
                            }
                        }
                    }

                    com.store59.kylin.common.model.Result<Repo> result = repoApi.findRepoByRid(dormitem.getRid());
                    if(null != result) {
                        Repo repo = result.getData();

                        if (null != repo && REPO_NOSALE_STATUS != repo.getNoSale() && REPO_YEMAO_TYPE == repo.getCateType()) {
                            ExcludeRepoItem excludeRepoItem = new ExcludeRepoItem();
                            excludeRepoItem.setImg(BOX_IMAGE_PREX + repo.getDefaultImage() + BOX_IMAGE_SUFX);
                            excludeRepoItem.setName(repo.getName());
                            //商品是否为新品
                            excludeRepoItem.setNewRepo(repo.getIsNew());
                            excludeRepoItem.setPrice(dormitem.getPurchasePrice());
                            excludeRepoItem.setRid(dormitem.getRid());
                            excludeRepoItem.setOrder(order++);
                            excludeRepoItemList.add(excludeRepoItem);
                        }else {
                            subnum += 1;
                        }
                    }

                }
                //可选商品数量
                excludeRepoItems.setCapacityLeft(CollectionUtils.isEmpty(box.getItemList()) ? list.size()-subnum
                        : list.size()-subnum - box.getItemList().size());
                excludeRepoItems.setItems(excludeRepoItemList);
            }
        }
        return ResultUtil.genSuccessResult(excludeRepoItems);
    }

    /**
     * 方法的功能描述：盒子新增商品明晰
     * 
     * @param       boxId,rids,uid  盒子id，商品id列表，用户id
     * @return      Result
     * @author  zhangwangyong
     * */
    public Result itemAdd(Integer boxId,List<Integer> rids,Long uid){
        com.store59.kylin.common.model.Result<Box> boxResult = boxServiceApi.findBoxById(boxId, true);
        if(null == boxResult && null == boxResult.getData()){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择的盒子不存在");
        }

        if(boxResult.getData().getUid().compareTo(uid) != 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"您操作的不是属于您的盒子");
        }

        List<Dormitem> dormitems = dormitemApi.getDormitemListByDormId(boxResult.getData().getDormId()).getData();
        if(CollectionUtils.isEmpty(dormitems)){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"您选择的商品楼主未采购");
        }

        if(!CollectionUtils.isEmpty(rids)){
            List<Repo> repos = repoApi.findRepoListByIds(rids).getData();
            for(BoxItem boxItem : boxResult.getData().getItemList()){
                for (Integer rid : rids){
                    if(rid.compareTo(boxItem.getRid()) == 0){
                        for (Repo repo : repos){
                            if(rid.compareTo(repo.getRid()) == 0){
                                return ResultUtil.genResult(RetCode.NORMAL_ERROR,"商品"+repo.getName()+"盒子中已存在");
                            }
                        }
                    }
                }
            }
            List<BoxItem> boxItemList = new ArrayList<>();
            List<DistributionRecordAddRequest> distributionRecordAddRequests = new ArrayList<>();

            for (Integer rid : rids){
                BoxItem boxItem = new BoxItem();
                boxItem.setBoxId(boxId);
                boxItem.setRid(rid);
                for (Dormitem dormitem : dormitems){
                    if(dormitem.getRid().compareTo(rid) == 0){
                        boxItem.setPrice(dormitem.getPurchasePrice());
                    }
                }
                boxItem.setStock(0);
                boxItemList.add(boxItem);

                DistributionRecordAddRequest distributionRecordAddRequest = new DistributionRecordAddRequest();
                distributionRecordAddRequest.setRid(rid);
                distributionRecordAddRequest.setBoxId(boxId);
                distributionRecordAddRequest.setDistributionRecordAddBy(DistributionRecordAddBy.OWNER);
                distributionRecordAddRequests.add(distributionRecordAddRequest);
            }
            //新增盒子明细
            boxServiceApi.addBoxItemList(boxItemList);
            //新增补货申请
            distributionRecordService.addDistributionRecordList(distributionRecordAddRequests);
            //删除黑名单中的商品
            dislikeRepoService.removeDislikeRepo(uid,rids);
        }

        return ResultUtil.genSuccessResult(null);
    }

    /**
     * 方法的功能描述：删除盒子明晰
     * 
     * @param       boxId,rid,uid   盒子id，商品id，用户id
     * @return      Result
     * @author  zhangwangyong
     * */
    public Result itemRemove(Integer boxId,Integer rid,Long uid){
        com.store59.kylin.common.model.Result<Box> boxResult = boxServiceApi.findBoxById(boxId, true);
        if(null == boxResult && null == boxResult.getData()){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择的盒子不存在");
        }

        if(boxResult.getData().getUid().compareTo(uid) != 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"您操作的不是属于您的盒子");
        }
        Box box = boxResult.getData();
        List<BoxItem> boxItemList = box.getItemList();
        if(!CollectionUtils.isEmpty(boxItemList)){
            for (BoxItem boxItem : boxItemList){
                if(boxItem.getRid().compareTo(rid) == 0){
                    //将商品丛盒子中下架
                    boxServiceApi.updateBoxItemStatus(boxId, boxItem.getId(), BoxItemStatus.USER_OFFLINE);
                    //将商品加入不喜欢食品列表
                    DislikeRepo dislikeRepo = new DislikeRepo();
                    dislikeRepo.setRid(rid);
                    dislikeRepo.setUid(uid);
                    dislikeRepoService.addDislikeRepo(dislikeRepo);
                }
            }
        }
        return ResultUtil.genSuccessResult(null);
    }

    /**
     * 方法的功能描述：盒子商品补货
     *
     * @param       boxId,rid,uid
     * @return      Result
     * @author  zhangwangyong
     * */
    public Result itemReplenish(Integer boxId,Integer rid,Long uid){
        com.store59.kylin.common.model.Result<Box> boxResult = boxServiceApi.findBoxById(boxId, true);
        if(null == boxResult && null == boxResult.getData()){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择的盒子不存在");
        }

        if(boxResult.getData().getUid().compareTo(uid) != 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"您操作的不是属于您的盒子");
        }
        //判断申请到商品是否已经申请过了
        DistributionRecordQuery distributionRecordQuery = new DistributionRecordQuery();
        distributionRecordQuery.setBoxId(boxId);
        distributionRecordQuery.setDistributionRecordStatus(DistributionRecordStatus.UNDELIVERED);
        com.store59.kylin.common.model.Result<List<DistributionRecord>> distributionRecordList =
                distributionRecordService.findDistributionRecordList(distributionRecordQuery);
        List<DistributionRecord> distributionRecords = distributionRecordList.getData();
        if(!CollectionUtils.isEmpty(distributionRecords)){
            for (DistributionRecord distributionRecord : distributionRecords){
                if(distributionRecord.getRid().compareTo(rid) == 0){
                    return  ResultUtil.genResult(RetCode.NORMAL_ERROR,"选择到商品已经申请补货");
                }
            }
        }
        //删除黑名单中的商品
        dislikeRepoService.removeDislikeRepo(uid,rid);

        //发起补货申请
        DistributionRecordAddRequest distributionRecordAddRequest = new DistributionRecordAddRequest();
        distributionRecordAddRequest.setRid(rid);
        distributionRecordAddRequest.setBoxId(boxId);
        distributionRecordAddRequest.setDistributionRecordAddBy(DistributionRecordAddBy.OWNER);
        distributionRecordService.addDistributionRecord(distributionRecordAddRequest);
        return ResultUtil.genSuccessResult(null);
    }

    /**
     * service的盒子信息转变为显示端端的盒子信息
     * */
    private ViewBox changeServiceBox2ViewBox(Box box){
        ViewBox viewBox = new ViewBox();
        viewBox.setStatus(box.getBoxStatus().ordinal());
        viewBox.setBoxId(box.getId());
        viewBox.setCode(box.getCode());
        viewBox.setOwner(box.getOwner());
        viewBox.setRoom(box.getRoom());

        if(!CollectionUtils.isEmpty(box.getItemList())){
            int i=0;
            List<BoxItem> boxItemList = box.getItemList();
            //对盒子明细按rid排序
            boxItemList = boxItemList.parallelStream().sorted((p1,p2) -> p1.getId().compareTo(p2.getId())).collect(Collectors.toList());
            //获取楼主的商品列表
            List<Dormitem> dormitems = dormitemApi.getDormitemListByDormId(box.getDormId()).getData();
            box.setItemList(boxItemList);
            for(BoxItem boxItem : box.getItemList()){
                ViewBoxItem viewBoxItem = new ViewBoxItem();
                viewBoxItem.setItemId(boxItem.getId());
                viewBoxItem.setRid(boxItem.getRid());
                //价格使用楼主采购价
                if(!CollectionUtils.isEmpty(dormitems)){
                    for (Dormitem dormitem : dormitems){
                        if(dormitem.getRid().compareTo(boxItem.getRid()) == 0){
                            viewBoxItem.setPrice(dormitem.getPurchasePrice());
                        }
                    }
                }
                viewBoxItem.setStock(boxItem.getStock());
                viewBoxItem.setOrder(i++);
                if(null != boxItem.getRepo()){
                    viewBoxItem.setImg(boxItem.getRepo().getDefaultImage());
                    viewBoxItem.setName(boxItem.getRepo().getName());
                }
                if(null == viewBox.getItems()) {
                    viewBox.setItems(new ArrayList<ViewBoxItem>());
                }
                viewBox.getItems().add(viewBoxItem);
            }
        }
        return viewBox;
    }

}
