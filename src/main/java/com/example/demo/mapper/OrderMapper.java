package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {


    @Select("select o.order_no orderNo, o.amount, o.user_id userId, i.description " +
            "from t_order o join t_order_item i on o.order_no = i.order_no")
    List<OrderDetailDTO> selectAllByOrderNoOrderDetailDtoList();

}
