package org.macross.apigateway.config;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * lettuce连接池–>RedisStandaloneConfiguration-> LettuceConnectionFactory–>RedisTemplate
 */
@Configuration
public class RedisConfig {
    /**
     * 配置lettuce连接池
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig redisPool() {
        return new GenericObjectPoolConfig<>();
    }

    /**
     * 配置master
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.master")
    public RedisStandaloneConfiguration redisConfigMaster() {
        return new RedisStandaloneConfiguration();
    }

    /**
     * 配置第一个slave
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.slave1")
    public RedisStandaloneConfiguration redisConfigSlave1() {
        return new RedisStandaloneConfiguration();
    }

    /**
     * 配置第二个slave
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.slave2")
    public RedisStandaloneConfiguration redisConfigSlave2() {
        return new RedisStandaloneConfiguration();
    }


    /**
     * 配置数据源的连接工厂
     * 这里注意：需要添加@Primary 指定bean的名称，目的是为了创建不同名称的LettuceConnectionFactory
     * @param config
     * @param redisConfig
     * @return
     */
    @Bean("Master")
    @Primary
    public LettuceConnectionFactory factoryMaster(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigMaster) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigMaster, clientConfiguration);
    }


    @Bean("Slave1")
    public LettuceConnectionFactory factorySlave1(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigSlave1) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigSlave1, clientConfiguration);
    }
    @Bean("Slave2")
    public LettuceConnectionFactory factorySlave2(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisConfigSlave2) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfigSlave2, clientConfiguration);
    }


    /**
     * 配置第一个数据源的RedisTemplate
     * 注意：这里指定使用名称=factory 的 RedisConnectionFactory
     * 并且标识第一个数据源是默认数据源 @Primary
     *
     * @param factory
     * @return
     */
    @Bean("redisTemplateMaster")
    @Primary
    public RedisTemplate<String, Object> redisTemplateMaster(@Qualifier("Master") RedisConnectionFactory factory) {
        return getStringStringRedisTemplate(factory);
    }


    @Bean("redisTemplateSlave1")
    public RedisTemplate<String, Object> redisTemplateSlave1(@Qualifier("Slave1") RedisConnectionFactory factory2) {
        return getStringStringRedisTemplate(factory2);
    }

    @Bean("redisTemplateSlave2")
    public RedisTemplate<String, Object> redisTemplateSlave2(@Qualifier("Slave2") RedisConnectionFactory factory2) {
        return getStringStringRedisTemplate(factory2);
    }



    public RedisTemplate<String,Object> getStringStringRedisTemplate(RedisConnectionFactory redisConnectionFactory){

        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String,Object>();

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
