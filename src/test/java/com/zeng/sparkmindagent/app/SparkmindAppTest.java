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

        String message = "你还记得我叫什么名字吗？";
        String answer = sparkmindApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

    }
}
