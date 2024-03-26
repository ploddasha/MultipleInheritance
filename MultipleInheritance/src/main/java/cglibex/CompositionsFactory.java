package cglibex;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Через эту фабрику пользователь создаёт свои мульти-объекты.
 * Пользователь получает наружу Object и приводит его к корневому интерфейсу: (RootInterface) obj
 * или к тому классу, объект которого он создаёт: (Class12) obj
 * При вызове методов этого объекта срабатывает описанный в данном классе прокси, перехватывающий методы
 * и запускающий обход по супер-классам.
 * ..С точки зрения архитектуры создание прокси можно в отдельный класс вынести.
 */
public class CompositionsFactory {

    public Object makeObject(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Composition composition = new Composition(clazz);

        MethodInterceptor handler = (obj, method, arguments, proxy) -> {
            boolean flag = true;
            // Поиск метода у нижнего класса в иерархии
            for (var met : composition.handle.getClass().getDeclaredMethods()) {
                if (met.getName().equals(method.getName()) && met.isAnnotationPresent(TakeMethodFrom.class)) {
                    var classFrom = met.getAnnotation(TakeMethodFrom.class).fromWhere();
                    for (var subMet : composition.composition.get(classFrom).getClass().getDeclaredMethods()) {
                        if (subMet.getName().equals(met.getName())) {
                            return subMet.invoke(composition.composition.get(classFrom), arguments);
                        }
                    }
                    throw new NoSuchMethodException("No method " + met.getName() + " in " + classFrom.getName());
                }
                if (met.getName().equals(method.getName()) && !(met.isAnnotationPresent(IgnoreMethod.class))) {
                    if (met.getReturnType() == void.class) {
                        met.invoke(composition.handle, arguments);
                        flag = false;
                        break;
                    }
                    else {
                        return met.invoke(composition.handle, arguments);
                    }
                }
            }
            // Поиск метода в остальной иерархии
            for (var key : composition.composition.keySet()) {
                var partOfComposition = composition.composition.get(key);
                for (var met : partOfComposition.getClass().getDeclaredMethods()) {
                    if (Objects.equals(met.getName(), method.getName()) && !(met.isAnnotationPresent(IgnoreMethod.class))) {
                        if (met.getReturnType() == void.class) {
                            met.invoke(partOfComposition, arguments);
                            flag = false;
                            break;
                        }
                        else {
                            return met.invoke(partOfComposition, arguments);
                        }
                    }
                }
            }
            if (flag) {
                throw new NoSuchMethodException("Method " + method.toString() + " not found. Please, check classes in MultipleInheritance annotation and their methods.");
            }
            return method.toString();
        };

        return Enhancer.create(clazz, handler);
    }

}
