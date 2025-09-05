package com.rongqi.springai.quickstart.service;


import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;


@Service
public class AliDashScopeService {
    @Value("${spring.ai.deepseek.api-key}")
    private String apikey;

    private final DashScopeChatModel dashScopeChatModel;

    private final DashScopeImageModel dashScopeImageModel;

    private final DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel;

    private final DashScopeAudioTranscriptionModel dashScopeAudioTranscriptionModel;

    /**
     * 项目是了解springAI框架 为了快速使用这里不做功能解耦
     */
    public AliDashScopeService(DashScopeChatModel dashScopeChatModel, DashScopeImageModel dashScopeImageModel, DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel, DashScopeAudioTranscriptionModel dashScopeAudioTranscriptionModel) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.dashScopeImageModel = dashScopeImageModel;
        this.dashScopeSpeechSynthesisModel = dashScopeSpeechSynthesisModel;
        this.dashScopeAudioTranscriptionModel = dashScopeAudioTranscriptionModel;
    }

    public String call(String message) {
        return dashScopeChatModel.call(message);
    }

    public Flux<String> stream(String message) {
        Prompt prompt = new Prompt(message);
        return dashScopeChatModel.stream(prompt)
                .mapNotNull(response -> {
                    if(response.getResult() != null && response.getResult().getOutput() != null){
                        return response.getResult().getOutput().getText();
                    }
                    return null;
                });
    }

    /**
     * 提供文生图基础能力
     */
    public ImageResponse text2Img(String message, DashScopeImageOptions dashScopeImageOptions) {
        ImagePrompt imagePrompt = new ImagePrompt(message, dashScopeImageOptions);
        return dashScopeImageModel.call(imagePrompt);
    }

    /**
     * 提供文生语音基本能力
     */
    public SpeechSynthesisResponse text2Audio(String message, DashScopeSpeechSynthesisOptions dashScopeSpeechSynthesisOptions){
        SpeechSynthesisPrompt prompt = new SpeechSynthesisPrompt(message, dashScopeSpeechSynthesisOptions);
        return dashScopeSpeechSynthesisModel.call(prompt);

    }

    /**
     * 提供语音翻译文本基本能力
     */
    public AudioTranscriptionResponse audio2Transcription(UrlResource urlResource, DashScopeAudioTranscriptionOptions options) {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(urlResource, options);
        return dashScopeAudioTranscriptionModel.call(prompt);
    }

    /**
     * 多模态 图片识别
     */
    public ChatResponse useMultiModel(Media media, String textContent) {
        DashScopeChatOptions options = DashScopeChatOptions.builder().withMultiModel(true).withModel("qwen-vl-max-latest").build();
        Prompt prompt = Prompt.builder()
                .chatOptions(options)
                .messages(UserMessage.builder().media(media).text(textContent).build())
                .build();
        return dashScopeChatModel.call(prompt);
    }

    /**
     * 提供文生视频能力
     */
    public VideoSynthesisResult text2Video(String message, String model) throws ApiException, NoApiKeyException, InputRequiredException {
        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .model(StringUtils.hasText(model) ? model : "wanx2.1-t2v-turbo")
                        .prompt(message)
                        .size("1280*720")
                        .apiKey(apikey)
                        .build();
        return vs.call(param);
    }
}
