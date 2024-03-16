package cglibex.classes;

import cglibex.Mult;

@Mult(classes = {Class1.class, Class2.class,  Class3.class})
public class Class12 implements MixinInterface {

    @Override
    public void first() {
        System.out.println("First method of 12 class");
    }

    @Override
    public String second() {
        return null;
    }
}
