package cglibex;

import net.sf.cglib.proxy.Mixin;

public class MixinTest {

    public static void main(String[] args) {
        Mixin mixin = Mixin.create(
                new Class[]{ Interface1.class, Interface2.class, MixinInterface.class },
                new Object[]{ new Class1(), new Class2() }
        );
        MixinInterface mixinDelegate = (MixinInterface) mixin;

        System.out.println(mixinDelegate.first());
        System.out.println(mixinDelegate.second());
    }

}

