package generation;

import cglibex.AccessingAllClassesInPackage;
import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public class Main {

    public static void main(String[] args) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //
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
            if (clazz.getName().equals("generation.ClassB")) {
                cp.insertClassPath(new ClassClassPath(clazz));
            }
        }

        for (Class clazz : setOfClasses) {
            if (clazz.getName().equals("generation.ClassB")) {
                CtClass classToAdd = cp.get(clazz.getName());
                System.out.println(classToAdd);

                // добавляем методы текущего класса
                CtMethod[] methods2 = classToAdd.getDeclaredMethods();
                for (CtMethod method : methods2) {
                    CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), someInterfaceRoot);
                    newMethod.setBody(method, null);
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
