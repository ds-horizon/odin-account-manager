package com.dream11.odin.util;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class VertxUtil {

  private static final String SHARED_DATA_MAP_NAME = "__vertx.sharedDataUtils";
  private static final String SHARED_DATA_CLASS_PREFIX = "__class.";
  private static final String SHARED_DATA_DEFAULT_KEY = "__default.";

  private static final String CONTEXT_INSTANCE_PREFIX = "__vertx.contextUtils.";

  /**
   * Returns a singleton shared object across vert.x instance Note: It is your responsibility to
   * ensure T returned by supplier is thread-safe
   */
  public <T> T getOrCreateSharedData(Vertx vertx, String name, Supplier<T> supplier) {
    LocalMap<String, ThreadSafe<T>> singletons =
        vertx.sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    // LocalMap is internally backed by a ConcurrentMap
    return singletons.computeIfAbsent(name, k -> new ThreadSafe<>(supplier.get())).object();
  }

  /**
   * Helper wrapper on getOrCreate to setInstance. Note: Doesn't reset the instance if already
   * exists
   */
  public <T> void setInstanceInSharedData(Vertx vertx, T instance) {
    getOrCreateSharedData(
        vertx,
        SHARED_DATA_CLASS_PREFIX + SHARED_DATA_DEFAULT_KEY + instance.getClass().getName(),
        () -> instance);
  }

  /** Helper wrapper on getOrCreate to getInstance. */
  public <T> T getInstanceFromSharedData(Vertx vertx, Class<T> clazz) {
    return getOrCreateSharedData(
        vertx,
        SHARED_DATA_CLASS_PREFIX + SHARED_DATA_DEFAULT_KEY + clazz.getName(),
        () -> {
          throw new NoSuchElementException("Cannot find default instance of " + clazz.getName());
        });
  }

  record ThreadSafe<T>(@Getter T object) implements Shareable {}

  /** Shared across verticle instance. */
  public <T> T getInstanceFromContext(String key) {
    return io.vertx.reactivex.core.Vertx.currentContext().get(CONTEXT_INSTANCE_PREFIX + key);
  }

  /**
   * Accessible from anywhere in this verticle instance. Note: This has to be set from one of the
   * VertxThreads (may cause NullPointerException otherwise) We are intentionally avoiding
   * vertx.getOrCreateContext() to ensure better coding practices
   */
  public <T> void setInstanceInContext(T object, String key) {
    io.vertx.reactivex.core.Vertx.currentContext().put(CONTEXT_INSTANCE_PREFIX + key, object);
  }
}
