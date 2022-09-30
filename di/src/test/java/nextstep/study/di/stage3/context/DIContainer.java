package nextstep.study.di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {
    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        beans = new HashSet<>();
        init(classes);
    }

    private void init(Set<Class<?>> classes) {
        for (Class<?> aClass : classes) {
            beans.add(convertToObject(aClass));
        }
    }

    private Object convertToObject(Class<?> aClass) {
        try {
            Constructor<?> constructor = getConstructor(aClass);
            List<?> parameterBeans = getParameterBeans(constructor.getParameterTypes());
            return constructor.newInstance(parameterBeans.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("No Constructor!");
        }
    }

    private Constructor<?> getConstructor(Class<?> aClass) {
        return aClass.getDeclaredConstructors()[0];
    }

    private List<?> getParameterBeans(Class<?>... parameterTypes) {
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            Object bean = getBean(parameterType);
            parameters.add(bean);
        }
        return parameters;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> abstractClass) {
        return (T) beans.stream()
                .filter(bean -> matchesBean(abstractClass, bean))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no Bean"));
    }

    private <T> boolean matchesBean(Class<T> abstractClass, Object bean) {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces.length == 0) {
            return bean.getClass() == abstractClass;
        }
        return isInterface(abstractClass, interfaces);
    }

    private <T> boolean isInterface(Class<T> abstractClass, Class<?>[] interfaces) {
        for (Class<?> anInterface : interfaces) {
            if (anInterface == abstractClass) {
                return true;
            }
        }
        return false;
    }
}
