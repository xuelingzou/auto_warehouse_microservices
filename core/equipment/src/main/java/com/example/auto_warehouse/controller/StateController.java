package com.example.auto_warehouse.controller;

import com.example.auto_warehouse.bean.*;
import com.example.auto_warehouse.mapper.*;
import com.example.auto_warehouse.service.InputService;
import com.example.auto_warehouse.service.OutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/state")
public class StateController {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private InputService inputService;
    @Autowired
    private OutputService outputService;


    // 实际入库（python发送，orderID需要去数据库中看）
    @RequestMapping("/actual_input")
    @ResponseBody
    public String actual_input(@RequestBody Map<String,String> map1) throws ParseException {
        int orderID = Integer.parseInt(map1.get("orderID"));
        return inputService.actual_input(orderID);
    }


    // 入库确认（python发送，orderID需要去数据库中看）
    @RequestMapping("/actual_input_confirm")
    @ResponseBody
    public String actual_input_confirm(@RequestBody Map<String,String> map1) throws ParseException {
        int orderID;
        try {
            orderID = Integer.parseInt(map1.get("orderID"));
        }catch (NumberFormatException e){
            return "false";
        }
        return inputService.actual_input_confirm(orderID);
    }
}
