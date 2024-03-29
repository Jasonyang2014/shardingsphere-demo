package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.demo.entity.Order;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public PageDTO<Order> page(Page query) {
        query = Optional.ofNullable(query).orElse(new Page<>());
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.orderByAsc("order_no");
        PageHelper.startPage((int) query.getCurrent(), (int) query.getSize());
        List<Order> orders = orderMapper.selectList(orderQueryWrapper);
        PageInfo<Order> page = new PageInfo<>(orders);

        PageDTO<Order> pageDTO = new PageDTO<>();
        pageDTO.setRecords(page.getList());
        pageDTO.setCurrent(page.getPageNum());
        pageDTO.setSize(page.getSize());
        pageDTO.setTotal(page.getTotal());
        pageDTO.setPages(page.getPages());
        return pageDTO;
    }
}
