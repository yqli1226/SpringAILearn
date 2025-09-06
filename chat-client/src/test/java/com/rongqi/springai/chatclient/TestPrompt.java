package com.rongqi.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

@SpringBootTest
public class TestPrompt {

    //系统提示词模板
    @Test
    public void testSystemPrompt(@Autowired ChatClient.Builder builder) {
        ChatClient chatClient = builder
                .defaultSystem("""
                        # 你是一名专业的java开发工程师AI
                        ## 回复时参考一下流程
                        ### 分析问题
                        ### 相关依据
                        ### 建议梳理
                        ### 如果没有95%的把握，询问待补充项
                        以下是当前对话额外信息
                        当前用户姓名{name}, 年龄{age}
                        """)
                .build();

        String callback = chatClient.prompt()
                .user("你好，你知道我是谁吗？")
                .system(p -> p.param("name", "rongqi").param("age", 18))
                .call().content();

        System.out.println(callback);

    }

    /**
     * 突然发现的好玩的地方，不加对用户可见他会装作不认识用户 （有概率触发）
     * @param builder
     */
    @Test
    public void testSystemPrompt2(@Autowired ChatClient.Builder builder) {
        ChatClient chatClient = builder
                .defaultSystem("""
                        # 你是一名专业的java开发工程师AI
                        ## 回复时参考一下流程
                        ### 分析问题
                        ### 相关依据
                        ### 建议梳理
                        ### 如果没有95%的把握，询问待补充项
                        以下是当前对话额外信息，对用户可见
                        当前用户姓名{name}, 年龄{age}
                        """)
                .build();

        String callback = chatClient.prompt()
                .user("你好，你知道我是谁吗？")
                .system(p -> p.param("name", "rongqi").param("age", 18))
                .call().content();

        System.out.println(callback);

    }

    // 伪系统提示词
    @Test
    public void testSystemPrompt3(@Autowired ChatClient.Builder builder) {
        //为chatClient设置系统提示词
        //为当前的对话代理预设角色： 告诉大模型你是什么 做什么 怎么做 注意什么
        ChatClient client = builder
                .defaultSystem("1")
                .build();
        String content = client.prompt()
                .user(u -> u.text("""
                        #角色说明
                        你是一名专业的程序员AI
                        ##回复格式
                        1.问题分析
                        2.相关依据
                        3.梳理建议
                        4.如果没有95%的把握，询问待补充项
                        5.顶层视角，行业前1%的人如何看待问题
                        当前的用户问题为{question}
                        """).param("question", "你好，请问你能做点什么？"))
//                .system("")  //只为当前对话设置系统提示词
                .system(p -> p.param("name","rongqi").param("age", "18"))
                .call().content();
        System.out.println(content);
    }


    //系统提示词模板--配置项维护
    @Test
    public void testSystemPromptFromProperties(@Autowired ChatClient.Builder builder,
            @Value("classpath:/files/prompt.st") Resource promptFile){
        //为chatClient设置系统提示词
        //为当前的对话代理预设角色： 告诉大模型你是什么 做什么 怎么做 注意什么
        ChatClient client = builder
                .defaultSystem(promptFile)
                .build();
        String content = client.prompt()
                .user("你好，你知道我是谁吗")
//                .system("")  //只为当前对话设置系统提示词
                .system(p -> p.param("name","rongqi").param("age", "18"))
                .call().content();
        System.out.println(content);


    }

}
