package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {

    private Long orderNo;

    private BigDecimal amount;

    private Long userId;

    private String description;
}
