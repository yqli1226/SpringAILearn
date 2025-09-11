package com.rongqi.springai.flightcustomerserviceagent.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Autowired
    FlightBookingService flightBookingService;

    /**
     * 退票
     * @param bookingNumber 预定号
     * @param name 真实人名（必填，必须是人的真实姓名，严禁用其他信息代替）
     * @return 已取消预定
     */
    @Tool(description = "退票/取消预定，调用前先查询航班")
    public String cancelBooking(
            @ToolParam(description = "预定号") String bookingNumber,
            @ToolParam(description = "真实人名（必填，必须是人的真实姓名，严禁用其他信息代替）") String name) {
        flightBookingService.cancelBooking(bookingNumber, name);
        return "已取消预定";
    }

    @Tool(description = "查询航班信息")
    public FlightBookingService.BookingDetails getBookingInfo (
            @ToolParam(description = "预定号") String bookingNumber,
            @ToolParam(description = "真实人名（必填，必须是人的真实姓名，严禁用其他信息代替）") String name) {
        return flightBookingService.getBookingDetails(bookingNumber, name);

    }





}
