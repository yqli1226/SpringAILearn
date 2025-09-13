package com.rongqi.springai.mcpsseserver;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class McpSseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpSseServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(UserToolService userToolService) {
        return MethodToolCallbackProvider.builder().toolObjects(userToolService).build();
    }


}
