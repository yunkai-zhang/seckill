package com.zhangyun.zseckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置类，最主要的功能就是为了实现存入redis的数据序列化
 * */
@Configuration
public class RedisConfig {

    /*
    * 实现序列化主要就是通过redisTemplate方法
    *
    * key一般是String类型，要存用户对象的话value是Object类型
    *
    * redisTemplate需要传入RedisConnectionFactory（连接工厂）
    * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //拿到redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        //设置redis 的key 的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        /*
        * 设置redis 的value 的序列化；这里不能用String了，因为value是object，得用 GenericJackson2JsonRedisSerializer；
        *
        * redis默认使用jdk的序列化（jdk序列化产出的是2进制，产生的数据比较长）；
        * Jackson2JsonRedisSerializer产生的是java字符串，需传入类对象；
        * GenericJackson2JsonRedisSerializer是通用的json转换，它序列化完成后也是序列化数据，但是不需要传入类对象（所以一般选择这个序列化器）
        * */
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //虽然hash本质也是kv形式，但是对hash类型要单独做keyvalue序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        //在拿到的redistemplate中设置连接工厂，即刚刚注入的redisConnectionFactory
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

}
