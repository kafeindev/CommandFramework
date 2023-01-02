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

package me.kafein.commands.common.command;

import me.kafein.commands.common.command.abstraction.ChildCommand;
import me.kafein.commands.common.command.abstraction.ParentCommand;
import me.kafein.commands.common.command.annotation.*;
import me.kafein.commands.common.reflect.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

final class CommandConverter {

    @NotNull
    ParentCommand convert(@NotNull BaseCommand baseCommand) {
        Class<?> clazz = baseCommand.getClass();

        Annotation[] annotations = clazz.getAnnotations();
        if (annotations.length == 0) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " has no annotations!");
        }

        CommandBuilder builder = CommandBuilder.newBuilder(baseCommand)
                .executor(ReflectionUtils.getMethodAnnotatedWith(clazz, NoArgsCommand.class))
                .aliases(clazz.getAnnotation(CommandAlias.class))
                .usage(clazz.getAnnotation(CommandUsage.class))
                .completions(clazz.getAnnotation(CommandCompletion.class))
                .description(clazz.getAnnotation(CommandDescription.class))
                .permission(clazz.getAnnotation(CommandPermission.class))
                .reply(clazz.getAnnotation(CommandReply.class))
                .type(true);
        return (ParentCommand) builder.build();
    }

    @Nullable
    ChildCommand convert(@NotNull BaseCommand baseCommand, @NotNull Method method) {
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
                .reply(method.getAnnotation(CommandReply.class))
                .type(false);
        return (ChildCommand) builder.build();
    }
}
