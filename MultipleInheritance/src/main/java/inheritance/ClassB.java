package inheritance;

public class ClassB extends SuperClass{
    @Override
    public void doSomething(){
        System.out.println("Какая-то реализация класса B");
    }
    //собственный метод класса  ClassB
    public void methodB(){
        System.out.println("Собственный метод B");
    }
}
