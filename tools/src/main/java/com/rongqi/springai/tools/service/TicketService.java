package com.rongqi.springai.tools.service;

import org.springframework.stereotype.Service;

@Service
public class TicketService {

    public void cancel(String ticketNumber, String name) {
        System.out.println("取消订单，订单号：" + ticketNumber + "，乘客：" + name);
    }
}
