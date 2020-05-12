package org.sdoroshenko;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ApplicationContext {
    private ObjectFactory factory;
    private Map<Class, Object> cache = new ConcurrentHashMap<>();
    private Config config;

    public ApplicationContext(Config config) {
        this.config = config;
    }

    public <T> T getObject(Class<T> type) {
        try {
            if (cache.containsKey(type)) {
                return (T) cache.get(type);
            }

            Class<? extends T> implClass = type;

            if (type.isInterface()) {
                implClass = config.getImplClass(type);
            }
            T t = factory.createObject(implClass);

            if (implClass.isAnnotationPresent(Singleton.class)) {
                cache.put(type, t);
            }

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFactory(final ObjectFactory factory) {
        this.factory = factory;
    }

    public Config getConfig() {
        return this.config;
    }
}
