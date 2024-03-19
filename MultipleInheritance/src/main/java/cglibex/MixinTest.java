package cglibex;

import cglibex.classes.Class12;
import cglibex.classes.Class123;
import cglibex.classes.MixinInterface;
import javassist.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Пользователь создаёт класс, имплементирующий rootInterface (у нас он называется MixinInterface,
 * наверное лучше его переименовать).
 * @ В аннотиции Mult указывает суперклассы, от которых он хочет наследоваться.
 * Через CompositionsFactory пользователь создаёт объект своего класса:
 * var x = (MixinInterface) objFactory.makeObject(Class12.class);
 * ..PS Может когда-нибудь я научусь перегружать пользовательский конструктор.
 * Под капотом создаётся объект-композиция из всех супер-классов и
 * над этой композицией делается прокси-обёртка, которая при вызове метода у пользовательского объекта
 * вызывает этот метод у каждого объекта в композиции.
 * Для void методов делает обход и вызывает этот метод для всех классов иерархии.
 * Для методов с возвращаемым значением возвращает результат первого найденного метода в иерархии.
 * @ Новая аннотация Useless. Помечает методы, которые должны игнорироваться при обходе.
 * TODO Обход не в ширину а рандомный, но теоретически это не сложно доделать.
 * TODO Сделать глубину поиска суперклассов при создании объекта больше 2.
 * Пока что мы не генерируем рутовый класс, а надо бы (может и не надо).
 */

public class MixinTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException {


        // User code

        CompositionsFactory objFactory = new CompositionsFactory();

        //var x = (MixinInterface) objFactory.makeObject(Class12.class);
        //x.first();
        //System.out.println(x.second());

        var y = (Class123) objFactory.makeObject(Class123.class);
        y.first();
        System.out.println(y.second());
        System.out.println(y.third());
        //x.first();
        //y.first();

 /*       Map<Class<?>, Object> mixins = MixinsFactory.start("cglibex.classes");
        ObjectsFactory objectsFactory = new ObjectsFactory(mixins);

        Class12 userProxy = (Class12) objectsFactory.makeObject(Class12.class);
*/
    }

}

