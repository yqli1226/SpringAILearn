package com.rongqi.springai.flightcustomerserviceagent.controller;

import com.rongqi.springai.flightcustomerserviceagent.service.ToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@CrossOrigin
public class OpenAiController {

    ChatClient chatClient;

    public OpenAiController(ChatClient.Builder builder,
                            ChatMemory chatMemory,
                            ToolService toolService) {
        this.chatClient = builder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                )
                .defaultSystem("""
                          ## 角色
                              你是图灵航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复问题
                              你正在通过在线聊天的形式与客户互动
                          ## 要求
                              1. 在涉及增删改（除了查询）function-call前，必须等待用户回复“确认”后再调用tool
                              2. 默认语言为中文
                          ## 额外信息
                              1. 今天的日期为{current_date}
                        """)
                .defaultTools(toolService)
                .build();
    }

    @GetMapping(value = "/ai/generateStreamAsString", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStreamAsString(
            @RequestParam(value = "message", defaultValue = "讲个笑话") String message,
            @RequestParam(value = "requestId", required = false) String requestId) {

        System.out.println("收到请求 [" + requestId + "]: " + message);

        // 使用原子布尔值跟踪是否已发送完成信号
        AtomicBoolean completed = new AtomicBoolean(false);

        return chatClient.prompt()
                .system(p -> p.param("current_date", LocalDate.now().toString()))
                .user(message)
                .stream()
                .content()
                // 确保在流结束时发送完成信号
                .concatWith(Flux.defer(() -> {
                    if (completed.compareAndSet(false, true)) {
                        return Flux.just("[complete]");
                    }
                    return Flux.empty();
                }));
    }

}
