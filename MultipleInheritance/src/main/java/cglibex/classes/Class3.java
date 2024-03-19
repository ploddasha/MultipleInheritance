package cglibex.classes;

import cglibex.Useless;

public class Class3 implements RootInterface{//Interface3{

    @Override
    @Useless
    public void first() {
        System.out.println("Useless method");
    }

    @Override
    public String second() {
        return null;
    }

    public void newMethod() {
        System.out.println("New method from 3 class");
    }
}
