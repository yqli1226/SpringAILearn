package com.rongqi.springai.chatclient.memory;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootTest
public class TestMemory {

    ChatClient client;

    @BeforeEach
    public void setUp(@Autowired ChatClient.Builder builder, @Autowired ChatMemory chatMemory) {
        client = builder.defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()).build();
    }


    @Test
    public void testMemory  (@Autowired ChatModel chatModel) {
        //这里使用new创建时，直接使用Builder默认设置的内存存储记忆而非jdbc存储
        ChatMemory memory = MessageWindowChatMemory.builder().maxMessages(20).build();
        String conversationId = "userId_timestamp";

        interaction(memory, chatModel, conversationId, "你好我叫李勇气");
        System.out.println("--------------------------------------------------------------------------");
        interaction(memory, chatModel, conversationId, "我叫什么");

    }

    private void interaction(ChatMemory memory, ChatModel chatModel, String conversationId, String userContent) {

        UserMessage userMessage = new UserMessage(userContent);
        memory.add(conversationId, userMessage);
        ChatResponse aiResponse = chatModel.call(new Prompt(memory.get(conversationId)));
        memory.add(conversationId, aiResponse.getResult().getOutput());
        System.out.println(aiResponse.getResult().getOutput().getText());
    }

    // 记忆拦截器
    @Test
    public void testMemoryAdvisor (@Autowired ChatClient.Builder builder, @Autowired ChatMemory chatMemory) {
        ChatClient client = builder.defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()).build();

        String content = client.prompt().user("你好我叫李勇气").call().content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");

        content = client.prompt().user("我叫什么").call().content();
        System.out.println(content);
    }

    @Test
    public void testMemoryAdvisorByTenant() {
        String content = client.prompt().user("我叫李勇气").advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");
        content = client.prompt().user("我叫什么").advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");
        content = client.prompt().user("我叫什么").advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "2")).call().content();
        System.out.println(content);
    }

    @TestConfiguration
    static class TestMemoryConfiguration {

        @Bean
        @Primary //需要使用注解指定内存存储
        ChatMemoryRepository inMemoryChatMemoryRepository() {
            return new InMemoryChatMemoryRepository();
        }

        @Bean
        ChatMemory chatMemory(ChatMemoryRepository cmRepository) {
            return MessageWindowChatMemory.builder().maxMessages(20).chatMemoryRepository(cmRepository).build();
        }
    }
}
