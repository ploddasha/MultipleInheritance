package framework.generation;

import framework.AccessingAllClassesInPackage;
import framework.annotations.RootInterface;
import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class Main {

    public static void main(String[] args) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        RootInterface rootInterfaceAnnotation = RootInterfaceExample.class.getAnnotation(RootInterface.class);
        String packageName = rootInterfaceAnnotation.packageName();
        AccessingAllClassesInPackage accessingAllClassesInPackage = new AccessingAllClassesInPackage();
        Set<Class> setOfClasses = accessingAllClassesInPackage.findAllClassesUsingClassLoader(packageName);

        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("framework.generation"); // Добавление импорта для пакета framework.generation

        cp.importPackage("java.util.Map");
        cp.importPackage("java.util.LinkedHashMap");
        cp.importPackage("java.util.List");
        cp.importPackage("java.util.ArrayList");
        cp.importPackage("java.lang.Class");
        cp.importPackage("java.lang.Object");
        cp.importPackage("java.lang.reflect.Method");




        // создаем корневой класс
        CtClass someInterfaceRoot = cp.makeClass("framework.generation.SomeInterfaceRoot");

        // получаем методы
        Method[] methods;
        for (Class clazz : setOfClasses) {
            if (clazz.isAnnotationPresent(RootInterface.class)) {
                methods = clazz.getMethods();
                System.out.println(methods);
            }
        }


        // добавляем классы, от которых хотим взять методы
        for (Class clazz : setOfClasses) {
            System.out.println(clazz.getName());
            if (clazz.getName().equals("framework.generation.ClassB") || clazz.getName().equals("framework.generation.ClassC") || clazz.getName().equals("framework.generation.OurClass")) {
                cp.insertClassPath(new ClassClassPath(clazz));
            }
        }


        // добавление поля со списком классов
        CtField newFieldMap = CtField.make("public java.util.Map classes = new java.util.LinkedHashMap();", someInterfaceRoot);
        someInterfaceRoot.addField(newFieldMap);

        // добавление поля со списком классов ключей
        CtField newFieldList = CtField.make("public java.util.List keyList = new java.util.ArrayList();", someInterfaceRoot);
        someInterfaceRoot.addField(newFieldList);

        // добавляем поле i
        CtField newFieldI = CtField.make("Integer i = new Integer(0);", someInterfaceRoot);
        newFieldI.setModifiers(Modifier.PRIVATE);
        someInterfaceRoot.addField(newFieldI);

        // Добавляем новый метод в класс - callNextMethod
        // Создаем тело метода
        String nameOfMethod = "method";
        String methodBody = "{" +
                "    Integer zero = new Integer(0);" +
                "    if (i.equals(zero)) {" +
                "       System.out.println(\"Поиск и добавление классов иерархии\");" +
                "       OurClass ourClass = new OurClass();" +
                "       classes = ourClass.getClasses();" +
                "       keyList.addAll(classes.keySet());" +
                "    }" +
                "    int s = classes.values().size();" +
                "    Integer size = new Integer(s);" +
                "    if (!i.equals(size)) { " +
                "       java.lang.Class cl = (java.lang.Class) keyList.get(i.intValue());" +
                "       System.out.println(\"Наш класс 2 \" + cl);    " +
                "       Object objectO =  classes.get(cl);" +
                "       System.out.println(\"Наш объект 2 \" + objectO);    " +
                "       for (int j = 0; j < cl.getMethods().length; j++) {" +
                "           java.lang.reflect.Method met = cl.getMethods()[j];" +
                "           if (met.getName().equals(\"" + nameOfMethod + "\")) {" +
                "                 java.lang.Object[] arguments = new java.lang.Object[0];" +
                "                 met.invoke(objectO, arguments);" +
                "           }" +
                "       }" +
                "       i = Integer.valueOf(i.intValue() + 1);" +
                "       System.out.println(\"Переход к вызову следующего метода i = \" + i);" +
                "       callNextMethod();" +
                "    } else {" +
                "       System.out.println(\"Обход завершен\");" +
                "       i = Integer.valueOf(0);" +
                "    }" +
                "}";

        OurClass ourClass = new OurClass();
        Map<Class<?>, Object> classes = ourClass.getClasses();
        List<Class<?>> keyList = new ArrayList<>();
        keyList.addAll(classes.keySet()) ;

        ClassC classC2 = (ClassC) classes.get(ClassC.class);
        classC2.method();
        System.out.println("наш объект - " + classC2);

        Class cl;
        cl = keyList.get(0);
        //Class<?> cl = keyList.get(0);
        System.out.println("наш класс - " + cl);
        Object objectC =  classes.get(cl);
        System.out.println("наш объект - " + objectC);
        var array = cl.getMethods();

        for (int i = 0; i < cl.getMethods().length; i++) {
            Method met = cl.getMethods()[i];
            if (met.getName().equals("method")) {
                System.out.println("---");
                Object[] arguments = new Object[0];
                met.invoke(objectC, arguments);
            }
        }
        /*
        for (Method met : cl.getDeclaredMethods()) {
            if (met.getName().equals("method")) {
                met.invoke(classO);
            }
        } */


        // Создаем метод и добавляем его в класс
        CtMethod newMethodCallNextMethod = CtNewMethod.make("public void callNextMethod() " + methodBody, someInterfaceRoot);
        someInterfaceRoot.addMethod(newMethodCallNextMethod);

        for (Class clazz : setOfClasses) {
            if (clazz.getName().equals("framework.generation.ClassB")) {
                CtClass classToAdd = cp.get(clazz.getName());
                System.out.println(classToAdd);

                // добавляем методы текущего класса
                CtMethod[] methods2 = classToAdd.getDeclaredMethods();
                for (CtMethod method : methods2) {
                    CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), someInterfaceRoot);
                    newMethod.setBody(method, null);


                    // добавить вызов метода посмотреть поле
                    String newMethodToAddString = "System.out.println(i);";
                    newMethod.insertAfter(newMethodToAddString);


                    // добавляем в конец метода (после основного тела) свою функциональность
                    String callNextMethod = "callNextMethod();";
                    newMethod.insertAfter(callNextMethod);



                    // добавить вызов метода посмотреть поле
                    String newMethodToAddList = "System.out.println(classes);";
                    newMethod.insertAfter(newMethodToAddList);

                    // добавляем новый метод в класс
                    someInterfaceRoot.addMethod(newMethod);
                }

            }

        }

        // добавляем свои методы
        someInterfaceRoot.addMethod(CtNewMethod.make("public void myMethod() { System.out.println(\"Hello from myMethod\"); }", someInterfaceRoot));

        //смотрим все методы нашего root
        Class<?> a = someInterfaceRoot.toClass();
        Method[] methodsA = a.getMethods();
        for (Method method : methodsA) {
            System.out.println(method);
        }

        // Создаем экземпляр класса A
        Object instance = a.getConstructor().newInstance();
        System.out.println(instance);

        // Вызываем методы класса A
        Method[] methods3 = a.getDeclaredMethods();
        for (Method method : methods3) {
            if (method.getName().equals("myMethod") || method.getName().equals("method")) {
                // Вызываем метод для экземпляра
                method.invoke(instance);
            }

        }


    }

}


/*
ClassB {

    Добавить список классов?
    classes // C, D, E
    i = 0

    method() {
        ///////
        callNextMethod()
    }

    callNextMethod() {
        if (i == 0) {
            OurClass ourClass = new OurClass()
            classes = ourClass.getClasses()
        }
        if (i != classes.size - 1) {
            classes[i].method()
            i++
            callNextMethod()
        } else {
            classes[i].method()
        }
    }

}



CtClass classC = cp.get("framework.generation.ClassC");
                    CtMethod newMethodToAdd = classC.getMethod("method", "()V");

//CtClass fieldType = ClassPool.getDefault().get("java.lang.Integer"); // Тип нового поля
                    //CtField newField = new CtField(fieldType, "i", someInterfaceRoot);


"{" +
                "    if (\"0\".equals(i)) {" +
                "        OurClass ourClass = new OurClass();" +
                "        classes = ourClass.getClasses();" +
                "    }" +
                "    if (i != classes.length - 1) {" +
                "        classes[i].method();" +
                "        i++;" +
                "    } else {" +
                "        classes[i].method();" +
                "        i = 0;" +
                "    }" +
                "}";
 */
