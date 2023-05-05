package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.demo.entity.Order;

public interface OrderService {


    PageDTO<Order> page(Page query);
}
