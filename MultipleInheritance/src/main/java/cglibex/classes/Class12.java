package cglibex.classes;

import cglibex.Mult;
import cglibex.Useless;

@Mult(classes = {Class1.class, Class2.class,  Class3.class})
public class Class12 implements RootInterface {

    @Override
    public void first() {
        System.out.println("First method of 12 class");
    }

    @Override
    @Useless
    public String second() {
        return null;
    }

    //@Useless
    public void newMethod() {
        System.out.println("Мы не хотим, чтобы это вызывалось");
    }
}
