package com.zeng.sparkmindagent.controller;

import com.zeng.sparkmindagent.app.SparkmindApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private SparkmindApp sparkmindApp;

    @Resource
    private ChatModel dashscopeChatModel;

    @GetMapping("/sparkmind_app/chat/sync")
    public String doChatWithSparkmindAppSync(String message, String chatId) {
        return sparkmindApp.doChat(message, chatId);
    }

    /**
     * 返回Flux对象
     * 设置泛型为 ServerSentEvent
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/sparkmind_app/chat/sse")
    public Flux<ServerSentEvent<String>> doChatWithSparkmindAppSSE(String message, String chatId) {
        return sparkmindApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}
