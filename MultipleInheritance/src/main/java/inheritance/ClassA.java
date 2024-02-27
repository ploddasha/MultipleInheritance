package inheritance;

public class ClassA extends SuperClass{
    @Override
    public void doSomething(){
        System.out.println("Какая-то реализация класса A");
    }
    //собственный метод класса  ClassA
    public void methodA(){
        System.out.println("Собственный метод A");
    }
}
