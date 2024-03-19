package generation;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Main {

    public static void main(String[] args) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ClassPool cp = ClassPool.getDefault();

        // создаем класс
        CtClass someRoot = cp.makeClass("generation.A");

        //добавляем второй класс, от которого хотим взять методы
        cp.insertClassPath(new ClassClassPath(ClassB.class));

        System.out.println(new ClassClassPath(ClassB.class));
        System.out.println(cp.get("generation.ClassB"));
        CtClass class2 = cp.get("generation.ClassB");

        // добавляем методы класса B
        CtMethod[] methods2 = class2.getDeclaredMethods();
        for (CtMethod method : methods2) {
            CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), someRoot);
            newMethod.setBody(method, null);
            someRoot.addMethod(newMethod);
        }

        // добавляем свои методы
        someRoot.addMethod(CtNewMethod.make("public void myMethod() { System.out.println(\"Hello from myMethod\"); }", someRoot));

        //смотрим все методы нашего root
        Class<?> a = someRoot.toClass();
        Method[] methods = a.getMethods();
        for (Method method : methods) {
            System.out.println(method);
        }

        // Создаем экземпляр класса A
        Object instance = a.getConstructor().newInstance();
        System.out.println(instance);

        // Вызываем методы класса A
        Method[] methods3 = a.getDeclaredMethods();
        for (Method method : methods3) {
            if (method.getName().equals("myMethod")) {
                // Вызываем метод для экземпляра
                method.invoke(instance);
                break;
            }
        }

    }

}
