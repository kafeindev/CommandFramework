/*
 * MIT License
 *
 * Copyright (c) 2022 FarmerPlus
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

package com.mineles.commands.common.command;

import com.mineles.commands.common.command.abstraction.ChildCommand;
import com.mineles.commands.common.command.abstraction.ParentCommand;
import com.mineles.commands.common.command.annotation.*;
import com.mineles.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

final class CommandConverter<T> {

    @NotNull
    ParentCommand<T> convert(@NotNull BaseCommand baseCommand) {
        Class<?> clazz = baseCommand.getClass();

        Annotation[] annotations = clazz.getAnnotations();
        if (annotations.length == 0) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " has no annotations!");
        }

        try {
            CommandBuilder builder = CommandBuilder.newBuilder(baseCommand)
                    .executor(clazz.getDeclaredMethod("execute", SenderComponent.class, String[].class))
                    .aliases(clazz.getAnnotation(CommandAlias.class))
                    .usage(clazz.getAnnotation(CommandUsage.class))
                    .completions(clazz.getAnnotation(CommandCompletion.class))
                    .description(clazz.getAnnotation(CommandDescription.class))
                    .permission(clazz.getAnnotation(CommandPermission.class))
                    .type(true);

            return (ParentCommand<T>) builder.build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    ChildCommand<T> convert(@NotNull BaseCommand baseCommand, @NotNull Method method) {
        Annotation[] annotations = method.getAnnotations();
        if (annotations.length == 0) {
            return null;
        }

        //is a child command?
        if (method.getAnnotation(Subcommand.class) == null) {
            return null;
        }

        CommandBuilder builder = CommandBuilder.newBuilder(baseCommand)
                .executor(method)
                .aliases(method.getAnnotation(Subcommand.class))
                .usage(method.getAnnotation(CommandUsage.class))
                .completions(method.getAnnotation(CommandCompletion.class))
                .description(method.getAnnotation(CommandDescription.class))
                .permission(method.getAnnotation(CommandPermission.class))
                .type(false);

        return (ChildCommand<T>) builder.build();
    }
}
