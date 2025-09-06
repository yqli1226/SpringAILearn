package com.rongqi.springai.moreplatformandmodel.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.rongqi.springai.moreplatformandmodel.common.MorePlatformAndModelOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@RestController
public class MorePlatformAndModelController {
    HashMap<String, ChatModel> platformAndModel = new HashMap<>();

    public MorePlatformAndModelController(
            DeepSeekChatModel deepSeekChatModel,
            DashScopeChatModel dashScopeChatModel,
            OllamaChatModel ollamaChatModel) {

        platformAndModel.put("deepSeek", deepSeekChatModel);
        platformAndModel.put("dashScope", dashScopeChatModel);
        platformAndModel.put("ollama", ollamaChatModel);
    }

    @RequestMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat (String message, MorePlatformAndModelOptions options) {
        String platform = options.getPlatform();
        ChatModel chatModel = platformAndModel.get(platform);

        ChatClient.Builder builder = ChatClient.builder(chatModel);

        ChatClient client = builder
                .defaultOptions(
                        ChatOptions.builder()
                                .temperature(options.getTemperatureOrDefault())
                                .model(options.getModel())
                                .build()
                ).build();

        return client.prompt().user(message).stream().content();
    }
}
