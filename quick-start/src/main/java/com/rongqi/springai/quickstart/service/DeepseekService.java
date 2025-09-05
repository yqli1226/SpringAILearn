package com.rongqi.springai.quickstart.service;


import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DeepseekService {

    private final DeepSeekChatModel deepSeekChatModel;


    public DeepseekService(DeepSeekChatModel deepSeekChatModel) {
        this.deepSeekChatModel = deepSeekChatModel;
    }


    /**
     * 同步调用 - 确保认证正确
     */
    public String call(String message) {
        return deepSeekChatModel.call(message);
    }

    /**
     * 流式调用 - 确保认证正确
     */
    public Flux<String> stream(String message) {
        // 使用 Prompt 对象而不是直接传递字符串
        Prompt prompt = new Prompt(message);
        return deepSeekChatModel.stream(prompt)
                .mapNotNull(response -> {
                    if (response.getResult() != null && response.getResult().getOutput() != null) {
                        return response.getResult().getOutput().getText();
                    }
                    return "";
                });
    }

    /**
     * 带选项的调用 - 确保认证正确
     */
    public String callWithOptions(String message, DeepSeekChatOptions options) {
        Prompt prompt = new Prompt(message, options);
        ChatResponse response = deepSeekChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    /**
     * 带选项的流式调用 - 确保认证正确
     */
    public Flux<String> streamWithOptions(String message, DeepSeekChatOptions options) {
        Prompt prompt = new Prompt(message, options);
        return deepSeekChatModel.stream(prompt)
                .mapNotNull(response -> {
                    if (response.getResult() != null && response.getResult().getOutput() != null) {
                        return response.getResult().getOutput().getText();
                    }
                    return "";
                });
    }
}
