package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequestMapping("/order")
@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/page")
    public PageDTO<Order> page(Page query) {
        return orderService.page(query);
    }

    @GetMapping("/new/{cnt}")
    public Integer addOrderAndItem(@PathVariable(name = "cnt") Integer cnt){
        Order order = new Order();
        order.setUserId(1L);
        order.setAmount(new BigDecimal("23.4"));
        OrderItem orderItem = new OrderItem();
        orderItem.setDesc("test global transaction");
        return orderService.saveOrderAndItem(order,orderItem);
    }

}
