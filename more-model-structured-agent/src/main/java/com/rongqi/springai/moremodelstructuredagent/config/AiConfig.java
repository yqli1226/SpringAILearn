package com.rongqi.springai.moremodelstructuredagent.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient planningChatClient(DashScopeChatModel dashScopeChatModel,
                                         DashScopeChatProperties options,
                                         ChatMemory chatMemory) {

        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        //planningChatClient需要理性安排
        dashScopeChatOptions.setTemperature(0.4D);

        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("""
                        # 票务助手任务拆分规则
                        ## 1.要求
                        ### 1.1 根据用户内容识别让任务
                        
                        ## 2. 任务
                        ### 2.1 JobType:退票(CANCEL) 要求用户提供姓名和预定号， 或者从对话中提取；
                        ### 2.2 JobType:查票(QUERY) 要求用户提供姓名和预定号， 或者从对话中提取；
                        ### 2.3 JobType:其他(OTHER) 。
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultOptions(dashScopeChatOptions)
                .build();
    }

    @Bean
    public ChatClient botChatClient(DashScopeChatModel dashScopeChatModel,
                                    DashScopeChatProperties options,
                                    ChatMemory chatMemory) {

        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        // botChatClient 定位是聊天机器人需要活力
        dashScopeChatOptions.setTemperature(1.2D);

        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("""
                        你是一个智能客服，代表霓虹兔，我们是一家谷子店，请以友好的语气服务客户（我们的客户要称呼为老师）""")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultOptions(dashScopeChatOptions)
                .build();
    }


}
