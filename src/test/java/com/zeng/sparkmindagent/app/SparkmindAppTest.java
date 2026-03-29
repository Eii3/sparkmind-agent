package com.zeng.sparkmindagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SparkmindAppTest {
    @Resource
    private SparkmindApp sparkmindApp;

    @Test
    void testChat(){
        String chatId = UUID.randomUUID().toString();

        String message = "hello i am zeng!";
        String answer = sparkmindApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "my favorite movie is zootopia";
        answer = sparkmindApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "wait what's my name?";
        answer = sparkmindApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
