package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.Order;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public PageDTO<Order> page(Page query) {
        query = Optional.ofNullable(query).orElse(new Page());

        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.orderByAsc("order_no");
        orderQueryWrapper.last(String.format("limit %d, %d", query.getCurrent(), query.getSize()));
        Page<Order> page = orderMapper.selectPage(query, orderQueryWrapper);
        PageDTO<Order> pageDTO = new PageDTO<>();
        pageDTO.setOrders(page.orders());
        pageDTO.setPages(page.getPages());
        pageDTO.setRecords(page.getRecords());
        pageDTO.setCurrent(page.getCurrent());
        pageDTO.setSize(page.getSize());
        pageDTO.setPages(page.getPages());
        pageDTO.setTotal(page.getTotal());
        return pageDTO;
    }
}
