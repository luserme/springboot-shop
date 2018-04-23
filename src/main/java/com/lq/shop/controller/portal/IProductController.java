package com.lq.shop.controller.portal;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.service.IProductService;
import com.lq.shop.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luqing
 * @date 2018/04/23 20:28
 */

@RestController
@RequestMapping("/product")
public class IProductController {

    private IProductService iProductService;

    @Autowired
    public void setIProductService(IProductService iProductService) {
        this.iProductService = iProductService;
    }

    @RequestMapping("/detail")
    public ServerResult<ProductDetailVo> detail(Integer productId){
        return iProductService.findProductDetail(productId);
    }

    @RequestMapping("/list")
    public ServerResult<Page> list(@RequestParam(value = "keyword",required = false)String keyword,
                                   @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                   @RequestParam(value = "pageNum",defaultValue = Const.Page.PAGE_DEFAULT_NUM) int pageNum,
                                   @RequestParam(value = "pageSize",defaultValue = Const.Page.PAGE_DEFAULT_SIZE) int pageSize,
                                   @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.findByKeywordAndCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);
    }

}
