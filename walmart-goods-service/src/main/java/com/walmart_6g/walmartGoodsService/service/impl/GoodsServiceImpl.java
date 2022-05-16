package com.walmart_6g.walmartGoodsService.service.impl;

import com.alibaba.fastjson.JSON;
import com.walmart_6g.walmartGoodsService.entity.Goods;
import com.walmart_6g.walmartGoodsService.entity.GoodsCategory;
import com.walmart_6g.walmartGoodsService.entity.GoodsDetail;
import com.walmart_6g.walmartGoodsService.entity.Store;
import com.walmart_6g.walmartGoodsService.feignInterface.IUserService;
import com.walmart_6g.walmartGoodsService.mapper.GoodsCategoryRepository;
import com.walmart_6g.walmartGoodsService.mapper.GoodsMapper;
import com.walmart_6g.walmartGoodsService.mapper.StoreMapper;
import com.walmart_6g.walmartGoodsService.service.GoodsService;
import entity.Response;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsCategoryRepository goodsCategoryRepository;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private IUserService iUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsServiceImpl.class);

    Boolean CheckUser(HttpServletRequest request, String user_id) {
        LOGGER.info("[{}]开始运行","CheckUser");
        String userId = request.getHeader("userId");
        String role = request.getHeader("role");
        if(userId == null || role == null) {
            return false;
        }
        Integer roleValue = Integer.parseInt(role);
        return (roleValue == User.admin_role || (roleValue == User.seller_role && userId.equals(user_id)));
    }

    @Override
    public ResponseEntity<Response> addGoods(HttpServletRequest request, Goods goods){
        LOGGER.info("[{}]开始运行","addGoods");
        if(!goods.CheckValid()) {
            return ResponseEntity.status(400).body(new Response<>(400, "name and storeId can not be null", null));
        }
        if(CheckUser(request, goods.getUserId())){
            goods.setDeprecated(false);
            try {
                goodsMapper.save(goods);
                return ResponseEntity.ok(new Response<>(200, "SUCCESS", goods));
            } catch (Exception e) {
                LOGGER.error("add goods failed: "  + e.toString());
                return ResponseEntity.status(400).body(new Response<>(400, e.toString(), null));
            }
        }else{
            return ResponseEntity.status(403).body(new Response<>(403, "Not authorized", null));
        }
    }

    @Override
    public ResponseEntity<Response> deleteGoods(HttpServletRequest request, String id) {
        LOGGER.info("[{}]开始运行","deleteGoods");
        Goods goods = goodsMapper.findById(id);
        if(goods==null){
            return ResponseEntity.status(404).body(new Response<>(404, "No goods", null));
        }
        // 2.判断是否有商品分类<=1,否则不允许删除
        ArrayList<GoodsCategory> goodsCategories = goodsCategoryRepository.getAllByGoodsId(id);
        if(goodsCategories.size()>1){
            return ResponseEntity.status(400).body(new Response<>(400, "Not allowed to delete goods when category >1",null));
        }
        if(CheckUser(request, goods.getUserId())){
            try {
                if(!goodsCategories.isEmpty())
                    goodsCategoryRepository.delete(goodsCategories.get(0));
                goods.setDeprecated(true);
                goodsMapper.save(goods);
                return ResponseEntity.ok(new Response<>(200, "DELETE GOODS SUCCESS", null));
            } catch (Exception e) {
                LOGGER.error("delete goods failed: "  + e.toString());
                return ResponseEntity.status(409).body(new Response<>(409, e.getMessage(), null));
            }
        } else{
            return ResponseEntity.status(403).body(new Response<>(403, "not authorized", null));
        }
    }

    @Override
    public ResponseEntity<Response> getGoodsByID(String id) {
        LOGGER.info("[{}]开始运行","getGoodsByID");
        Goods goods = goodsMapper.findById(id);
        if (goods == null ) {
            return ResponseEntity.status(404).body(new Response<>(404, "No goods", null));
        } else {
            return ResponseEntity.ok(new Response<>(200, "SUCCESS",goods));
        }
    }

    @Override
    public ResponseEntity<Response> updateGoods(HttpServletRequest request, String id, Goods goods) {
        LOGGER.info("[{}]开始运行","updateGoods");
        Goods oldInfo = goodsMapper.findById(id);
        if(oldInfo==null){
            return ResponseEntity.status(404).body(new Response<>(404, "Goods not found", null));
        }
        if(CheckUser(request, oldInfo.getUserId())) {
            if(goods.getName() != null) {
                oldInfo.setName(goods.getName());
            }
            if(goods.getDescription() != null) {
                oldInfo.setDescription(goods.getDescription());
            }
            if(goods.getImage() != null) {
                oldInfo.setImage(goods.getImage());
            }
            goodsMapper.save(oldInfo);
            return ResponseEntity.ok(new Response<>(200, "Modify success", null));
        } else{
            return ResponseEntity.status(403).body(new Response<>(403, "not authorized", null));
        }
    }


    public ResponseEntity<Response> queryGoods(String userId, String storeId, String name, Integer page, Integer pageNum)
    {
        LOGGER.info("[{}]开始运行","queryGoods");
        ArrayList<Goods> goodsList;
        Integer goodsNum = goodsMapper.findByNameNum(name,userId,storeId);  /*商品总数*/
        Integer totalPage = (int) Math.ceil(goodsNum.doubleValue() / pageNum) ;   /*总页数*/
        Map data = new HashMap();
        if(goodsNum!=0&&(page<=0||page>totalPage))
            return ResponseEntity.status(400).body(new Response<>(400, "Page number should be in the range of 1 to " + totalPage, null ));
        goodsList = goodsMapper.findByName(name, userId, storeId, (page-1)*pageNum,pageNum);

        ArrayList<String> storeIds = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();
        for(Goods g: goodsList){
            storeIds.add(g.getStoreId());
            userIds.add(g.getUserId());
        }
        //storeIds is gotten
        List<Store> stores = storeMapper.findByIds(storeIds);
        HashMap<String,List<String>> tempuserids = new HashMap();
        tempuserids.put("userIds",userIds);
        ResponseEntity<Response> usersResponse = iUserService.getUsersByIdBatch(tempuserids);  //get user name
        System.out.println(usersResponse);
        List<User> users = (ArrayList<User>) usersResponse.getBody().getData();
        System.out.println(users);
        if(users == null)
        {
            return ResponseEntity.ok(new Response<>(200, "SUCCESS",data));
        }
        HashMap<String, String> smp = new HashMap();
        HashMap<String,String> uname = new HashMap();
        for(Store s: stores){
            smp.put(s.getId(), s.getName());
        }
        for(int i=0;i<users.size();i++)
        {
            User u = JSON.parseObject(JSON.toJSONString(users.get(i)), User.class);
            uname.put(u.getId(),u.getName());
        }
        for(Goods g: goodsList){
            g.setStoreName(smp.get(g.getStoreId()));  //set store name
            g.setUserName(uname.get(g.getUserId()));
        }
        data.put("goods",goodsList);
        data.put("totalPage",totalPage);
        data.put("totalNumOfGoods",goodsNum);
        System.out.println(data);
        return ResponseEntity.ok(new Response<>(200, "SUCCESS", data));
    }

    public ResponseEntity<Response> addGoodsCategory(HttpServletRequest request, GoodsCategory goodsCategory){
        LOGGER.info("[{}]开始运行","addGoodsCategory");
        // 1. 判断商品是否存在
        if(!goodsCategory.CheckValid()) {
            return ResponseEntity.status(400).body(new Response(400, "name goodsId price can not be null, price can not be less than 0", null));
        }
        Goods goods = goodsMapper.findById(goodsCategory.getGoodsId());
        if(goods == null){
            return ResponseEntity.status(404).body(new Response(404, "No goods", null));
        }
        // 2. 判断卖家是否给自己的商品创建商品类别
        if(!CheckUser(request, goods.getUserId())){
            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
        }
        // 3. 判断goodsId, name是否重复，重复不允许创建
        Optional<GoodsCategory> goodsCategory1 = goodsCategoryRepository.getGoodsCategoryByNameAndGoodsId(goodsCategory.getName(), goodsCategory.getGoodsId());
        if(goodsCategory1.isPresent()){
            return ResponseEntity.status(409).body(new Response(409, "The goods already exists this category, please change another name!", null));
        }
        try {
            goodsCategoryRepository.save(goodsCategory);
            return ResponseEntity.status(200).body(new Response(200, "success", goodsCategory));
        }catch (Exception e){
            LOGGER.error("add goods category failed: "  + e.toString());
            return ResponseEntity.status(400).body(new Response<>(400, e.getMessage(), null));
        }
    }

    public ResponseEntity<Response> deleteGoodsCategory(HttpServletRequest request, String categoryId){
        LOGGER.info("[{}]开始运行","deleteGoodsCategory");
        // 1. 判断类别是否存在
        Optional<GoodsCategory> goodsCategory = goodsCategoryRepository.findById(categoryId);
        if(!goodsCategory.isPresent()){
            return ResponseEntity.status(404).body(new Response(404, "The category doesn't exist", null));
        }
        Goods goods = goodsMapper.findById(goodsCategory.get().getGoodsId());
        if(goods == null){
            return ResponseEntity.status(404).body(new Response(404, "goods doesn't exist", null));
        }
        // 2. 判断该商家是否有权限删除该类别
        if(!CheckUser(request, goods.getUserId())){
            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
        }
        // 3. 判断是否为最后一个类别
        ArrayList<GoodsCategory> goodsCategoryList = goodsCategoryRepository.getGoodsCategoryByGoodsId(goodsCategory.get().getGoodsId());
        if(goodsCategoryList.size()>1){
            try {
                goodsCategoryRepository.delete(goodsCategory.get());
                return ResponseEntity.ok(new Response<>(200, "Delete category success", null));
            } catch (Exception e) {
                LOGGER.error("delete goods category failed: "  + e.toString());
                return ResponseEntity.status(409).body(new Response<>(409, e.getMessage(), null));
            }
        }else{
            return ResponseEntity.status(400).body(new Response(400, "Not allowed to delete the last category", null));
        }
    }

    public ResponseEntity<Response> updateGoodsCategory(HttpServletRequest request, String categoryId, GoodsCategory goodsCategory){
        LOGGER.info("[{}]开始运行","updateGoodsCategory");
        // 1. 判断类别是否存在
        Optional<GoodsCategory> oldGoodsCategory = goodsCategoryRepository.findById(categoryId);
        if(!oldGoodsCategory.isPresent()){
            return ResponseEntity.status(404).body(new Response(404, "The category doesn't exist", null));
        }
        // 2. 判断该商家是否有权限修改该类别

        // TODO: since order may invoke this method, do not check user now
        // TODO: Need a workaround
//        Goods goods = goodsMapper.findById(oldGoodsCategory.get().getGoodsId());
//        if(goods == null){
//            return ResponseEntity.status(404).body(new Response(404, "goods doesn't exist", null));
//        }
//        if(!CheckUser(request, goods.getUserId())){
//            return ResponseEntity.status(403).body(new Response(403, "Forbiden", null));
//        }

        try{
            GoodsCategory oldGC = oldGoodsCategory.get();
            if(goodsCategory.getNum()!=null)
                oldGC.setNum(goodsCategory.getNum());
            if(goodsCategory.getPrice()!=null)
                oldGC.setPrice(goodsCategory.getPrice());
            if(goodsCategory.getName()!=null)
                oldGC.setName(goodsCategory.getName());
            goodsCategoryRepository.save(oldGC);
            return ResponseEntity.status(200).body(new Response(200, "Update category success", oldGC));
        }catch (Exception e){
            LOGGER.error("update goods category failed: "  + e.toString());
            return  ResponseEntity.status(400).body(new Response(400, e.getMessage(), null));
        }
    }

    public ResponseEntity<Response> getGoodsCategoryById(String categoryId){
        try{
            LOGGER.info("[{}]开始运行","getGoodsCategoryById");
            GoodsCategory goodsCategory = goodsCategoryRepository.getById(categoryId);
            if(goodsCategory == null){
                return  ResponseEntity.status(404).body(new Response(404, "category not exist", null));
            }
            return ResponseEntity.status(200).body(new Response(200, "Get category success", goodsCategory));
        }catch (Exception e){
            LOGGER.error("get goods category by id failed: "  + e.toString());
            return  ResponseEntity.status(404).body(new Response(404, e.getMessage(), null));
        }
    }

    public ResponseEntity<Response>getGoodsDetailByCategoryId(String categoryId) {
        try {
            LOGGER.info("[{}]开始运行","getGoodsDetailByCategoryId");
            GoodsCategory goodsCategory = goodsCategoryRepository.getById(categoryId);
            if (goodsCategory == null) {
                return ResponseEntity.status(404).body(new Response(404, "category not exist", null));
            }
            Goods goods = goodsMapper.findById(goodsCategory.getGoodsId());
            if (goods == null) {
                return ResponseEntity.status(404).body(new Response(404, "goods not exist", null));
            }
            Store store = storeMapper.findById(goods.getStoreId());
            if(store == null ){
                return ResponseEntity.status(404).body(new Response(404, "store not exist", null));
            }
            GoodsDetail gd = new GoodsDetail(categoryId, goodsCategory.getName(), goodsCategory.getNum(), goodsCategory.getPrice(),
                    goods.getId(), goods.getName(), goods.getImage(), store.getId(), store.getName(), goods.getUserId(), goods.getUserName());
            return ResponseEntity.status(200).body(new Response(200, "Get category success", gd));
        } catch (Exception e) {
            LOGGER.error("get goods detail by category id failed: "  + e.toString());
            return ResponseEntity.status(404).body(new Response(404, e.getMessage(), null));
        }
    }

    public ResponseEntity<Response> getGoodsDetailByCategoryIds(List<String> categoryIds) {
        try {
            LOGGER.info("[{}]开始运行","getGoodsDetailByCategoryIds");
            ArrayList<GoodsCategory> gcs = goodsCategoryRepository.getByIds(categoryIds);
            System.out.println(gcs);
            ArrayList<String> gids = new ArrayList<String>();
            for(GoodsCategory gc: gcs){
                gids.add(gc.getGoodsId());
            }
            System.out.println(gids);
            List<Goods> gs = goodsMapper.findByIds(gids);
            System.out.println(gs);
            ArrayList<String> sids = new ArrayList();
            HashMap<String, Goods> gmp = new HashMap();
            for(Goods g: gs){
                sids.add(g.getStoreId());
                gmp.put(g.getId(), g);
            }
            List<Store> stores = storeMapper.findByIds(sids);
            System.out.println(stores);
            HashMap<String, Store> smp = new HashMap();
            for(Store s: stores){
                smp.put(s.getId(), s);
            }
            List<GoodsDetail> gds = new ArrayList<>();
            for(GoodsCategory gc: gcs){
                GoodsDetail gd = new GoodsDetail();
                gd.setCategoryId(gc.getId());
                gd.setCategoryName(gc.getName());
                gd.setStockNum(gc.getNum());
                gd.setPrice(gc.getPrice());

                Goods g = gmp.get(gc.getGoodsId());
                if(g != null){
                    gd.setGoodsId(g.getId());
                    gd.setGoodsName(g.getName());
                    gd.setGoodsImage(g.getImage());

                }

                Store s = smp.get(g.getStoreId());
                if(s != null){
                    gd.setStoreId(s.getId());
                    gd.setStoreName(s.getName());
                }
                gd.setSellerId(g.getUserId());
                gd.setSellerName(g.getUserName());
                gds.add(gd);
            }
            return ResponseEntity.status(200).body(new Response(200, "Get category success", gds));
        } catch (Exception e) {
            LOGGER.error("get goods detail by category ids failed: "  + e.toString());
            return ResponseEntity.status(404).body(new Response(404, e.getMessage(), null));
        }
    }

    public ResponseEntity<Response> getGoodsCategoryByGoodsId(String goodsId){
        try{
            LOGGER.info("[{}]开始运行","getGoodsCategoryByGoodsId");
            ArrayList<GoodsCategory> goodsCategories = goodsCategoryRepository.getAllByGoodsId(goodsId);
            HashMap mp = new HashMap();
            mp.put("categories", goodsCategories);
            return ResponseEntity.status(200).body(new Response(200, "Get categories success", mp));
        }catch (Exception e){
            LOGGER.error("get goods category by goods id failed: "  + e.toString());
            return  ResponseEntity.status(404).body(new Response(404, e.getMessage(), null));
        }
    }
}


