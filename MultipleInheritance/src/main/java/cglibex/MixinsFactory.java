package cglibex;

import cglibex.classes.MixinInterface;
import net.sf.cglib.proxy.Mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MixinsFactory {

    public static Map<Class, MixinInterface> start() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<Class, MixinInterface> mixins = new HashMap<>();

        AccessingAllClassesInPackage allClassesInPackage = new AccessingAllClassesInPackage();
        for (var currentClass : allClassesInPackage.findAllClassesUsingClassLoader("cglibex.classes")) {

            if (currentClass.isAnnotationPresent(Mult.class)) {
                Mult an = (Mult) currentClass.getAnnotation(Mult.class);

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

                List<Class> interfacesList = new ArrayList<>();

                for (var currentInterface : currentClass.getInterfaces()) {
                    interfacesList.addAll(Arrays.asList(currentInterface.getInterfaces()));
                    interfacesList.add(currentInterface);
                }

                Class[] interfaces = new Class[interfacesList.size()];
                for (int i = 0; i < interfaces.length; i++) {
                    interfaces[i] = interfacesList.get(i);
                }

                Mixin mixin = Mixin.create(
                    interfaces,
                    objects
                );
                // Вот тут сильно подумать надо как сделать универсальный конструктор миксинов.
                mixins.put(currentClass, (MixinInterface) currentClass.getInterfaces()[0].cast(mixin));
            }
        }

        return mixins;
    }

}
