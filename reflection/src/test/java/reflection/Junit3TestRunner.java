package reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class Junit3TestRunner {

    @Test
    void run() {
        Class<Junit3Test> clazz = Junit3Test.class;
        Junit3Test junit3Test = new Junit3Test();

        // TODO Junit3Test에서 test로 시작하는 메소드 실행
        Method[] methods = clazz.getMethods();
        List<Method> methodList = Arrays.stream(methods)
                .filter(method -> method.getName().startsWith("test"))
                .collect(Collectors.toList());

        for (Method method : methodList) {
            invoke(junit3Test, method);
        }
    }

    private void invoke(Object obj, Method method) {
        try {
            method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
