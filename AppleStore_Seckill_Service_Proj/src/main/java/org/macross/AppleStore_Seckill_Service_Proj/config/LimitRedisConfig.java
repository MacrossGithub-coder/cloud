package org.macross.AppleStore_Seckill_Service_Proj.config;

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
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 限流Redis配置，database:2
 * 秒杀业务Redis配置
 * lettuce连接池–>RedisStandaloneConfiguration-> LettuceConnectionFactory–>RedisTemplate
 */
@Configuration
public class LimitRedisConfig {

    /**
     * 配置master
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.limit")
    public RedisStandaloneConfiguration redisConfigMasterLimit() {
        return new RedisStandaloneConfiguration();
    }


    /**
     * 配置数据源的连接工厂
     * @param config
     * @param redisConfig
     * @return
     */
    @Bean("MasterLimit")
    public LettuceConnectionFactory factoryMasterLimit(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigMasterLimit) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigMasterLimit, clientConfiguration);
    }


    /**
     * redisTemplateMasterLimit
     * @param factory
     * @return
     */
    @Bean("redisTemplateMasterLimit")
    public StringRedisTemplate redisTemplateMasterLimit(@Qualifier("MasterLimit") RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

}
