package cglibex;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс-композиция для создания и хранения экземпляров каждого супер-класса.
 */
public class Composition {

    public Map<Class<?>, Object> composition;
    public Object rootInterface;

    public Composition(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        composition = new HashMap<>();
        make(clazz);
    }

    public void make(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Mult an = clazz.getAnnotation(Mult.class);
        for (var superClass : an.classes()) {
            var superPumperClass = superClass.getSuperclass();
            if (superPumperClass != null && !(superPumperClass.isInterface())) {
                addInstance(superPumperClass);
            }
            addInstance(superClass);
        }
        var constructor = clazz.getDeclaredConstructor();
        rootInterface = constructor.newInstance() ;
    }

    private void addInstance(Class<?> superClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var constructor = superClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        composition.put(superClass, constructor.newInstance());
    }

}
