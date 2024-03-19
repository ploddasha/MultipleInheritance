package cglibex;

import cglibex.classes.Class12;
import cglibex.classes.Class123;
import cglibex.classes.Class3;
import cglibex.classes.RootInterface;
import javassist.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Пользователь создаёт класс, имплементирующий RootInterface
 * @ В аннотиции Mult указывает суперклассы, от которых он хочет наследоваться.
 * Через CompositionsFactory пользователь создаёт объект своего класса:
 * var x = (RootInterface) objFactory.makeObject(Class12.class);
 * ..PS Может когда-нибудь я научусь перегружать пользовательский конструктор.
 * Под капотом создаётся объект-композиция из всех супер-классов и
 * над этой композицией делается прокси-обёртка, которая при вызове метода у пользовательского объекта
 * вызывает этот метод у каждого объекта в композиции.
 * Для void методов делает обход и вызывает этот метод для всех классов иерархии.
 * Для методов с возвращаемым значением возвращает результат первого найденного метода в иерархии.
 * @ Новая аннотация Useless. Помечает методы, которые должны игнорироваться при обходе.
 * Обход лассов идёт в ширину, слева-направо, снизу-вверх.
 * Глубина обхода - все пользовательские классы в иерархии.
 * Пока что мы не генерируем рутовый класс, а надо бы (может и не надо).
 */

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException {

        // User code

        CompositionsFactory objFactory = new CompositionsFactory();

        var x = (RootInterface) objFactory.makeObject(Class12.class);
        //x.first();
        //System.out.println(x.second());
        //var x = (Class12) objFactory.makeObject(Class12.class);
        //x.newMethod();
        //x.first();
        //System.out.println(x.second());

        var y = (Class123) objFactory.makeObject(Class123.class);
        //y.third();
        //y.first();

    }

}

