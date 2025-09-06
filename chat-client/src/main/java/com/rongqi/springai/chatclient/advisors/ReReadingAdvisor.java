package com.rongqi.springai.chatclient.advisors;


import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;
import java.util.Optional;

public class ReReadingAdvisor implements BaseAdvisor {

    private static final String RE_READING_USER_ADVISOR_TEMPLATE = """
            {input_query}
            再次读取：{input_query}
            """;


    /**
     * 请求前重写提示词，增强询问
     * @param chatClientRequest
     * @param advisorChain
     * @return
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String content = Optional.ofNullable(chatClientRequest)
                .map(ChatClientRequest::prompt)
                .map(Prompt::getContents)
                .orElse("");

        String inputQuery = PromptTemplate.builder()
                .template(RE_READING_USER_ADVISOR_TEMPLATE)
                .build()
                .render(Map.of("input_query", content));

        return chatClientRequest.mutate().prompt(Prompt.builder().content(inputQuery).build()).build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
