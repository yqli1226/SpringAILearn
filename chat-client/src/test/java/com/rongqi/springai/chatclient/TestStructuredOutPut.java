package com.rongqi.springai.chatclient;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class TestStructuredOutPut {

    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired DashScopeChatModel chatModel) {
        chatClient = ChatClient.builder(chatModel).build();

    }

    //布尔型结构化输出
    @Test
    public void testStructuredBoolOut() {
        Boolean isComplain = chatClient
                .prompt()
                .system("请根据用户输入判断是否投诉,只输出ture或false不做任何其他输出")
                .user("我最近发现我的快递一直不送，我投诉了快递公司，但是没有回应")
                .call()
                .entity(Boolean.class);

        if (Boolean.TRUE.equals(isComplain)) {
            System.out.println("客户是投诉，转人工处理");
        } else {
            System.out.println("客户不是投诉，自动流转客服机器人");
            //  继续调用客服ChatClient 进行对话....
        }

    }

    //实体类（收货地址）结构化输出
    @Test
    public void testStructuredEntityOut() {
        Address address = chatClient
                .prompt()
                .system("请根据用户输入提取收货地址")
                .user("收货人：张三 电话1888888888 地址湖北省武汉市洪山区xxx小区x栋x单元xxx")
                .call()
                .entity(Address.class);

        System.out.println(address);
    }

    // 实体类（收货地址）结构化输出
    public record Address(String name,
                          String phone,
                          String province,
                          String city,
                          String district,
                          String detail
    ) {}


    /**
     * 格式化输出转换器 底层实现
     * 根据结构化输出类型创建转换器
     */
    @Test
    public void testLowEntityOut(@Autowired DashScopeChatModel chatModel) {
        BeanOutputConverter<Address> converter = new BeanOutputConverter<>(Address.class);
        // 调用getFormat()获得映射
        String format = converter.getFormat();

        String user = "收货人：张三 电话1888888888 地址湖北省武汉市洪山区xxx小区x栋x单元xxx";

        String template = "请从{user}中提取收货地址 {format}";

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(template)
                .variables(Map.of("format", format, "user", user))
                .build();

        ChatResponse response = chatModel.call(
                promptTemplate.create()
        );

        String text = Optional.ofNullable(response)
                        .map(ChatResponse::getResult)
                        .map(Generation::getOutput)
                        .map(AssistantMessage::getText)
                        .orElseThrow(() -> new IllegalStateException("无法获取有效的地址信息，返回值为空"));

        Address address = converter.convert(text);

        System.out.println(address);
    }
}
