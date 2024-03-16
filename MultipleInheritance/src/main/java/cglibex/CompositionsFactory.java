package cglibex;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Через эту фабрику пользователь создаёт свои мульти-объекты.
 * Пользователь получает наружу Object и приводит его к корневому интерфейсу: (RootInterface) obj
 * При вызове методов этого обхекта срабатывает описанный в данном классе прокси, перехватывающий методы
 * и запускающий обход по супер-классам.
 * ..С точки зрения архитектуры создание прокси можно в отдельный класс вынести.
 */
public class CompositionsFactory {

    public Object makeObject(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Composition composition = new Composition(clazz);

        List<Object> methodList = new ArrayList<>();

        MethodInterceptor handler = (obj, method, arguments, proxy) -> {
            methodList.clear();
            for (var met : composition.rootInterface.getClass().getDeclaredMethods()) {
                if (met.getName().equals(method.getName()) && !(met.isAnnotationPresent(Useless.class))) {
                    methodList.add(met.invoke(composition.rootInterface, arguments));
                }
            }
            for (var key : composition.composition.keySet()) {
                var partOfComposition = composition.composition.get(key);
                for (var met : partOfComposition.getClass().getDeclaredMethods()) {
                    if (Objects.equals(met.getName(), method.getName()) && !(met.isAnnotationPresent(Useless.class))) {
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
