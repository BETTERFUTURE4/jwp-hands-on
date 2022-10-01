package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.study.ConsumerWrapper;
import nextstep.study.FunctionWrapper;


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

    public static DIContainer createContainerForPackage(final String rootPackageName) {
        Set<Class<?>> allClassesInPackage = ClassPathScanner.getAllClassesInPackage(rootPackageName);
        System.out.println("allClassesInPackage = " + allClassesInPackage);
        Set<Class<?>> beanClasses = allClassesInPackage
                .stream()
                .filter(aClass -> aClass.isAnnotationPresent(Repository.class) || aClass.isAnnotationPresent(Service.class))
                .collect(Collectors.toUnmodifiableSet());
        System.out.println("beanClasses = " + beanClasses);
        return new DIContainer(beanClasses);
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
