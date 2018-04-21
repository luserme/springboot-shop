package com.lq.shop.service.impl;

import com.google.common.collect.Lists;
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
import com.lq.shop.service.IProductService;
import com.lq.shop.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luqing
 * @date 2018/04/21 12:05
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{



    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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

        ProductDetailVo productDetailVo = assembleProductDetailVo(productEntity);

        return ServerResult.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResult getProductList(int pageNum, int pageSize) {

        Pageable pageable = new PageRequest(pageNum,pageSize);
        PageImpl<ProductEntity> entityPage = (PageImpl<ProductEntity>) productRepository.findAll(pageable);

        List<ProductEntity> allProduct = entityPage.getContent();
        List<ProductDetailVo> productDetailVoList = Lists.newArrayList();

        for (ProductEntity productEntity : allProduct){
            ProductDetailVo productDetailVo = assembleProductDetailVo(productEntity);
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
            ProductDetailVo productDetailVo = assembleProductDetailVo(productEntity);
            productDetailVoList.add(productDetailVo);
        }

        return ServerResult.createBySuccess(productDetailVoList);
    }


    private ProductDetailVo assembleProductDetailVo(ProductEntity productEntity) {

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

}


