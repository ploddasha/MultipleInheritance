package framework;

import framework.examples.Class12;


/**
 * Пользователь создаёт класс, имплементирующий RootInterface
 * @ В аннотиции MultipleInheritance указывает суперклассы, от которых он хочет наследоваться.
 * Через CompositionsFactory пользователь создаёт объект своего класса:
 * var x = (RootInterface) objFactory.makeObject(Class12.class);
 * ..PS Может когда-нибудь я научусь перегружать пользовательский конструктор.
 * Под капотом создаётся объект-композиция из всех супер-классов и
 * над этой композицией делается прокси-обёртка, которая при вызове метода у пользовательского объекта
 * вызывает этот метод у каждого объекта в композиции.
 * Для void методов делает обход и вызывает этот метод для всех классов иерархии.
 * Для методов с возвращаемым значением возвращает результат первого найденного метода в иерархии.
 * @ Новая аннотация IgnoreMethod. Помечает методы, которые должны игнорироваться при обходе.
 * @ Аннотация TakeMethodFrom указывает из какого класса вызывать метод вместо обхода.
 * Обход классов идёт в ширину, слева-направо, снизу-вверх.
 * Глубина обхода - все пользовательские классы в иерархии.
 * Пока что мы генерируем рутовый класс, но не используем его (и так хорошо всё работает).
 */

public class Main {

    public static void main(String[] args) throws Exception {

        // User code

        CompositionsFactory objFactory = new CompositionsFactory();

        String packageName = "framework.examples";
        var x = (Class12) objFactory.makeObject(Class12.class, packageName);
        //x.first();
        //System.out.println(x.second());
        x.newMethod();
        //x.uniqueMethod();

    }

}

