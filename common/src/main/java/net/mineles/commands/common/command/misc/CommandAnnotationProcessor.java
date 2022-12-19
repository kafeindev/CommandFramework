/*
 * MIT License
 *
 * Copyright (c) 2022 Mineles Command Framework
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

package net.mineles.commands.common.command.misc;

import com.google.common.collect.ImmutableList;
import net.mineles.commands.common.command.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class CommandAnnotationProcessor {

    private static final List<Class<? extends Annotation>> ANNOTATIONS = ImmutableList.of(
            NoArgsCommand.class, Subcommand.class,
            CommandAlias.class, CommandDescription.class,
            CommandCompletion.class, CommandUsage.class,
            CommandPermission.class
    );

    private CommandAnnotationProcessor() {}

    @Nullable
    public static String process(@NotNull Annotation annotation) {
        return process(annotation, "value", false);
    }

    @Nullable
    public static <T> T process(@NotNull Annotation annotation, @NotNull String value, boolean defaultValue)  {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (!ANNOTATIONS.contains(annotationType)) return null;

        try {
            @Nullable Object object = (T) annotationType.getMethod(value).invoke(annotation);

            return defaultValue && object == null
                    ? (T) annotationType.getMethod(value).getDefaultValue()
                    : (T) object;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
