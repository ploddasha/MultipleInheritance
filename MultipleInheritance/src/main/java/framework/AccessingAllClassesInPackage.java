package framework;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ковырялка для нахождения всех классов в package.
 * Как то так получилось, что в данный момент это никак не используется,
 * так как ответственность за создание мульти-объектов лежит на пользователе.
 * Но вообще штука прикольная, может пригодиться ещё.
 * .
 * Принимает на вход имя package. Возвращает сет всех классов в этом package.
 */
public class AccessingAllClassesInPackage {

    public Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
            .filter(line -> line.endsWith(".class"))
            .map(line -> getClass(line, packageName))
            .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
            System.err.println("Class " + className + " in " + packageName + " not found.");
        }
        return null;
    }
}