package cn.sx.sxupr.module.iot.server.controller.thingmodel;

import cn.sx.sxupr.module.iot.server.controller.vo.thingmodel.ThingModelVersionCreateReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.thingmodel.ThingModelVersionVO;
import cn.sx.sxupr.module.iot.server.service.thingmodel.ThingModelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iot/products/{productId}/thing-model")
public class ThingModelController {

    private final ThingModelService thingModelService;

    public ThingModelController(
            ThingModelService thingModelService) {

        this.thingModelService = thingModelService;
    }

    @PostMapping("/versions")
    public ThingModelVersionVO createVersion(
            @PathVariable Long productId,
            @Valid @RequestBody ThingModelVersionCreateReqVO reqVO) {

        return thingModelService.createVersion(
                productId,
                reqVO
        );
    }

    @GetMapping("/latest")
    public ThingModelVersionVO getLatest(
            @PathVariable Long productId) {

        return thingModelService.getLatest(productId);
    }
}