package framework.examples;

import framework.annotations.IgnoreMethod;

public class Class3 implements RootInterface{//Interface3{

    @Override
    @IgnoreMethod
    public void first() {
        System.out.println("Useless method");
    }

    @Override
    public String second() {
        return "Second method of third class";
    }


//    public void newMethod() {
//        System.out.println("New method from 3 class");
//    }
}
