package com.rongqi.springai.flightcustomerserviceagent.data;

import lombok.Getter;

/**
 * 预订舱位枚举类
 * 定义了不同类型的舱位及其对应的代码和名称
 */
@Getter
public enum BookingClass {

    // 经济舱，代码为"ECONOMY"
    ECONOMY("ECONOMY","经济舱"),

    // 高级经济舱，代码为"PREMIUM_ECONOMY"
    PREMIUM_ECONOMY("PREMIUM_ECONOMY","高级经济舱"),

    // 商务舱，代码为"BUSINESS"
    BUSINESS("BUSINESS","商务舱"),

    // 头等舱，代码为"FIRST"
    FIRST("FIRST","头等舱");

    // 舱位代码
    private final String code;

    // 舱位名称
    private final String name;

    /**
     * 构造函数，初始化舱位代码和名称
     * @param code 舱位代码
     * @param name 舱位名称
     */
    BookingClass(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
