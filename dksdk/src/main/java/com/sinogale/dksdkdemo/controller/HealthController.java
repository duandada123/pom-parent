package com.sinogale.dksdkdemo.controller;


import com.sinogale.dksdkdemo.dto.response.ApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/_health")
public class HealthController {

    @GetMapping("/ok")
    @ApiOperation(value = "健康检查", notes = "健康检查")
    public String health() {
        return "ok";
    }
}
