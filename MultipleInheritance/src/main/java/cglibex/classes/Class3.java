package cglibex.classes;

import cglibex.Useless;

public class Class3 implements MixinInterface{//Interface3{

    //@Override
    //public String third() {
    //    return "third behavior";
    //}

    @Override
    @Useless
    public void first() {
        System.out.println("Useless method");
    }

    @Override
    public String second() {
        return null;
    }
}
