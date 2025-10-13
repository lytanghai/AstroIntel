package com.tanghai.announcement.controller;

import com.tanghai.announcement.dto.req.SupportResistance;
import com.tanghai.announcement.dto.req.SupportResistanceReq;
import com.tanghai.announcement.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operator")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    @PostMapping("/mark-sp")
    public void supportResistanceReq(@RequestBody SupportResistance supportResistanceReq){
        operatorService.drawSupportResistance(supportResistanceReq);
    }

    @PostMapping("/check/mark-sp")
    public SupportResistanceReq getSupportResistanceReq(){
        return operatorService.getSupportResistanceReq();
    }

}
