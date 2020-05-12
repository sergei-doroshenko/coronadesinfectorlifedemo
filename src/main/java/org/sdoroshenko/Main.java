package org.sdoroshenko;

import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        Map<Class, Class> configMap = new HashMap<>();
        configMap.put(Policeman.class, PolicemanImpl.class);
        ApplicationContext context = Application.run("org.sdoroshenko", configMap);
        CoronaDesinfector desinfector = context.getObject(CoronaDesinfector.class);
        desinfector.start(new Room());
    }
}
