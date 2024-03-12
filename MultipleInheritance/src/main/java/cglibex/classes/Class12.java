package cglibex.classes;

import cglibex.Mult;

@Mult(classes = {Class1.class, Class2.class,  Class3.class})
public class Class12 implements MixinInterface {

    @Override
    public void first() {

    }

    @Override
    public String second() {
        return null;
    }
}
