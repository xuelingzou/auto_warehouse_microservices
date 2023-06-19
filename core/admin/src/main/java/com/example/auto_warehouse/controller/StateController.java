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

    // 管理员查看需要人工审核的全部订单
    @RequestMapping("/manual_review")
    @ResponseBody
    public List<Map<String,String>> manual_review(){
        List<Order> list_order = orderMapper.getOrderByStatePeople();
        List<Map<String,String>> list = new ArrayList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Order order:list_order){
            Map<String,String> map = new HashMap<>();
            map.put("orderID",String.valueOf(order.getOrderID()));
            map.put("suid",order.getSuid());
            map.put("time", sdf1.format(order.getTime()));
            list.add(map);
        }
        return list;
    }

    // 管理员查看需要人工审核的全部订单的入库申请单详情
    @RequestMapping("/manual_review_detail")
    @ResponseBody
    public List<Map<String,String>> manual_review_detail(@RequestBody Map<String,String> map1){
        int orderID = Integer.parseInt(map1.get("orderID"));
        List<Map<String,String>> list = new ArrayList<>();
        List<InputThings> list_inputThings = orderMapper.getInputThingsByOrderID(orderID);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        for(InputThings inputThings:list_inputThings){
            Map<String,String> map = new HashMap<>();
            map.put("sid",inputThings.getSid());
            map.put("sname",inputThings.getSname());
            map.put("stype",inputThings.getStype());
            map.put("num",String.valueOf(inputThings.getNum()));
            map.put("weight",String.valueOf(inputThings.getWeight()));
            map.put("sh",String.valueOf(inputThings.getSh()));
            map.put("sw",String.valueOf(inputThings.getSw()));
            map.put("sd",String.valueOf(inputThings.getSd()));
            map.put("productionDate", sdf1.format(inputThings.getProductionDate()));
            map.put("shelfLife",String.valueOf(inputThings.getShelfLife()));
            map.put("suid",inputThings.getSuid()); // suid是超市id
            map.put("size",inputThings.getSize());
            map.put("inputTime", sdf1.format(inputThings.getInputTime()));
            map.put("outputTime", sdf1.format(inputThings.getOutputTime()));
            map.put("orderID",String.valueOf(inputThings.getOrderID())); // order的id
            list.add(map);
        }
        return list;
    }


    // 人工审核通过
    @RequestMapping("/manual_review_passed")
    @ResponseBody
    public String manual_review_passed(@RequestBody Map<String,String> map1) throws ParseException {
        int orderID = Integer.parseInt(map1.get("orderID"));
        //List<Map<String,String>> list = new ArrayList<>();
        List<InputThings> list_inputThings = orderMapper.getInputThingsByOrderID(orderID);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        double cost=0;
        for(InputThings inputThings:list_inputThings){
            Map<String,String> map = new HashMap<>();
            map.put("sid",inputThings.getSid());
            map.put("sname",inputThings.getSname());
            map.put("stype",inputThings.getStype());
            map.put("num",String.valueOf(inputThings.getNum()));
            map.put("weight",String.valueOf(inputThings.getWeight()));
            map.put("sh",String.valueOf(inputThings.getSh()));
            map.put("sw",String.valueOf(inputThings.getSw()));
            map.put("sd",String.valueOf(inputThings.getSd()));
            map.put("production_date", sdf1.format(inputThings.getProductionDate()));
            map.put("shelf_life",String.valueOf(inputThings.getShelfLife()));
            map.put("suid",inputThings.getSuid()); // suid是超市id
            map.put("size",inputThings.getSize());
            map.put("inputTime", sdf1.format(inputThings.getInputTime()));
            map.put("outputTime", sdf1.format(inputThings.getOutputTime()));
            map.put("orderID",String.valueOf(inputThings.getOrderID())); // order的id
            //list.add(map);
            // 预留货位
            inputService.callInput(map,orderID);
            int day = (int) ((inputThings.getOutputTime().getTime() - inputThings.getInputTime().getTime())
                                / (24 * 60 * 60 * 1000));
            System.out.println("day:"+day);
            cost+=day*2*inputThings.getNum();
        }
        // 计费，写入对应order的cost;写入缴费日志
        orderMapper.modifyOrderCost(orderID,cost);
//        Order order = orderMapper.getOrderByOrderID(orderID);
//        OrderCostLog orderCostLog = new OrderCostLog(order.getSuid(),orderID,cost,"初始计划缴费");
//        orderMapper.insertOrderCostLog(orderCostLog);
        // 修改状态为“待选择缴费方式”
        orderMapper.modifyOrderState(orderID,"待选择缴费方式",inputService.getNowTime());
        Message message1 = new Message(orderID, "待选择缴费方式", orderMapper.getSuid(orderID));
        orderMapper.insertMessage(message1);

        return "true";
    }

    // 人工审核不通过
    @RequestMapping("/manual_review_failed")
    @ResponseBody
    public String manual_review_failed(@RequestBody Map<String,String> map1) throws ParseException {
        int orderID = Integer.parseInt(map1.get("orderID"));
        String reason = map1.get("reason");
        orderMapper.modifyOrderState(orderID,"人工审核不通过",inputService.getNowTime());
        orderMapper.modifyOrderOthers(orderID,reason);
        Message message1 = new Message(orderID, "人工审核不通过", orderMapper.getSuid(orderID));
        orderMapper.insertMessage(message1);
        return "true";
    }






}
