package com.rongqi.springai.tools.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Autowired
    TicketService ticketService;


    // 告知大模型提供了何种工具
    @Tool(description = "退票")
    public String cancel(
            // 告知大模型工具需要何种参数
            @ToolParam(description = "预定号") String ticketNumber,
            @ToolParam(description = "姓名") String name) {

        ticketService.cancel(ticketNumber, name);

        return "退票成功";
    }

}
