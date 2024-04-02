package framework;

import framework.annotations.IgnoreMethod;
import framework.annotations.RootInterface;
import framework.annotations.TakeMethodFrom;
import framework.generation.ClassB;
import javassist.*;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


        // получаем методы корневого интерфейса
        Method[] methods;
        List<String> methodNames = new ArrayList<>();
        for (Class clazz : setOfClasses) {
            if (clazz.isAnnotationPresent(RootInterface.class)) {
                methods = clazz.getMethods();
                for (Method met : methods) {
                    //System.out.println(met.getName());
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
                    newMethod.setBody(method, null);

                    // -----------------------------------
                    // Добавляем новый метод в класс - callNextMethod
                    // Создаем тело метода
                    String nameOfMethod = method.getName();
                    String methodBody = "{" +
                            "    Integer zero = new Integer(0);" +
                            "    if (i.equals(zero)) {" +

                            "       OurClass ourClass = new OurClass();" +
                            "       Composition composition = new Composition();" +
                            "       composition.make(\"" + clazzInput.getName() + "\");" +
                            "       classes = composition.getComposition();" +
                            //"       classes = ourClass.getClasses();" +

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
                            "                 java.lang.Object[] arguments = new java.lang.Object[0];" +
                            "                 met.invoke(objectO, arguments);" +
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

            // --------------------------------------------------------------------

            // Вызываем метод корневого класса
            Method[] methods3 = root.getDeclaredMethods();
            //boolean flag = true;
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
                    // Вызываем метод для экземпляра
                    //met.invoke(instance);
                }
                if (met.getName().equals(method.getName()) && met.getReturnType() != void.class) {
                    return met.invoke(instance);
                }
                if (met.getName().equals(method.getName())) {
                    met.invoke(instance);
                }
            }

            // --------------------------------------------------------------------

            /*
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
             */
            return method.toString();
        };

        return Enhancer.create(clazzInput, handler);
    }

}
