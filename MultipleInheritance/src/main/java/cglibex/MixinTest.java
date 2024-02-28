package cglibex;

import net.sf.cglib.proxy.Mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Классы читаются из аннотации.
 * TODO Интерфейсы можно читать через рефлексию.
 * Дальше надо сделать прокси, который будет перехватывать методы объекта-наследника
 * и заменять их на вызовы сгенерированного объекта-композиции (Mixin)
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        MixinInterface mixinDelegate = null;

        AccessingAllClassesInPackage allClassesInPackage = new AccessingAllClassesInPackage();
        for (var i : allClassesInPackage.findAllClassesUsingClassLoader("cglibex")) {
            if (i.isAnnotationPresent(Mult.class)) {
                Mult an = (Mult) i.getAnnotation(Mult.class);

                List<Object> objectList = new ArrayList<>();
                for (var x : an.classes()) {
                    var con = x.getDeclaredConstructor();
                    con.setAccessible(true);
                    objectList.add(con.newInstance());
                }
                Object[] objects = new Object[objectList.size()];
                for (int cnt = 0; cnt < objects.length; cnt++) {
                    objects[cnt] = objectList.get(cnt);
                }

                Mixin mixin = Mixin.create(
                    new Class[]{ Interface1.class, Interface2.class, MixinInterface.class },
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
        Но вместо этих методов через прокси вызываются аналогичные методы mixinDelegate.

        Class12 class12 = new Class12();
        class12.first();
        class12.second();
         */

    }

}

