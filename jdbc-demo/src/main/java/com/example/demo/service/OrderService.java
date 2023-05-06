package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;

public interface OrderService {


    PageDTO<Order> page(Page query);


    Integer saveOrder(Order order);

    Integer saveOrderAndItem(Order order, OrderItem orderItem);
}
