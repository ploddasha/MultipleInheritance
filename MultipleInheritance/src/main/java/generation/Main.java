package generation;

import cglibex.classes.Interface1;

import javassist.*;

import java.lang.reflect.Method;


public class Main {

    public static void main(String[] args) throws CannotCompileException, NotFoundException {
        ClassPool cp = ClassPool.getDefault();

        // создаем класс
        CtClass someRoot = cp.makeClass("com.example.A");

        //смотрим его методы
        Method[] methods = someRoot.toClass().getMethods();
        for (Method method : methods) {
            System.out.println(method);
        }

        //someRoot.addMethod(CtNewMethod.make("public void myMethod() { System.out.println(\"Hello from myMethod\"); }", someRoot));


        //someRoot.addInterface(cp.get("java.cglibex.classes.Interface1"));
    }

}
