package com.rongqi.springai.tools.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Autowired
    TicketService ticketService;


    // 告知大模型提供了何种工具
    @Tool(description = "退票")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancel(
            // 告知大模型工具需要何种参数
            @ToolParam(description = "预定号") String ticketNumber,
            @ToolParam(description = "姓名") String name) {

        ticketService.cancel(ticketNumber, name);

        return "退票成功";
    }

    @Tool(description = "查询天气")
    public String queryWeather(@ToolParam(description = "经度") double longitude,
                               @ToolParam(description = "纬度") double latitude) {
        return "下雨天，创世神不喜欢雨天";
    }

}
