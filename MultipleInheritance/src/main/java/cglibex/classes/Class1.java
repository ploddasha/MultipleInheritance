package cglibex.classes;

public class Class1 implements Interface1 {

    private Class1() {

    }

    @Override
    public void first() {
        System.out.println("First method of first class");
    }

    @Override
    public String second() {
        return null;
    }
}
