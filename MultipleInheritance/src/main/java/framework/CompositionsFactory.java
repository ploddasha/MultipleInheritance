package framework;

import framework.annotations.IgnoreMethod;
import framework.annotations.RootInterface;
import framework.annotations.TakeMethodFrom;
import javassist.*;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Через эту фабрику пользователь создаёт свои мульти-объекты.
 * Пользователь получает наружу Object и приводит его к корневому интерфейсу: (RootInterface) obj
 * или к тому классу, объект которого он создаёт: (Class12) obj
 * При вызове методов этого объекта срабатывает описанный в данном классе прокси, перехватывающий методы
 * и запускающий обход по супер-классам.
 * ..С точки зрения архитектуры создание прокси можно в отдельный класс вынести.
 */
public class CompositionsFactory {

    public Object makeObject(Class<?> clazzInput, String packageName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException {
        Composition composition = new Composition(clazzInput);

        // -----------------------------------------------------------------------

        AccessingAllClassesInPackage accessingAllClassesInPackage = new AccessingAllClassesInPackage();
        Set<Class> setOfClasses = accessingAllClassesInPackage.findAllClassesUsingClassLoader(packageName);

        ClassPool cp = ClassPool.getDefault();

        // Добавление импорта для пользовательского пакета
        cp.importPackage(packageName);

        cp.importPackage("java.util.Map");
        cp.importPackage("java.util.LinkedHashMap");
        cp.importPackage("java.util.List");
        cp.importPackage("java.util.ArrayList");
        cp.importPackage("java.lang.Class");
        cp.importPackage("java.lang.Object");
        cp.importPackage("java.lang.reflect.Method");

        cp.importPackage("framework.generation.OurClass");
        cp.importPackage("framework.Composition");
        cp.importPackage("framework.annotations");
        cp.importPackage("framework.annotations.IgnoreMethod");

        // получаем методы корневого интерфейса
        Method[] methods;
        List<String> methodNames = new ArrayList<>();
        for (Class clazz : setOfClasses) {
            if (clazz.isAnnotationPresent(RootInterface.class)) {
                methods = clazz.getMethods();
                for (Method met : methods) {
                    methodNames.add(met.getName());
                }
            }
        }


        // добавляем в пул классы иерархии (от которых хотим взять методы)
        for (Class clazz : setOfClasses) {
            cp.insertClassPath(new ClassClassPath(clazz));
        }

        // создаем корневой класс
        CtClass someInterfaceRoot = cp.makeClass("framework.generation.SomeInterfaceRoot");

        // добавление поля со списком классов иерархии
        CtField newFieldComposition = CtField.make("public Composition composition = new Composition(\"" + clazzInput.getName() + "\");", someInterfaceRoot);
        someInterfaceRoot.addField(newFieldComposition);

        // добавление поля со списком классов иерархии
        CtField newFieldMap = CtField.make("public java.util.Map classes = new java.util.LinkedHashMap();", someInterfaceRoot);
        someInterfaceRoot.addField(newFieldMap);

        // добавление поля со списком классов ключей
        CtField newFieldList = CtField.make("public java.util.List keyList = new java.util.ArrayList();", someInterfaceRoot);
        someInterfaceRoot.addField(newFieldList);

        // добавляем поле i
        CtField newFieldI = CtField.make("Integer i = new Integer(0);", someInterfaceRoot);
        newFieldI.setModifiers(Modifier.PRIVATE);
        someInterfaceRoot.addField(newFieldI);


        for (Class clazz : setOfClasses) {
            if (clazz.getName().equals(clazzInput.getName())) {
                CtClass classToAdd = cp.get(clazz.getName());

                // добавляем методы текущего класса
                CtMethod[] methodsToAdd = classToAdd.getDeclaredMethods();
                for (CtMethod method : methodsToAdd) {
                    CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), someInterfaceRoot);
                    if (!method.hasAnnotation(IgnoreMethod.class)) {
                        newMethod.setBody(method, null);
                    } else {
                        newMethod.setBody("{}");
                    }
                    // -----------------------------------
                    // Добавляем новый метод в класс - callNextMethod
                    // Создаем тело метода
                    String nameOfMethod = method.getName();
                    String methodBody = "{" +
                            "    Integer zero = new Integer(0);" +
                            "    if (i.equals(zero)) {" +
                            "       classes = composition.getComposition();" +
                            "       keyList.addAll(classes.keySet());" +
                            "    }" +
                            "    int s = classes.values().size();" +
                            "    Integer size = new Integer(s);" +
                            "    if (!i.equals(size)) { " +
                            "       java.lang.Class cl = (java.lang.Class) keyList.get(i.intValue());" +
                            "       Object objectO =  classes.get(cl);" +
                            "       for (int j = 0; j < cl.getMethods().length; j++) {" +
                            "           java.lang.reflect.Method met = cl.getMethods()[j];" +
                            "           if (met.getName().equals(\"" + nameOfMethod + "\")) {" +
                            "                 if (!met.isAnnotationPresent(IgnoreMethod.class)) {" +
                            "                   java.lang.Object[] arguments = new java.lang.Object[0];" +
                            "                   met.invoke(objectO, arguments);" +
                            "                 }" +
                            "           }" +
                            "       }" +
                            "       i = Integer.valueOf(i.intValue() + 1);" +
                            "       callNext" + nameOfMethod + "();" +
                            "    } else {" +
                            "       i = Integer.valueOf(0);" +
                            "    }" +
                            "}";


                    // Создаем метод и добавляем его в класс
                    String callNextMethodName = "callNext" + nameOfMethod;
                    CtMethod newMethodCallNextMethod = CtNewMethod.make("public void " + callNextMethodName + "() " + methodBody, someInterfaceRoot);
                    someInterfaceRoot.addMethod(newMethodCallNextMethod);
                    // -----------------------------------

                    // добавляем в конец метода (после основного тела) свою функциональность
                    String callNextMethodLine = callNextMethodName + "();";
                    newMethod.insertAfter(callNextMethodLine);

                    // добавляем новый метод в класс
                    someInterfaceRoot.addMethod(newMethod);
                }
            }
        }

        // Создаем экземпляр корневого класса
        Class<?> root = someInterfaceRoot.toClass();
        Object instance = root.getConstructor().newInstance();

        // -----------------------------------------------------------------------

        MethodInterceptor handler = (obj, method, arguments, proxy) -> {

            // Собираем методы корневого класса
            Method[] methods3 = root.getDeclaredMethods();
            for (Method met : methods3) {
                if (met.getName().equals(method.getName()) && method.isAnnotationPresent(TakeMethodFrom.class)) {
                    var fromWhere = method.getAnnotation(TakeMethodFrom.class).fromWhere();
                    for (var subMet : fromWhere.getDeclaredMethods()) {
                        if (subMet.getName().equals(met.getName())) {
                            var constructor = fromWhere.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            return subMet.invoke(constructor.newInstance());
                        }
                    }
                    throw new NoSuchMethodException("No method " + met.getName() + " in " + fromWhere.getName());
                }
                if (met.getName().equals(method.getName()) && met.getReturnType() != void.class) {
                    return met.invoke(instance);
                }
                if (met.getName().equals(method.getName())) {
                    met.invoke(instance);
                }
            }
            return method.toString();
        };

        return Enhancer.create(clazzInput, handler);
    }

}