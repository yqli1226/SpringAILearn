package com.rongqi.springai.quickstart.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;



@Service
public class OllamaService {

    private final OllamaChatModel ollamaChatModel;


    public OllamaService(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    public String call(String message) {
        return ollamaChatModel.call(message);
    }

    public Flux<String> stream(String message) {
        Prompt prompt = new Prompt(message);
        return ollamaChatModel.stream(prompt)
                .mapNotNull(response ->{
                  if(response.getResult() !=null && response.getResult().getOutput() != null){
                      return response.getResult().getOutput().getText();
                  }
                  return null;
                });
    }

    public ChatResponse multiModality(Media media, String textContent, OllamaOptions options) {
        Prompt prompt = new Prompt(UserMessage.builder()
                .media(media)
                .text(textContent)
                .build(), options);
        return ollamaChatModel.call(prompt);
    }
}
