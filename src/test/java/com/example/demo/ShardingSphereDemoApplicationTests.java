package com.example.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class ShardingSphereDemoApplicationTests {

    @Resource
    UserMapper userMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderItemMapper orderItemMapper;

    @Test
    void insertTest() {
        User user = new User();
        user.setName("test");
        int insert = userMapper.insert(user);
        assert insert == 1;
    }

    @Test
    void insertUserShardingTest() {
        for (int i = 0; i < 4; i++) {
            User user = new User();
            user.setName("test" + i);
            int insert = userMapper.insert(user);
            assert insert == 1;
        }
    }

    /**
     * Actual SQL: slave ::: SELECT  id,name  FROM t_user
     */
    @Test
    void selectTest() {
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        System.out.println(users);
    }


    @Test
    void insertOrderShardingTest() {
        for (int i = 1; i < 9; i++) {
            Order order = new Order();
            order.setUserId(1L);
            order.setAmount(new BigDecimal(String.valueOf(i)));
            orderMapper.insert(order);
        }
    }


    @Test
    void insertOrderAndItemsShardingTest() {
        for (int i = 1; i < 9; i++) {
            Order order = new Order();
            order.setUserId(1L);
            order.setAmount(new BigDecimal(String.valueOf(i)));
            orderMapper.insert(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setId((long) i);
            Long orderNo = order.getOrderNo();
            System.out.println(orderNo + "<===");
            orderItem.setOrderNo(orderNo);
            orderItem.setDesc("test desc");
            orderItemMapper.insert(orderItem);

        }
    }

    @Test
    public void selectAllOrdersTest() {
        List<Order> orders = orderMapper.selectList(null);
        orders.forEach(System.out::println);
    }

    @Test
    public void selectAllOrderDetail(){
        List<OrderDetailDTO> list = orderMapper.selectAllByOrderNoOrderDetailDtoList();
        list.forEach(System.out::println);
    }

    @Test
    void deleteAllOrders() {
        for (int i = 1; i < 9; i++) {
            Order order = new Order();
            order.setOrderNo((long) i);
            orderMapper.deleteById(order);
        }
    }
}
