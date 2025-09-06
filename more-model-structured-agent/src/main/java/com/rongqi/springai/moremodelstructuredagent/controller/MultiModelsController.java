package com.rongqi.springai.moremodelstructuredagent.controller;

import com.rongqi.springai.moremodelstructuredagent.common.AiJob;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


@RestController
public class MultiModelsController {

    @Autowired
    ChatClient planningChatClient;

    @Autowired
    ChatClient botChatClient;

    @GetMapping(value = "/stream", produces = "text/Stream;charset=UTF8")
    Flux<String> stream(@RequestParam String message) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        sink.tryEmitNext("正在计划任务...<br/>");

        new Thread(() -> {
            AiJob.Job job = planningChatClient
                    .prompt()
                    .user(message)
                    .call()
                    .entity(AiJob.Job.class);

            if (job == null) {
                sink.tryEmitNext("解析参数异常");
                return;
            }

            switch (job.jobType()) {
                case CANCEL -> {
                    System.out.println(job);
                    if(job.keyInfos().isEmpty()){
                        sink.tryEmitNext("请输入姓名和订单号，以进行退票业务");
                    }else{
                        // 调用 ticketService.cancel() 执行真实的退票业务
                        sink.tryEmitNext("退票成功");
                    }
                }

                case QUERY -> {
                    System.out.println(job);
                    if(job.keyInfos().isEmpty()){
                        sink.tryEmitNext("请输入姓名和订单号，以进行查询业务");
                    }else{
                        // 调用 ticketService.query() 执行真实的查票业务
                        sink.tryEmitNext("查询预定信息：xxx");
                    }
                }

                case OTHER -> {
                    System.out.println(job);
                    // 不确定的业务类型推送给bot进行陪聊
                    Flux<String> content = botChatClient.prompt().user(message).stream().content();
                    content.doOnNext(sink::tryEmitNext)
                            .doOnComplete(sink::tryEmitComplete)
                            .subscribe();

                }

                default -> {
                    System.out.println(job);
                    sink.tryEmitNext("解析失败");
                }
            }
        }).start();
        return sink.asFlux();
    }
}
