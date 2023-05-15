package com.example.elastic.job.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum GenderEnum {
    FEMALE(0,"female"),
    MALE(1,"male"),
    UNKNOW(2,"unKnow");

    @EnumValue
    public final Integer value;
    public final String desc;

    GenderEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static GenderEnum valueOf(int index){
        for (GenderEnum value : values()) {
            if (value.value == index){
                return value;
            }
        }
        return UNKNOW;
    }
}
