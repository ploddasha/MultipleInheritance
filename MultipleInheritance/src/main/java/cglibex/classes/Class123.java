package cglibex.classes;

import cglibex.Mult;
import cglibex.Useless;

@Mult(classes = {Class1.class, Class2.class})//, Class3.class})
public class Class123 implements RootInterface{

    @Override
    public void first() {
        System.out.println("First method of bottom class");
    }

    @Override
    public String second() {
        return null;
    }

    //@Useless
    public String third() {
        return "Unique method of 123 class";
    }
}
