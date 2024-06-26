package framework;

import framework.annotations.MultipleInheritance;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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

    public Composition() {
        composition = new LinkedHashMap<>();
    }

    public Composition(String clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        composition = new LinkedHashMap<>();
        make(clazz);
    }


    public Map<Class<?>, Object> getComposition() {
        return composition;
    }

    public void make(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        MultipleInheritance an = clazz.getAnnotation(MultipleInheritance.class);
        List<Class<?>> superClasses = new ArrayList<>(List.of(an.classes()));
        Set<Class<?>> supersPumpers = new HashSet<>();

        while (true) {
            for (var superClass : superClasses) {
                var superPumperClass = superClass.getSuperclass();
                if (superPumperClass != null && !(superPumperClass.isInterface()) && !(superPumperClass == Object.class) && !(Modifier.isAbstract(superPumperClass.getModifiers()))) {
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

    public void make(String className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> clazz = Class.forName(className);

        MultipleInheritance an = clazz.getAnnotation(MultipleInheritance.class);
        List<Class<?>> superClasses = new ArrayList<>(List.of(an.classes()));
        Set<Class<?>> supersPumpers = new HashSet<>();

        while (true) {
            for (var superClass : superClasses) {
                var superPumperClass = superClass.getSuperclass();
                if (superPumperClass != null && !(superPumperClass.isInterface()) && !(superPumperClass == Object.class) && !(Modifier.isAbstract(superPumperClass.getModifiers()))) {
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

        try {
            var constructor = superClass.getDeclaredConstructor();
            try {
                constructor.setAccessible(true);
            }
            catch (InaccessibleObjectException e) {
                System.err.println("InaccessibleObjectException in " + superClass.getName());
            }
            try {
                composition.put(superClass, constructor.newInstance());
            }
            catch (IllegalAccessException ie) {
                System.err.println("IllegalAccessException in " + superClass.getName());
                System.err.println("Just don't use this bad class");
            }
        } catch (NoSuchMethodException ne) {
            System.err.println("Cannot create an instance of " + superClass.getName() + " because it doesn't have constructor.");
        }

    }

}
