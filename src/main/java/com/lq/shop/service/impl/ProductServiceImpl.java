package com.lq.shop.service.impl;

import com.google.common.collect.Lists;
import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.DateTimeUtil;
import com.lq.shop.common.util.PageUtils;
import com.lq.shop.common.util.PropertiesUtil;
import com.lq.shop.common.util.StringUtils;
import com.lq.shop.dao.CategoryRepository;
import com.lq.shop.dao.ProductRepository;
import com.lq.shop.entity.CategoryEntity;
import com.lq.shop.entity.ProductEntity;
import com.lq.shop.service.ICategoryService;
import com.lq.shop.service.IProductService;
import com.lq.shop.vo.ProductDetailVo;
import com.lq.shop.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luqing
 * @date 2018/04/21 12:05
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;

    private ICategoryService iCategoryService;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setICategoryService(ICategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    @Override
    public ServerResult saveOrUpdateProduct(ProductEntity productEntity) {
        if (productEntity != null){
            if (StringUtils.isNotBlank(productEntity.getSubImages())){
                String[] subImageArr = productEntity.getSubImages().split(",");
                if (subImageArr.length>0){
                    productEntity.setMainImage(subImageArr[0]);
                }
            }


            if (productEntity.getId() != null){
                ProductEntity save = productRepository.saveAndFlush(productEntity);
                if (save != null){
                    return ServerResult.createBySuccess("更新产品成功");
                }

                return ServerResult.createByErrorMessage("更新产品失败");
            }
        }


        return ServerResult.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResult setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(),ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }

        ProductEntity productEntity = productRepository.findOne(productId);

        if (productEntity != null){
            productEntity.setStatus(status);
            productRepository.saveAndFlush(productEntity);

            return ServerResult.createBySuccess("修改产品销售成功");
        }

        return ServerResult.createBySuccess("当前产品不存在");
    }

    @Override
    public ServerResult manageProductDetail(Integer productId) {
        if (productId == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(),ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }

        ProductEntity productEntity = productRepository.findOne(productId);
        if (productEntity == null){
            return ServerResult.createByErrorMessage("产品已下架或着删除");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVO(productEntity);

        return ServerResult.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResult getProductList(int pageNum, int pageSize) {

        Pageable pageable = new PageRequest(pageNum,pageSize);
        PageImpl<ProductEntity> entityPage = (PageImpl<ProductEntity>) productRepository.findAll(pageable);

        List<ProductEntity> allProduct = entityPage.getContent();
        List<ProductDetailVo> productDetailVoList = Lists.newArrayList();

        for (ProductEntity productEntity : allProduct){
            ProductDetailVo productDetailVo = assembleProductDetailVO(productEntity);
            productDetailVoList.add(productDetailVo);
        }

        PageImpl<ProductDetailVo> page = new PageImpl<>(productDetailVoList,pageable,entityPage.getTotalElements());

        return ServerResult.createBySuccess(page);
    }

    @Override
    public ServerResult searchProduct(String productName, Integer productId, int pageNum, int pageSize) {

        Pageable pageable = new PageRequest(pageNum,pageSize);

        Page<ProductEntity> page = new PageUtils(pageable, productRepository).findPage(productId, productName);

        List<ProductEntity> content = page.getContent();
        List<ProductDetailVo> productDetailVoList = Lists.newArrayList();

        for (ProductEntity productEntity : content){
            ProductDetailVo productDetailVo = assembleProductDetailVO(productEntity);
            productDetailVoList.add(productDetailVo);
        }

        return ServerResult.createBySuccess(productDetailVoList);
    }

    @Override
    public ServerResult<ProductDetailVo> findProductDetail(Integer productId) {

        if(productId == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(),ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }

        ProductEntity product = productRepository.findOne(productId);
        if(product == null){
            return ServerResult.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResult.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVO(product);
        return ServerResult.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResult<Page> findByKeywordAndCategoryId(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {

        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(),ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Pageable pageable = new PageRequest(pageNum,pageSize);

        List<Integer> categoryIdList = new ArrayList<>();

        if(categoryId != null){

            CategoryEntity category = categoryRepository.findOne(categoryId);

            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageImpl pageInfo = new PageImpl<>(productListVOList,pageable,productListVOList.size());
                return ServerResult.createBySuccess(pageInfo);
            }

            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }


        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                Sort sort = new Sort(Sort.Direction.fromStringOrNull(orderByArray[1]), orderByArray[0]);
                pageable = new PageRequest(pageNum,pageSize,sort);
            }
        }

        Page<ProductEntity> productPage = null;
        if (StringUtils.isNotBlank(keyword) && categoryIdList.size()!=0){
            productPage = productRepository.findAllByNameLikeAndCategoryIdIn("%" + keyword + "%",categoryIdList,pageable);
        }

        if (StringUtils.isNotBlank(keyword) && categoryIdList.size()==0){
            productPage = productRepository.findAllByNameLike("%" + keyword + "%",pageable);
        }

        if (categoryIdList.size()!=0 && StringUtils.isBlank(keyword)){
            productPage = productRepository.findPageByCategoryIdIn(categoryIdList,pageable);
        }

        List<ProductListVO> productListVOList = Lists.newArrayList();

        if (productPage != null){
            for(ProductEntity product : productPage.getContent()){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }

        Page page = new PageImpl<>(productListVOList,pageable,productPage==null?0:productPage.getTotalElements());

        return ServerResult.createBySuccess(page);
    }



    private ProductDetailVo assembleProductDetailVO(ProductEntity productEntity) {

        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(productEntity.getId());
        productDetailVo.setSubtitle(productEntity.getSubtitle());
        productDetailVo.setPrice(productEntity.getPrice());
        productDetailVo.setMainImage(productEntity.getMainImage());
        productDetailVo.setSubImages(productEntity.getSubImages());
        productDetailVo.setCategoryId(productEntity.getCategoryId());
        productDetailVo.setDetail(productEntity.getDetail());
        productDetailVo.setName(productDetailVo.getName());
        productDetailVo.setStatus(productEntity.getStatus());
        productDetailVo.setStock(productEntity.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.luserme.com/"));

        CategoryEntity categoryEntity = categoryRepository.findOne(productEntity.getCategoryId());

        if (categoryEntity == null){
            //默认使用根节点
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(categoryEntity.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dataToStr(productEntity.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dataToStr(productEntity.getUpdateTime()));

        return productDetailVo;
    }


    private ProductListVO assembleProductListVO(ProductEntity product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setName(product.getName());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVO.setMainImage(product.getMainImage());
        productListVO.setPrice(product.getPrice());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setStatus(product.getStatus());
        return productListVO;
    }

}


