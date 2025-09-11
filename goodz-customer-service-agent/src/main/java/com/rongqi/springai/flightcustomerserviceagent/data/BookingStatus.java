package com.rongqi.springai.flightcustomerserviceagent.data;

import lombok.Getter;

@Getter
public enum BookingStatus {

    CONFIRMED("CONFIRMED", "已预定"),
    CANCELLED("CANCELLED","已取消"),
    COMPLETED("COMPLETED","已完成");

    private final String status;
    private final String description;

    BookingStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

}
