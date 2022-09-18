package reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class Junit4TestRunner {

    @Test
    void run() {
        Class<Junit4Test> clazz = Junit4Test.class;
        Junit4Test junit4Test = new Junit4Test();

        // TODO Junit4Test 에서 @MyTest 애노테이션이 있는 메소드 실행
        Method[] methods = clazz.getMethods();
        List<Method> methodsList = Arrays.stream(methods)
                .filter(this::isMatch)
                .collect(Collectors.toList());

        for (Method method : methodsList) {
            invoke(junit4Test, method);
        }
    }

    private boolean isMatch(Method method) {
        return Arrays.stream(method.getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.getClass().isInstance(MyTest.class));
    }

    private void invoke(Object obj, Method method) {
        try {
            method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
