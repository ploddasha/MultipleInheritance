package cglibex;

import net.sf.cglib.proxy.Mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Классы читаются из аннотации.
 * Интерфейсы читаются через рефлексию, пока что с глубиной наследования 1, потом улучшим.
 * Дальше надо сделать прокси, который будет перехватывать методы объекта-наследника
 * и заменять их на вызовы сгенерированного объекта-композиции (Mixin)
 * .
 * TODO! Сейчас поддерживается наличие только одного класса с аннотацией Mult.
 * В ближайщее время доделаю, чтобы миксины в мапу складывались (или не в мапу, тут надо подумать)
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        MixinInterface mixinDelegate = null;

        AccessingAllClassesInPackage allClassesInPackage = new AccessingAllClassesInPackage();
        for (var currentClass : allClassesInPackage.findAllClassesUsingClassLoader("cglibex")) {
            if (currentClass.isAnnotationPresent(Mult.class)) {
                Mult an = (Mult) currentClass.getAnnotation(Mult.class);

                List<Object> objectList = new ArrayList<>();
                for (var superClass : an.classes()) {
                    var con = superClass.getDeclaredConstructor();
                    con.setAccessible(true);
                    objectList.add(con.newInstance());
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

                mixinDelegate = (MixinInterface) mixin;
            }
        }

        assert mixinDelegate != null;
        System.out.println(mixinDelegate.first());
        System.out.println(mixinDelegate.second());

        /*
        Вот это пишет юзер.
        Но вместо этих методов через прокси вызываются аналогичные методы из mixinDelegate.

        Class12 class12 = new Class12();
        class12.first();
        class12.second();
         */

    }

}

