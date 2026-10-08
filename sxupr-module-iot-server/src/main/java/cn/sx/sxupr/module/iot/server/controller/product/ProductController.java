package cn.sx.sxupr.module.iot.server.controller.product;

import cn.sx.sxupr.module.iot.server.service.product.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iot/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductRespVO createProduct(
            @RequestBody ProductCreateReqVO request) {

        return productService.createProduct(request);
    }

    @GetMapping
    public List<ProductRespVO> getProductList() {
        return productService.getProductList();
    }

}