package com.rongqi.springai.chatclient.memory;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestJDBCMemory {

    ChatClient chatClient;

    @BeforeEach
    public void setup(@Autowired DashScopeChatModel chatModel,
                      @Autowired ChatMemory chatMemory) {

        chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();

    }

    @Test
    public void testJDBCAdvisor() {
        String content = chatClient.prompt()
                .user("你好我是荣启")
//                .advisors(new ReReadingAdvisor())
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1"))
                .call()
                .content();

        System.out.println(content);
        System.out.println("----------------------------------------------------------------------");
        content = chatClient.prompt()
                .user("我叫什么？")
//                .advisors(new ReReadingAdvisor())
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1"))
                .call()
                .content();
        System.out.println(content);
    }


    @TestConfiguration
    static class TestJDBCAdvisorConfiguration {

        @Bean
        ChatMemory chatMemory(JdbcChatMemoryRepository cmRepository) {
            return MessageWindowChatMemory
                    .builder()
                    .maxMessages(10)
                    .chatMemoryRepository(cmRepository).build();
        }

    }

}