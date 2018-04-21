package com.lq.shop.common.util;

import com.lq.shop.dao.ProductRepository;
import com.lq.shop.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;


/**
 * @author luqing
 * @date 2018/04/21 15:55
 */
public class PageUtils {

        private Pageable pageable;

        private ProductRepository productRepository;

        public PageUtils(Integer page, Integer size,ProductRepository productRepository) {
            pageable = new PageRequest(page, size);
            this.productRepository = productRepository;
        }

        public PageUtils(Pageable pageable, ProductRepository productRepository) {
            this.pageable = pageable;
            this.productRepository = productRepository;
        }


    /**
     *
     * @param id 商品id
     * @param name 商品名称
     * @return 查询结果
     */
        public Page<ProductEntity> findPage(Integer id, String name) {
            return productRepository.findAll((root, query, cb) -> {
                
                Path<String> searchId = root.get("id");
                Path<String> searchName = root.get("name");

                if (id == null) {
                    //这里可以设置任意条查询条件
                    query.where(cb.like(searchName, "%" + name + "%"));
                    return null;
                }

                if (name == null){
                    //这里可以设置任意条查询条件
                    query.where(cb.equal(searchId, id));
                    return null;
                }


                query.where(cb.equal(searchId, id),
                        cb.like(searchName, "%" + name + "%")
                );
                return null;
            }, pageable);
        }

}
