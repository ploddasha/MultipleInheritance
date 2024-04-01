package framework.generation;

import framework.AccessingAllClassesInPackage;
import framework.annotations.RootInterface;
import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class Main {

    public static void main(String[] args) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        String packageName = "framework.generation";
        Class<?> clazzInput = ClassB.class;

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


        // получаем методы корневого интерфейса
        Method[] methods;
        List<String> methodNames = new ArrayList<>();
        for (Class clazz : setOfClasses) {
            if (clazz.isAnnotationPresent(RootInterface.class)) {
                methods = clazz.getMethods();
                for (Method met : methods) {
                    System.out.println(met.getName());
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
                    System.out.println("метод " + method.getName());
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
                            "       classes = ourClass.getClasses();" +
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

        // Вызываем метод корневого класса
        Method[] methods3 = root.getDeclaredMethods();
        for (Method method : methods3) {
            if (method.getName().equals("first")) {
                // Вызываем метод для экземпляра
                method.invoke(instance);
            }

        }


    }

}

