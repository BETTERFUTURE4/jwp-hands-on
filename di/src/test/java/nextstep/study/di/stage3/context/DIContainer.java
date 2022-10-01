package nextstep.study.di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.study.ConsumerWrapper;
import nextstep.study.FunctionWrapper;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.beans = createBeans(classes);
        this.beans.forEach(this::setFields);
    }

    private Set<Object> createBeans(Set<Class<?>> classes) {
        return classes.stream()
                .map(FunctionWrapper.apply(Class::getDeclaredConstructor))
                .peek(constructor -> constructor.setAccessible(true))
                .map(FunctionWrapper.apply(Constructor::newInstance))
                .collect(Collectors.toUnmodifiableSet());
    }

    private void setFields(final Object bean) {
        for (final var field : bean.getClass().getDeclaredFields()) {
            setField(bean, field);
        }
    }

    private void setField(Object bean, Field field) {
        Class<?> fieldType = field.getType();
        field.setAccessible(true);
        beans.stream()
                .filter(fieldType::isInstance)
                .forEach(ConsumerWrapper.accept(matchBean -> field.set(bean, matchBean)));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> abstractClass) {
        return beans.stream()
                .filter(abstractClass::isInstance)
                .findFirst()
                .map(bean -> (T) bean)
                .orElseThrow(() -> new IllegalArgumentException("no Bean"));
    }
}
