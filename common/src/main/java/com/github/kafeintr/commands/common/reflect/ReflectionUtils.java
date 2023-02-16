/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Kafein's CommandFramework
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.kafeintr.commands.common.reflect;

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
    public static Annotation getAnnotation(@Nullable Class<?> clazz, @NotNull Class<? extends Annotation> annotationClass) {
        return clazz == null ? null : clazz.getAnnotation(annotationClass);
    }

    @Nullable
    public static Annotation getAnnotation(@Nullable Method method, @NotNull Class<? extends Annotation> annotationClass) {
        return method == null ? null : method.getAnnotation(annotationClass);
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
