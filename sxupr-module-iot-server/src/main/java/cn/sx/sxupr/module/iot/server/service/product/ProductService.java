package cn.sx.sxupr.module.iot.server.service.product;

import cn.sx.sxupr.module.iot.server.controller.product.ProductCreateReqVO;
import cn.sx.sxupr.module.iot.server.controller.product.ProductRespVO;

import java.util.List;

public interface ProductService {

    ProductRespVO createProduct(ProductCreateReqVO request);

    List<ProductRespVO> getProductList();

}