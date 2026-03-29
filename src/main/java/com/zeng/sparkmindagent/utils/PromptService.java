package com.zeng.sparkmindagent.utils;

import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PromptService {
    public static final String SYSTEM_PROMPT_PATH = "/prompts/system-prompt.txt";

    public static final String SYSTEM_PROMPT;

    static {
        try {
            // 使用类加载器读取 resources/prompts/system-prompt.txt
            var inputStream = PromptService.class.getResourceAsStream(SYSTEM_PROMPT_PATH);
            if (inputStream == null) {
                throw new RuntimeException("Prompt file not found: "+SYSTEM_PROMPT_PATH);
            }
            SYSTEM_PROMPT = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load system prompt", e);
        }
    }
}