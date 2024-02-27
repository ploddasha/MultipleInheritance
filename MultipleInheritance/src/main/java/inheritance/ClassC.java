package inheritance;

public class ClassC{

    ClassA objA = new ClassA();
    ClassB objB = new ClassB();

    public void test(){
        objA.doSomething();
    }

    public void methodA(){
        objA.methodA();
    }

    public void methodB(){
        objB.methodB();
    }


}
