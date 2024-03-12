package cglibex;

import cglibex.classes.Class1;
import cglibex.classes.Class12;
import cglibex.classes.Class123;
import cglibex.classes.MixinInterface;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Супер классы читаются из аннотации.
 * интерфейс читается через рефлексию.
 * Пользовательские объекты создаются через ObjectsFactory
 * Методы, вызываемые у пользовательских объектов, перехватываются прокси и вместо них
 * срабатывают методы соответствующего объекта-миксина.
 * Сейчас мы упёрлись в комбинирование методов - в миксин попадает только по одному экземплляру каждого метода.
 * .
 * TODO Пока что мы не генерируем рутовый класс, а надо бы.
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

       CompObjFactory objFactory = new CompObjFactory();

        var x = (MixinInterface) objFactory.makeObject(Class12.class);
        x.first();
        //System.out.println(x.first());
        System.out.println(x.second());
        // User code

        Map<Class<?>, Object> mixins = MixinsFactory.start("cglibex.classes");
        ObjectsFactory objectsFactory = new ObjectsFactory(mixins);

        Class12 userProxy = (Class12) objectsFactory.makeObject(Class12.class);
        Class123 class123 = (Class123) objectsFactory.makeObject(Class123.class);

        //System.out.println(userProxy.first());
        //System.out.println(userProxy.second());
        //System.out.println(class123.first());
        //System.out.println(class123.second());

    }

}

