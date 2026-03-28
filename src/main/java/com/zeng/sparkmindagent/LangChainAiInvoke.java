package com.zeng.sparkmindagent;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {

        String apiKey = System.getenv("DASHSCOPE_API_KEY");
        ChatLanguageModel qwenModel = QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-max")
                .build();
        System.out.println(qwenModel.chat("if right you can see my chat so you just only need to reply 1"));

    }
}
