package com.example.auto_warehouse.controller;

import com.example.auto_warehouse.bean.OrderCostLog;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StateControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StateController stateController;

//    @Test
//    void show_supermarket_allOrder() throws ParseException {
//        // 构造请求体
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("suid", "101");
//        //发送POST请求，获取响应结果下
//        ResponseEntity<String> response = restTemplate.postForEntity("/state/show_supermarket_allOrder",requestBody, String.class);
//        assertThat(response.getStatusCode().value()).isEqualTo(200);
//        assertThat(response.getBody()).isNotEmpty();
//        System.out.println("-------response----------");
//        System.out.println(response);
////
//        //采用parse处理
//        System.out.println(response.getBody());
//        JSONParser parser = new JSONParser();
//        JSONArray jsonArray = (JSONArray) parser.parse(response.getBody());
//
//        String orderID="63";
//        System.out.println(jsonArray);
//        for (Object obj : jsonArray) {
//            JSONObject jsonObject = (JSONObject) obj;
//
//            String actualStype = jsonObject.getAsString("orderID");
//            MatcherAssert.assertThat("orderID should be " + orderID, actualStype, equalTo(orderID));
//        }
//
//    }


    @Test
    void show_supermarket_order_allState()throws ParseException {
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("orderID", "64");
        //发送POST请求，获取响应结果下
        ResponseEntity<String> response = restTemplate.postForEntity("/state/show_supermarket_order_allState",requestBody, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotEmpty();
        System.out.println("-------response----------");
        System.out.println(response);
//
        //采用parse处理
        System.out.println(response.getBody());
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(response.getBody());

        String orderID="63";
        System.out.println(jsonArray);
//        for (Object obj : jsonArray) {
//            JSONObject jsonObject = (JSONObject) obj;
//
//            String actualStype = jsonObject.getAsString("orderID");
//            MatcherAssert.assertThat("orderID should be " + orderID, actualStype, equalTo(orderID));
//        }

    }

    @Test
    void show_supermarket_order_notInput()  throws ParseException {
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("orderID", "64");
        //发送POST请求，获取响应结果下
        ResponseEntity<String> response = restTemplate.postForEntity("/state/show_supermarket_order_notInput",requestBody, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotEmpty();
        System.out.println("-------response----------");
        System.out.println(response);

        //采用parse处理
        System.out.println(response.getBody());
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(response.getBody());

//        String orderID="64";
//        System.out.println(jsonArray);
//        for (Object obj : jsonArray) {
//            JSONObject jsonObject = (JSONObject) obj;
//
//            String actualStype = jsonObject.getAsString("orderID");
//            MatcherAssert.assertThat("orderID should be " + orderID, actualStype, equalTo(orderID));
//        }

    }


}