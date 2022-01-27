package com.hipcommerce.config.redis.support;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisAtomicOperator {

  private final RedisConnectionFactory redisConnectionFactory;

  private RedisOperations<String, Long> redisOperations;

  @PostConstruct
  public void postConstruct() {
    RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
    redisTemplate.setExposeConnection(true);
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.afterPropertiesSet();
    this.redisOperations = redisTemplate;
  }


  public long increment(String key) {
    RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisOperations);
    return redisAtomicLong.incrementAndGet();
  }

  public long increment(String key, long time, TimeUnit timeUnit) {
    RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisOperations);
    redisAtomicLong.expire(time, timeUnit);
    return redisAtomicLong.incrementAndGet();
  }

  public long increment(String key, Instant expireAt) {
    RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisOperations);
    redisAtomicLong.expireAt(expireAt);
    return redisAtomicLong.incrementAndGet();
  }

  public long increment(String key, Date expireAt) {
    RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisOperations);
    redisAtomicLong.expireAt(expireAt);
    return redisAtomicLong.incrementAndGet();
  }

  public long increment(String key, int increment, long time, TimeUnit timeUnit) {
    RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisOperations);
    redisAtomicLong.expire(time, timeUnit);
    return redisAtomicLong.incrementAndGet();
  }

}
