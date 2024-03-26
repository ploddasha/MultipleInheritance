package cglibex;

import net.sf.cglib.proxy.Mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MixinsFactory {

    public static Map<Class<?>, Object> start(String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<Class<?>, Object> mixins = new HashMap<>();

        AccessingAllClassesInPackage allClassesInPackage = new AccessingAllClassesInPackage();
        for (var currentClass : allClassesInPackage.findAllClassesUsingClassLoader(packageName)) {

            if (currentClass.isAnnotationPresent(MultipleInheritance.class)) {
                MultipleInheritance an = (MultipleInheritance) currentClass.getAnnotation(MultipleInheritance.class);

                List<Object> objectList = new ArrayList<>();
                for (var superClass : an.classes()) {
                    var constructor = superClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    objectList.add(constructor.newInstance());
                }
                Object[] objects = new Object[objectList.size()];
                for (int cnt = 0; cnt < objects.length; cnt++) {
                    objects[cnt] = objectList.get(cnt);
                }

                List<Class> interfacesList = new ArrayList<>(List.of(currentClass.getInterfaces()));

                Class<?>[] interfaces = new Class[interfacesList.size()];
                for (int i = 0; i < interfaces.length; i++) {
                    interfaces[i] = interfacesList.get(i);
                }

                Mixin mixin = Mixin.create(
                    interfaces,
                    objects
                );
                // Вот тут сильно подумать надо как сделать универсальный конструктор миксинов.
                mixins.put(currentClass,  currentClass.getInterfaces()[0].cast(mixin));
            }
        }

        return mixins;
    }

}
