package cglibex;

import cglibex.classes.Class12;
import cglibex.classes.Class123;
import cglibex.classes.MixinInterface;
import net.sf.cglib.proxy.Mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Классы читаются из аннотации.
 * интерфейсы читаются через рефлексию, пока что с глубиной наследования 1, потом улучшим.
 * Дальше надо сделать прокси, который будет перехватывать методы объекта-наследника
 * и заменять их на вызовы сгенерированного объекта-композиции (Mixin)
 * .
 * TODO! Сейчас поддерживается наличие только одного класса с аннотацией Mult.
 * В ближайщее время доделаю, чтобы миксины в мапу складывались (или не в мапу, тут надо подумать)
 * .
 * Как методы комбинировать - самое сложное, пока изучаем эту тему.
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

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

        System.out.println(mixins.get(Class12.class).first());
        System.out.println(mixins.get(Class12.class).second());
        System.out.println(mixins.get(Class123.class).first());
        System.out.println(mixins.get(Class123.class).second());
        //System.out.println(mixins.get(Class123.class).third());

        /*
        Вот это пишет юзер.
        Но вместо этих методов через прокси вызываются аналогичные методы из mixinDelegate.

        Class12 class12 = new Class12();
        class12.first();
        class12.second();
         */

    }

}

