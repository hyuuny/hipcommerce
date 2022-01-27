package com.hipcommerce.config.redis;

import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisCache implements Cache {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Object getNativeCache() {
    return null;
  }

  @Override
  public ValueWrapper get(Object key) {
    return null;
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    return null;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    return null;
  }

  @Override
  public void put(Object key, Object value) {

  }

  @Override
  public void evict(Object key) {

  }

  @Override
  public void clear() {
    log.debug("모든 캐시를 삭제합니다...");
    try {
      redisTemplate.execute((RedisCallback) connection -> {
        connection.flushAll();
        return null;
      });
    } catch (Exception e) {
      log.warn("모든 캐시를 삭제하는데 실패했습니다.", e);
    }
  }
}
