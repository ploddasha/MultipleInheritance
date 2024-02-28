package cglibex;

@Mult(classes = {Class1.class, Class2.class, Class3.class})
public class Class123 implements MixinInterface{

    @Override
    public String first() {
        return null;
    }

    @Override
    public String second() {
        return null;
    }

    //@Override
    public String third() {
        return "Not ready yet";
    }
}
