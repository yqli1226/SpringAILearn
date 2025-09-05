package com.rongqi.springai.quickstart;

import com.rongqi.springai.quickstart.service.DeepseekService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestDeepseek {

    @Autowired
    DeepseekService deepseekService;

    @Test
    public void testDeepseekCall() {
        String callback = deepseekService.call("你好你是谁？");
        System.out.println(callback);
    }

    @Test
    public void testDeepseekStream() {
        Flux<String> stream = deepseekService.stream("你好你是谁？");
        stream.toIterable().forEach(System.out::println);
    }

    @Test
    public void testDeepseekCallWithOptions() {
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.builder().temperature(1.9d).model("deepseek-chat").build();
        String callback = deepseekService.callWithOptions("请写一首关于反法西斯战争胜利80周年的诗", deepSeekChatOptions);
        System.out.println(callback);
    }

    @Test
    public void testDeepseekStreamWithOptions() {
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.builder().temperature(1.9d).model("deepseek-chat").build();
        Flux<String> stream = deepseekService.streamWithOptions("请写一首关于反法西斯战争胜利80周年的诗", deepSeekChatOptions);
        stream.toIterable().forEach(System.out::println);
    }

}
