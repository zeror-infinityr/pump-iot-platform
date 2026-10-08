package cn.sx.sxupr.module.iot.server.service.product;

import cn.sx.sxupr.module.iot.server.controller.product.ProductCreateReqVO;
import cn.sx.sxupr.module.iot.server.controller.product.ProductRespVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.product.ProductDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.product.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public ProductRespVO createProduct(ProductCreateReqVO request) {

        ProductDO product = new ProductDO();

        product.setProductKey(generateProductKey());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setStatus(1);

        productMapper.insert(product);

        return convert(product);
    }

    @Override
    public List<ProductRespVO> getProductList() {

        List<ProductDO> products = productMapper.selectList(null);

        return products.stream()
                .map(this::convert)
                .toList();
    }

    private ProductRespVO convert(ProductDO product) {
        return new ProductRespVO(
                product.getId(),
                product.getProductKey(),
                product.getName(),
                product.getDescription(),
                product.getStatus(),
                product.getCreateTime(),
                product.getUpdateTime()
        );
    }

    private String generateProductKey() {
        return "pk_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);
    }

}