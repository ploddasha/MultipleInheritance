package cglibex;

import cglibex.classes.Class12;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompositionsFactory {

    public Object makeObject(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Composition composition = new Composition(Class12.class);

        List<Object> methodList = new ArrayList<>();

        MethodInterceptor handler = (obj, method, arguments, proxy) -> {
            methodList.clear();
            for (var key : composition.composition.keySet()) {
                var partOfComposition = composition.composition.get(key);
                for (var met : partOfComposition.getClass().getDeclaredMethods()) {
                    if (Objects.equals(met.getName(), method.getName())) {
                        if (met.getReturnType() == void.class) {
                            met.invoke(partOfComposition, arguments);
                        }
                        else {
                            methodList.add(met.invoke(partOfComposition, arguments));
                        }
                    }
                }
            }

            if (!(methodList.isEmpty())) {
                String str = "";
                for (var i : methodList) {
                    if (i != null) {
                        str = str.concat(i + "\n");
                    }
                    else {
                        str = str.concat("null\n");
                    }
                }
                return str.substring(0, str.length() - 1);
            }
            else {
                return method.toString();
            }
        };

        return Enhancer.create(clazz, handler);
    }

}
