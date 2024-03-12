package cglibex;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Composition {

    public Map<Class<?>, Object> composition;
    public Object rootInterface;

    public Composition(Class<?> clazz, Class<?> interface1) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        composition = new HashMap<>();
        make(clazz, interface1);
    }

    public void make(Class<?> clazz, Class<?> interface1) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Mult an = clazz.getAnnotation(Mult.class);
        for (var superClass : an.classes()) {
            var constructor = superClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            composition.put(superClass, constructor.newInstance());
        }
        var constructor = interface1.getDeclaredConstructor();
        rootInterface = constructor.newInstance() ;
    }

}
