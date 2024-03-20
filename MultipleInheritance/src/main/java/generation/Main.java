package generation;

import cglibex.AccessingAllClassesInPackage;
import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public class Main {

    public static void main(String[] args) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //

//        AccessingAllClassesInPackage allClassesInPackage = new AccessingAllClassesInPackage();
//        var allClasses = allClassesInPackage.findAllClassesUsingClassLoader("name");
//        for (var clazz : allClasses) {
//            if (clazz.isAnnotationPresent(RootInterface.class)) {
//                генерируем RootClass(clazz);
//            }
//        }

        RootInterface rootInterfaceAnnotation = RootInterfacee.class.getAnnotation(RootInterface.class);
        String packageName = rootInterfaceAnnotation.packageName();
        AccessingAllClassesInPackage accessingAllClassesInPackage = new AccessingAllClassesInPackage();
        Set<Class> setOfClasses = accessingAllClassesInPackage.findAllClassesUsingClassLoader(packageName);

        ClassPool cp = ClassPool.getDefault();

        // создаем корневой класс
        CtClass someInterfaceRoot = cp.makeClass("generation.SomeInterfaceRoot");

        // получаем методы
        Method[] methods;
        for (Class clazz : setOfClasses) {
            if (clazz.isAnnotationPresent(RootInterface.class)) {
                methods = clazz.getMethods();
                System.out.println(methods);
            }
        }


        // добавляем классы, от которых хотим взять методы
        // классы mult? классы в пакете?
        for (Class clazz : setOfClasses) {
            System.out.println(clazz.getName());
            if (clazz.getName().equals("generation.ClassB") || clazz.getName().equals("generation.ClassC")) {
                cp.insertClassPath(new ClassClassPath(clazz));
            }
        }

        cp.insertClassPath("generation.OurClass");

        // добавление поля со списком классов
        CtField newFieldList = CtField.make("public java.util.Map classes = new java.util.LinkedHashMap();", someInterfaceRoot);
        someInterfaceRoot.addField(newFieldList);

        // добавляем поле i
        CtField newFieldI = CtField.make("private String " + "i" + " = \"" + "0" + "\";", someInterfaceRoot);
        newFieldI.setModifiers(Modifier.PRIVATE);
        someInterfaceRoot.addField(newFieldI);

        // добавляем новый метод в класс callNextMethod
        // Создаем тело метода
        String methodBody = "{" +
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

        // Создаем метод и добавляем его в класс
        CtMethod newMethodCallNextMethod = CtNewMethod.make("public void callNextMethod() " + methodBody, someInterfaceRoot);
        someInterfaceRoot.addMethod(newMethodCallNextMethod);

        for (Class clazz : setOfClasses) {
            if (clazz.getName().equals("generation.ClassB")) {
                CtClass classToAdd = cp.get(clazz.getName());
                System.out.println(classToAdd);

                // добавляем методы текущего класса
                CtMethod[] methods2 = classToAdd.getDeclaredMethods();
                for (CtMethod method : methods2) {
                    CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), someInterfaceRoot);
                    newMethod.setBody(method, null);

                    // добавляем в конец метода (после основного тела) свою функциональность
                    String newHello = "System.out.println(\"Hello! I am here!\");";
                    newMethod.insertAfter(newHello);
                    String callNextMethod = "callNextMethod();";
                    newMethod.insertAfter(callNextMethod);


                    // добавить вызов метода посмотреть поле
                    String newMethodToAddString = "System.out.println(i);";
                    newMethod.insertAfter(newMethodToAddString);


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
            OurClass ourClass = new OorClass()
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



CtClass classC = cp.get("generation.ClassC");
                    CtMethod newMethodToAdd = classC.getMethod("method", "()V");

//CtClass fieldType = ClassPool.getDefault().get("java.lang.Integer"); // Тип нового поля
                    //CtField newField = new CtField(fieldType, "i", someInterfaceRoot);

 */
