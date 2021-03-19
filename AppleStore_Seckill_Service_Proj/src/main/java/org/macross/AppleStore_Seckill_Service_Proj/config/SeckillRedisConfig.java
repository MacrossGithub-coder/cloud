package org.macross.AppleStore_Seckill_Service_Proj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 秒杀业务Redis配置
 * lettuce连接池–>RedisStandaloneConfiguration-> LettuceConnectionFactory–>RedisTemplate
 */
@Configuration
public class SeckillRedisConfig {

    /**
     * 配置master
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.master.seckill")
    public RedisStandaloneConfiguration redisConfigMasterSeckill() {
        return new RedisStandaloneConfiguration();
    }

    /**
     * 配置第一个slave
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.slave1.seckill")
    public RedisStandaloneConfiguration redisConfigSlave1Seckill() {
        return new RedisStandaloneConfiguration();
    }

    /**
     * 配置第二个slave
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.slave2.seckill")
    public RedisStandaloneConfiguration redisConfigSlave2Seckill() {
        return new RedisStandaloneConfiguration();
    }


    /**
     * 配置数据源的连接工厂
     *
     * @param config
     * @param redisConfig
     * @return
     */
    @Bean("MasterSeckill")
    public LettuceConnectionFactory factoryMasterSeckill(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigMasterSeckill) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigMasterSeckill, clientConfiguration);
    }

    @Bean("Slave1Seckill")
    public LettuceConnectionFactory factorySlave1Seckill(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigSlave1Seckill) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigSlave1Seckill, clientConfiguration);
    }

    @Bean("Slave2Seckill")
    public LettuceConnectionFactory factorySlave2Seckill(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigSlave1Seckill) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigSlave1Seckill, clientConfiguration);
    }


    /**
     * 配置第一个数据源的RedisTemplate
     * 注意：这里指定使用名称=factory 的 RedisConnectionFactory
     * 并且标识第一个数据源是默认数据源 @Primary
     *
     * @param factory
     * @return
     */
    @Bean("redisTemplateMasterSeckill")
    public RedisTemplate<String, Object> redisTemplateMasterSeckill(@Qualifier("MasterSeckill") RedisConnectionFactory factory) {
        return getStringStringRedisTemplate(factory);
    }


    @Bean("redisTemplateSlave1Seckill")
    public RedisTemplate<String, Object> redisTemplateSlave1Seckill(@Qualifier("Slave1Seckill") RedisConnectionFactory factory2) {
        return getStringStringRedisTemplate(factory2);
    }

    @Bean("redisTemplateSlave2Seckill")
    public RedisTemplate<String, Object> redisTemplateSlave2Seckill(@Qualifier("Slave2Seckill") RedisConnectionFactory factory2) {
        return getStringStringRedisTemplate(factory2);
    }


    private RedisTemplate<String, Object> getStringStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //json序列化设置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        //String序列化设置
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //Hash采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //value的序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //Hash value的序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
