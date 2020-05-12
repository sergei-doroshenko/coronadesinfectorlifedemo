package org.sdoroshenko;


public interface ObjectConfigurator {
    void configure(Object t,ApplicationContext context) throws IllegalAccessException;
}
