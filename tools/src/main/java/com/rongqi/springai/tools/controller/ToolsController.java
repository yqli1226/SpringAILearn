package com.rongqi.springai.tools.controller;

import com.rongqi.springai.tools.service.ToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolsController {

    ChatClient client;

    ChatMemory chatMemory;

    public ToolsController(ChatClient.Builder builder, ChatMemory chatMemory, ToolService toolService){
        this.chatMemory = chatMemory;
        this.client = builder
                .defaultTools(toolService)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @RequestMapping("/tool")
    public String tool(@RequestParam(value = "message") String message,
                       @RequestParam(value = "userConversationId") String userConversationId){
        return client.prompt().user(message).advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userConversationId)).call().content();
    }
}
