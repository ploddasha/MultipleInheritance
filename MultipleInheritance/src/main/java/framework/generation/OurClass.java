package framework.generation;

import java.util.LinkedHashMap;
import java.util.Map;

public class OurClass {
    public Map<Class<?>, Object> getClasses() {
        Map<Class<?>, Object> classes = new LinkedHashMap<>();
        ClassC classC = new ClassC();
        classes.put(ClassC.class, classC);
        return classes;
    }

    public void print() {
        System.out.println("hello world");
    }
}
