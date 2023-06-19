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


    // 超市查看需要缴费的订单
    @RequestMapping("/show_choose_payMethod")
    @ResponseBody
    public List<Map<String,String>> show_choose_payMethod(@RequestBody Map<String,String> map1){
        String suid = map1.get("suid");
//        List<Order> list_order = orderMapper.getOrderByStatePay(suid);
//        list_order.addAll(orderMapper.getOrderByStatePay2(suid));
        List<Order> list_order = orderMapper.getOrderBySuid(suid);
        List<Map<String,String>> list = new ArrayList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Order order:list_order){
            double method = order.getPayMethod();
            Map<String,String> map = new HashMap<>();
            if(order.getState().equals("入库缴费状态")){
                map.put("cost",String.valueOf((order.getCost())*method));
            }else if(order.getState().equals("出库重计费补缴费状态")){
                map.put("cost",String.valueOf(order.getActualCost()-order.getPaidMoney()));
            }else if(order.getState().equals("待选择缴费方式")){
                map.put("cost",String.valueOf(order.getCost()));
            }else{
                continue;
            }
            map.put("orderID",String.valueOf(order.getOrderID()));
            map.put("time", sdf1.format(order.getTime()));
            map.put("statement",order.getState());// 对于缴费的说明，是哪一阶段的
            map.put("payMethod",String.valueOf(order.getPayMethod()));

            list.add(map);
        }
        return list;
    }

    // 超市选择缴费方式之后
    @RequestMapping("/choose_payMethod")
    @ResponseBody
    public String choose_payMethod(@RequestBody Map<String,String> map1) throws ParseException {
        int orderID;
        try {
            orderID = Integer.parseInt(map1.get("orderID"));
        }catch (NumberFormatException e){
            return "false";
        }
        double payMethod = Double.parseDouble(map1.get("payMethod"));
        if (payMethod>1||payMethod<0){
            return "flase";
        }
        System.out.println("1111111111111111111"+payMethod);
        // 写进order的payMethod
        orderMapper.setPayMethod(orderID, payMethod);
        return inputService.finish_payment(orderID);
    }


    // 获取缴费日志
    @RequestMapping("/getPaymentOrderLog")
    public List<OrderCostLog>getPaymentOrderLog(@RequestBody Map<String,String>map1){
        String suid = map1.get("suid");
        return outputService.getPaymentOrderLog(suid);
    }

    // 超市缴费完成之后
    @RequestMapping("/finish_payment")
    @ResponseBody
    public String finish_payment(@RequestBody Map<String,String> map1) throws ParseException {
        int orderID = Integer.parseInt(map1.get("orderID"));
        Order order = orderMapper.getOrderByOrderID(orderID);
        if (order==null){
            return "flase";
        }
        double money = 0;
        if(order.getState().equals("入库缴费状态")){
            double pay = order.getCost()*order.getPayMethod();
            money = pay;
            // 之前不会缴费，所以直接把pay写入就行
            orderMapper.updatePaidMoney(orderID,pay);
            OrderCostLog orderCostLog = new OrderCostLog(order.getSuid(),orderID,pay,"入库缴费");
            orderMapper.insertOrderCostLog(orderCostLog);

            orderMapper.modifyOrderState(orderID,"入库缴费已完成",inputService.getNowTime());
            Message message1 = new Message(orderID, "入库缴费已完成", orderMapper.getSuid(orderID));
            orderMapper.insertMessage(message1);

        }

        // 出库的缴费状态判断
        if(order.getState().equals("出库重计费补缴费状态")){
            double pay = order.getActualCost()-order.getPaidMoney();
            money = pay;
            orderMapper.updatePaidMoney(orderID,order.getPaidMoney()+pay);
            OrderCostLog orderCostLog = new OrderCostLog(order.getSuid(),orderID,pay,"出库补缴费");
            orderMapper.insertOrderCostLog(orderCostLog);
            // 实际出库
            List<OutputThings> list = orderMapper.getOutputThingsByOrderID(orderID);
            for(OutputThings outputThings:list){
                Map<String,String> map = new HashMap<>();
                map.put("sid",outputThings.getSid());
                map.put("suid",outputThings.getSuid());
                map.put("name",outputThings.getName());
                map.put("num",String.valueOf(outputThings.getNum()));
                map.put("orderID",String.valueOf(outputThings.getOrderID()));
                outputService.callOutput(map);
            }
        }
        //------------------------------------------------------------------------------------------------移到重计费缴费那里
        // 仓库收入income表增加，repository收入增加
        inputService.income(money);
        return "true";
    }
}
