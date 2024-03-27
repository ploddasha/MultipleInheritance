package framework.examples;

import framework.annotations.MultipleInheritance;
import framework.annotations.IgnoreMethod;
import framework.annotations.TakeMethodFrom;

@MultipleInheritance(classes = {Class1.class, Class2.class,  Class3.class})
public class Class12 implements RootInterface {

    @Override
    public void first() {
        System.out.println("First method of 12 class");
    }

    @Override
    @TakeMethodFrom(fromWhere = Class1.class)
    public String second() {
        return null;
    }

    @IgnoreMethod
    public void newMethod() {
        System.out.println("Мы не хотим, чтобы это вызывалось");
    }

    public void uniqueMethod() {
        System.out.println("Unique method of 12 class");
    }
}
