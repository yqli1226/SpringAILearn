package com.rongqi.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestChatClient {

    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClient) {
        ChatClient client = chatClient.build();
        String content = client.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 多模型情况下使用
     * 【注解】Autowired ***ChatModel
     * ChatClient client = ChatClient.builder(***ChatModel).build();
     */
    @Test
    public void testStreamChatClient(@Autowired ChatClient.Builder chatClient) {
        ChatClient client = chatClient.build();
        Flux<String> content = client.prompt()
                .user("你好")
                .stream()
                .content();
        content.toIterable().forEach(System.out::println);
    }

}