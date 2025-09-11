package com.rongqi.springai.flightcustomerserviceagent.data;

import java.util.ArrayList;
import java.util.List;

/**
 * BookingData类用于存储和管理客户预订数据
 * 包含客户列表和预订列表，并提供相应的getter和setter方法
 */
public class BookingData {
    // 客户列表，用于存储所有客户信息
    private List<Customer> customers = new ArrayList<>();

    // 预订列表，用于存储所有预订信息
    private List<Booking> bookings = new ArrayList<>();

    // 获取客户列表的方法
    public List<Customer> getCustomers() {
        return customers;
    }

    // 设置客户列表的方法
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    // 获取预订列表的方法
    public List<Booking> getBookings() {
        return bookings;
    }

    // 设置预订列表的方法
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

}
