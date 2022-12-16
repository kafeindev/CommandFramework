package net.mineles.commands.common.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {}

    @NotNull
    public static List<Method> getMethods(@NotNull Class<?> clazz, boolean accessible) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> methodList = new ArrayList<>(methods.length);

        for (Method method : methods) {
            if (accessible) {
                method.setAccessible(true);
            }

            methodList.add(method);
        }

        return methodList;
    }

    @NotNull
    public static List<Method> getMethodsAnnotatedWith(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation, boolean accessible) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> annotatedMethods = new ArrayList<>();

        for (Method method : methods) {
            if (accessible) {
                method.setAccessible(true);
            }

            if (method.isAnnotationPresent(annotation)) {
                annotatedMethods.add(method);
            }
        }

        return annotatedMethods;
    }

    @Nullable
    public static Method getMethod(@NotNull Class<?> clazz, @NotNull String name, @NotNull Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
    public static Method getMethodAnnotatedWith(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotationClass) {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }
        return null;
    }

    @Nullable
    public static Enum<?> getEnum(@NotNull Class<? extends Enum<?>> enumClass, @NotNull String name) {
        for (Enum<?> e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e;
            }
        }

        return null;
    }
}
