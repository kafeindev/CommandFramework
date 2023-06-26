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

package com.github.kafeindev.commands.common.command;

import com.github.kafeindev.commands.common.command.abstraction.AbstractCommand;
import com.github.kafeindev.commands.common.command.base.BaseCommand;
import com.github.kafeindev.commands.common.command.completion.RegisteredCompletion;
import com.github.kafeindev.commands.common.command.misc.CommandPatternProcessor;
import com.github.kafeindev.commands.common.command.misc.CommandAnnotationProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class CommandBuilder {
    @NotNull
    public static CommandBuilder newBuilder(@NotNull BaseCommand baseCommand) {
        return new CommandBuilder(baseCommand);
    }

    @NotNull
    private final BaseCommand baseCommand;

    @Nullable
    private Method executor;

    private String[] aliases;

    @Nullable
    private RegisteredCompletion[] completions;

    @Nullable
    private List<String> parents;

    @Nullable
    private String usage;

    @Nullable
    private String description;

    @Nullable
    private String permission;

    private String permissionMessage;

    private boolean reply;

    private CommandBuilder(@NotNull BaseCommand baseCommand) {
        this.baseCommand = baseCommand;
    }

    @NotNull
    public CommandBuilder executor(@Nullable Method executor) {
        this.executor = executor;
        return this;
    }

    @NotNull
    public CommandBuilder parents(@Nullable List<String> parents) {
        this.parents = parents;
        return this;
    }

    @NotNull
    public CommandBuilder parents(@Nullable Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;

            String value = CommandAnnotationProcessor.process(annotation);
            if (value == null) continue;

            this.parents = CommandPatternProcessor.processParents(value);
        }
        return this;
    }

    @NotNull
    public CommandBuilder aliases(@Nullable String... aliases) {
        this.aliases = aliases;
        return this;
    }

    @NotNull
    public CommandBuilder aliases(@Nullable Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;

            String value = CommandAnnotationProcessor.process(annotation);
            if (value == null) continue;

            this.aliases = CommandPatternProcessor.processAlias(value);
        }
        return this;
    }

    @NotNull
    public CommandBuilder usage(@Nullable String usage) {
        this.usage = usage;
        return this;
    }

    @NotNull
    public CommandBuilder usage(@Nullable Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;

            this.usage = CommandAnnotationProcessor.process(annotation);
        }
        return this;
    }

    @NotNull
    public CommandBuilder completions(@Nullable RegisteredCompletion... completions) {
        this.completions = completions;
        return this;
    }

    @NotNull
    public CommandBuilder completions(@Nullable Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;

            String value = CommandAnnotationProcessor.process(annotation);
            if (value == null) continue;

            this.completions = CommandPatternProcessor.processCompletion(value);
        }
        return this;
    }

    @NotNull
    public CommandBuilder description(@Nullable String description) {
        this.description = description;
        return this;
    }

    @NotNull
    public CommandBuilder description(@Nullable Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;

            this.description = CommandAnnotationProcessor.process(annotation);
        }
        return this;
    }

    @NotNull
    public CommandBuilder permission(@Nullable String permission, @NotNull String permissionMessage) {
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        return this;
    }

    @NotNull
    public CommandBuilder permission(@Nullable Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;

            this.permission = CommandAnnotationProcessor.process(annotation);
            this.permissionMessage = CommandAnnotationProcessor.process(annotation, "message", true);
        }
        return this;
    }

    @NotNull
    public Command build(@NotNull Class<? extends AbstractCommand> commandClass) {
        CommandAttribute attribute = CommandAttribute.of(
                this.aliases, this.usage, this.description,
                this.permission, this.permissionMessage
        );

        try {
            return commandClass.getConstructor(BaseCommand.class, Method.class,
                            CommandAttribute.class, RegisteredCompletion[].class, List.class)
                    .newInstance(this.baseCommand, this.executor,
                            attribute, this.completions, this.parents);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to create command", e);
        }
    }
}
