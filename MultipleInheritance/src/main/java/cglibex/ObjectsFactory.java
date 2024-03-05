package cglibex;

import cglibex.classes.MixinInterface;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.Map;
import java.util.Objects;

public class ObjectsFactory {

    private Map<Class, MixinInterface> mixins;
    private MethodInterceptor handler;

    public ObjectsFactory(Map<Class, MixinInterface> mix) {
        mixins = mix;

        handler = (obj, method , arguments, proxy) -> {

            for (var met : mixins.get(method.getDeclaringClass()).getClass().getMethods()) {

                if (Objects.equals(met.getName(), method.getName())) {
                    return met.invoke(mixins.get(method.getDeclaringClass()), arguments);
                }
            }

            return method.toString();
        };
    }

    public Object makeClass(Class clazz) {
        return Enhancer.create(clazz, handler);
    }



}