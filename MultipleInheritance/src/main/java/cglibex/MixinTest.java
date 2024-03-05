package cglibex;

import cglibex.classes.Class12;
import cglibex.classes.Class123;
import cglibex.classes.MixinInterface;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.Mixin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Классы читаются из аннотации.
 * интерфейсы читаются через рефлексию, пока что с глубиной наследования 1, потом улучшим.
 * Дальше надо сделать прокси, который будет перехватывать методы объекта-наследника
 * и заменять их на вызовы сгенерированного объекта-композиции (Mixin)
 * .
 * TODO! Сейчас поддерживается наличие нескольких классов с аннотацией Mult, но все они имплементируют один интерфейс.
 * Надо
 * .
 * Как методы комбинировать - самое сложное, пока изучаем эту тему.
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Map<Class, MixinInterface> mixins = MixinsFactory.start();
        ObjectsFactory classesFactory = new ObjectsFactory(mixins);

        Class12 userProxy = (Class12) classesFactory.makeClass(Class12.class);
        Class123 class123 = (Class123) classesFactory.makeClass(Class123.class);

        System.out.println(userProxy.first());
        System.out.println(userProxy.second());
        System.out.println(class123.first());
        System.out.println(class123.second());


        //System.out.println(mixins.get(Class12.class).first());
        //System.out.println(mixins.get(Class12.class).second());
        //System.out.println(mixins.get(Class123.class).first());
        //System.out.println(mixins.get(Class123.class).second());
        //System.out.println(mixins.get(Class123.class).third());

        /*
        Это были мои мечты, на самом деле всё немного грустнее, но может сейчас получится чего.
        Вот это пишет юзер.
        Но вместо этих методов через прокси вызываются аналогичные методы из mixinDelegate.

        Class12 class12 = new Class12();
        class12.first();
        class12.second();
         */

    }

}

