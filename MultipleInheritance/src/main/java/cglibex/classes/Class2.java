package cglibex.classes;

public class Class2 implements Interface2 {
    @Override
    public void first() {
        System.out.println("First method of second class");
    }

    @Override
    public String second() {
        return "second behaviour";
    }
}
