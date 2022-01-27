package com.hipcommerce.config.redis;

import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Slf4j
@RequiredArgsConstructor
@EnableCaching
@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration redisConfiguration = RedisCacheConfiguration.defaultCacheConfig()
      .disableCachingNullValues()
      .entryTtl(Duration.ofMinutes(60))
      .computePrefixWith(CacheKeyPrefix.simple())
      .serializeValuesWith(
        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

    Map<String, RedisCacheConfiguration> cacheConfigurations = Maps.newHashMap();

    return RedisCacheManager.RedisCacheManagerBuilder
      .fromConnectionFactory(connectionFactory)
      .cacheDefaults(redisConfiguration)
      .withInitialCacheConfigurations(cacheConfigurations)
      .build();
  }

  @Bean
  public CustomKeyGenerator customKeyGenerator() {
    return new CustomKeyGenerator();
  }

  public static class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
      // class name. method name. parameter value
      String keySuffix =
        target.getClass().getSimpleName() + "." + method.getName() + "." + Arrays.toString(params);
      log.info("Cache key suffix : {}", keySuffix);
      return keySuffix;
    }

  }

}
