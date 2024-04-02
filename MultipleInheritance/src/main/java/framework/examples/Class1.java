package framework.examples;

public class Class1 extends Class0 {

    private Class1() {

    }

    @Override
    public void first() {
        System.out.println("First method of first class");
    }

    @Override
    public String second() {
        return "Second method from first class";
    }

    public void newMethod() {
        System.out.println("New method from 1 class");
    }
}
