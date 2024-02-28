package cglibex.classes;

import cglibex.Mult;

@Mult(classes = {Class1.class, Class2.class})
public class Class12 implements MixinInterface {

    @Override
    public String first() {
        return null;
    }

    @Override
    public String second() {
        return null;
    }
}
