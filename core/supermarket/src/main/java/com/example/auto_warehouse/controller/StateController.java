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


    // 超市查看自己所有订单当前状态
    @RequestMapping("/show_supermarket_allOrder")
    @ResponseBody
    public List<Map<String,String>> show_supermarket_allOrder(@RequestBody Map<String,String> map1){
        String suid = map1.get("suid");
        List<Map<String,String>> list = new ArrayList<>();
        List<Order> list_order = orderMapper.getOrderBySuid(suid);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Order order:list_order){
            Map<String,String> map = new HashMap<>();
            map.put("orderID",String.valueOf(order.getOrderID()));
            map.put("state",order.getState());
            map.put("time", sdf1.format(order.getTime()));
            map.put("others", order.getOthers()); // 如果未通过人工审核，原因会显示在这里
            list.add(map);
        }
        return list;
    }

    // 超市查看订单所有状态流
    @RequestMapping("/show_supermarket_order_allState")
    @ResponseBody
    public List<Map<String,String>> show_supermarket_order_allState(@RequestBody Map<String,String> map1){
        int orderID = Integer.parseInt(map1.get("orderID"));
        List<Map<String,String>> list = new ArrayList<>();
        List<Message> list_message = orderMapper.getMessageByOrderID(orderID);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Message message:list_message){
            Map<String,String> map = new HashMap<>();
            map.put("orderID",String.valueOf(message.getOrderID()));
            map.put("action",message.getAction());
            map.put("time", sdf1.format(message.getTime()));
            list.add(map);
        }
        return list;
    }

    // 超市查看未通过系统审核的申请单原因，其实就是notInput的信息
    @RequestMapping("/show_supermarket_order_notInput")
    @ResponseBody
    public List<Map<String,String>> show_supermarket_order_notInput(@RequestBody Map<String,String> map){
        int orderID = Integer.parseInt(map.get("orderID"));

        List<Map<String,String>> list = new ArrayList<>();
        List<NotInput> list_notInput = orderMapper.getNotinputByOrderID(orderID);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(NotInput not_Input:list_notInput){
            Map<String,String> map2 = new HashMap<>();
            map2.put("id",not_Input.getId());
            map2.put("name",not_Input.getName());
            map2.put("type",not_Input.getType());
            map2.put("reason",not_Input.getReason());
            map2.put("orderID",String.valueOf( not_Input.getOrderID()));
            map2.put("suid",String.valueOf( not_Input.getSuid()));
            map2.put("productionDate", sdf1.format(not_Input.getProduction_date()));
            map2.put("num", String.valueOf(not_Input.getNum()));
            map2.put("shelfLife", String.valueOf(not_Input.getShelf_life()));

            list.add(map2);
        }
        System.out.println("1234567889");
        System.out.println(list);
        return list;
    }

}
