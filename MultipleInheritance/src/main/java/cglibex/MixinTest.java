package cglibex;

import cglibex.classes.Class12;
import cglibex.classes.MixinInterface;

import java.lang.reflect.InvocationTargetException;

/**
 * Пользователь создаёт класс, имплементирующий rootInterface (у нас он называется MixinInterface,
 * наверное лучше его переименовать).
 * В аннотиции указывает суперклассы, от которых он хочет наследоваться.
 * Через CompositionsFactory пользователь создаёт объект своего класса:
 * var x = (MixinInterface) objFactory.makeObject(Class12.class);
 * ..PS Может когда-нибудь я научусь перегружать пользовательский конструктор.
 * Под капотом создаётся объект-композиция из всех супер-классов и
 * над этой композицией делается прокси-обёртка, которая при вызове метода у пользовательского объекта
 * вызывает этот метод у каждого объекта в композиции.
 * TODO Пока хорошо работает только для методов с возвращаемым значением void.
 * TODO И костыльно для String. Для всех остальных методов будет падать, там ещё надо подумать.
 * TODO Обход не в ширину а рандомный, но теоретически это не сложно доделать.
 * Пока что мы не генерируем рутовый класс, а надо бы (может и не надо).
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // User code

        CompositionsFactory objFactory = new CompositionsFactory();

        var x = (MixinInterface) objFactory.makeObject(Class12.class);
        x.first();
        System.out.println(x.second());

 /*       Map<Class<?>, Object> mixins = MixinsFactory.start("cglibex.classes");
        ObjectsFactory objectsFactory = new ObjectsFactory(mixins);

        Class12 userProxy = (Class12) objectsFactory.makeObject(Class12.class);
        Class123 class123 = (Class123) objectsFactory.makeObject(Class123.class);
*/
    }

}

