package com.zeng.sparkmindagent.chatMemory;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.zeng.sparkmindagent.utils.MessageSerializer;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RedisChatMemory implements ChatMemory {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public RedisChatMemory(RedisTemplate<String, Object> objectRedisTemplate){
        this.redisTemplate = objectRedisTemplate;
    }



    /**
     * 添加一个数据到Redis中
     * @param conversationId
     * @param message
     */
    @Override
    public void add(String conversationId, Message message) {
        setToRedis(conversationId,List.of(message));
    }

    /**
     * 数据存入Redis工具方法
     * @param conversationId
     * @param messages
     */
    private void setToRedis(String conversationId, List<Message> messages) {
        List<String> stringList = new ArrayList<>();
        for (Message message : messages) {
            String serialize = MessageSerializer.serialize(message);
            stringList.add(serialize);
        }
        redisTemplate.opsForValue().set(conversationId,stringList);
    }

    /**
     * 添加多条数据到Redis中
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getFromRedis(conversationId);
        messageList.addAll(messages);

        setToRedis(conversationId,messages);
    }

    /**
     * 从Redis获取数据工具方法
     * @param conversationId
     * @return
     */
    private List<Message> getFromRedis(String conversationId) {
        Object obj =  redisTemplate.opsForValue().get(conversationId);
        List<Message> messageList  = new ArrayList<>();
        if(obj != null){
            List<String> list = Convert.convert(new TypeReference<List<String>>() {
            }, obj);

            for (String s : list) {
                Message message = MessageSerializer.deserialize(s);
                messageList.add(message);
            }
        }
        return messageList;

    }

    /**
     * 从Redis获取数据
     * 获取倒数LastN条数据
     * @param conversationId
     * @param lastN
     * @return
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messageList = getFromRedis(conversationId);
        return messageList.stream()
                .skip(Math.max(0, messageList.size() - lastN))
                .toList();
    }

    /**
     * 清空数据
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(conversationId);
    }
}
