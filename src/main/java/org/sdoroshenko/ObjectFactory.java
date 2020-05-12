package org.sdoroshenko;

import javax.annotation.PostConstruct;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;


public class ObjectFactory {
    private final ApplicationContext context;
    private List<ObjectConfigurator> configurators = new ArrayList<>();
    private List<ProxyConfigurator> proxyConfigurators = new ArrayList<>();

    public ObjectFactory(ApplicationContext context) {
        try {
            this.context = context;
            for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
                configurators.add(aClass.getDeclaredConstructor().newInstance());
            }
            for (Class<? extends ProxyConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ProxyConfigurator.class)) {
                proxyConfigurators.add(aClass.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T createObject(Class<T> implClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        T t = create(implClass);

        configure(t);

        invokeInit(implClass, t);

        t = wrapWithProxyIfNeeded(implClass, t);

        return t;

    }

    private <T> T wrapWithProxyIfNeeded(Class<T> implClass, T t) {
        for (ProxyConfigurator proxyConfigurator : proxyConfigurators) {
            t = (T)proxyConfigurator.replaceWithProxyIfNeeded(t, implClass);
        }
        return t;
    }

    private <T> void invokeInit(Class<T> implClass, T t) throws IllegalAccessException, InvocationTargetException {
        for (Method method : implClass.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(t);
            }
        }
    }

    private <T> void configure(T t) {
        configurators.forEach(objectConfigurator -> {
            try {
                objectConfigurator.configure(t, context);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> T create(Class<T> implClass) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        return implClass.getDeclaredConstructor().newInstance();
    }
}




