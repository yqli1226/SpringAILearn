package com.rongqi.springai.chatclient;

import com.rongqi.springai.chatclient.advisors.ReReadingAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestAdvisor {

    private static final String CUSTOM_FAILURE_RESPONSE = "很抱歉！你的问询包含敏感词，我为此提供帮助！";


    ChatClient chatClient;

    @BeforeEach
    public void setUp(@Autowired ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    //设置基座拦截器
    @Test
    public void testBaseAdvisor(@Autowired ChatClient.Builder builder) {
        ChatClient chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        // 模拟调用
        String callback = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(callback);
    }


    // 安全词拦截器
    @Test
    public void testSafeGuardAdvisor(){
        String callback = chatClient.prompt()
                .advisors(new SafeGuardAdvisor(List.of("cnm")))
                .user("你知道，足球运动中退钱哥的梗吗？就是cnm退钱")
                .call()
                .content();

        System.out.println(callback);
    }

    // 自定义安全拦截器返回词
    @Test
    public void testSafeGuardAdvisorCustomResponse(){
        String callback = chatClient.prompt()
                .advisors(new SafeGuardAdvisor(List.of("cnm"), CUSTOM_FAILURE_RESPONSE, 0))
                .user("你知道，足球运动中退钱哥的梗吗？就是cnm退钱")
                .call()
                .content();

        System.out.println(callback);
    }


    // 日志拦截器
    @Test
    public void testSimpleLoggerAdvisor(){
        String callback = chatClient.prompt()
                .user("你好")
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();

        System.out.println(callback);
    }

    //自定义重读拦截器
    @Test
    public void testReReadingAdvisor() {
        String callback = chatClient.prompt()
                .user("你好")
                .advisors(new SimpleLoggerAdvisor(),
                        new ReReadingAdvisor(),
                        new SimpleLoggerAdvisor())
                .call()
                .content();

        System.out.println(callback);
    }

}
