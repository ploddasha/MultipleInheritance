package cglibex;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Класс-композиция для создания и хранения экземпляров каждого супер-класса.
 */
public class Composition {

    public Map<Class<?>, Object> composition;
    public Object handle;

    public Composition(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        composition = new LinkedHashMap<>();
        make(clazz);
    }

    public void make(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        MultipleInheritance an = clazz.getAnnotation(MultipleInheritance.class);
        List<Class<?>> superClasses = new ArrayList<>(List.of(an.classes()));
        Set<Class<?>> supersPumpers = new HashSet<>();

        while (true) {
            for (var superClass : superClasses) {
                var superPumperClass = superClass.getSuperclass();
                if (superPumperClass != null && !(superPumperClass.isInterface()) && !(superPumperClass == Object.class)) {
                    supersPumpers.add(superPumperClass);
                }
                addInstance(superClass);
            }
            if (supersPumpers.isEmpty()) {
                break;
            }
            superClasses.clear();
            superClasses.addAll(supersPumpers);
            supersPumpers.clear();
        }

        var constructor = clazz.getDeclaredConstructor();
        handle = constructor.newInstance() ;
    }

    private void addInstance(Class<?> superClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var constructor = superClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        composition.put(superClass, constructor.newInstance());
    }

}
