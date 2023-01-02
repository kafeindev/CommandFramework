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

import me.kafein.commands.common.command.abstraction.AbstractCommand;
import me.kafein.commands.common.command.abstraction.ChildCommand;
import me.kafein.commands.common.command.abstraction.ParentCommand;
import me.kafein.commands.common.command.completion.RegisteredCompletion;
import me.kafein.commands.common.command.misc.CommandAnnotationProcessor;
import me.kafein.commands.common.command.misc.CommandPatternProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class CommandBuilder {

    @NotNull
    static CommandBuilder newBuilder(@NotNull BaseCommand baseCommand) {
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
    private String usage;

    @Nullable
    private String description;

    @Nullable
    private String permission;

    @UnknownNullability
    private String permissionMessage;

    private boolean reply = true;

    private boolean isParent;

    private CommandBuilder(@NotNull BaseCommand baseCommand) {
        this.baseCommand = baseCommand;
    }

    public CommandBuilder type(boolean isParent) {
        this.isParent = isParent;
        return this;
    }

    public CommandBuilder executor(@Nullable Method executor) {
        this.executor = executor;
        return this;
    }

    public CommandBuilder aliases(@Nullable Annotation annotation) {
        if (annotation == null) return this;

        final String value = CommandAnnotationProcessor.process(annotation);
        if (value == null) return this;

        this.aliases = CommandPatternProcessor.processAlias(value);
        return this;
    }

    public CommandBuilder usage(@Nullable Annotation annotation) {
        if (annotation == null) return this;

        this.usage = CommandAnnotationProcessor.process(annotation);
        return this;
    }

    public CommandBuilder completions(@Nullable Annotation annotation) {
        if (annotation == null) return this;

        final String value = CommandAnnotationProcessor.process(annotation);
        if (value == null) return this;

        this.completions = CommandPatternProcessor.processCompletion(value);
        return this;
    }

    public CommandBuilder description(@Nullable Annotation annotation) {
        if (annotation == null) return this;

        this.description = CommandAnnotationProcessor.process(annotation);
        return this;
    }

    public CommandBuilder permission(@Nullable Annotation annotation) {
        if (annotation == null) return this;

        this.permission = CommandAnnotationProcessor.process(annotation);
        this.permissionMessage = CommandAnnotationProcessor.process(annotation, "message", true);
        return this;
    }

    public CommandBuilder reply(@Nullable Annotation annotation) {
        if (annotation == null) return this;

        this.reply = CommandAnnotationProcessor.process(annotation, "value", false);
        return this;
    }

    private CommandAttribute buildAttribute() {
        return CommandAttribute.of(
                this.aliases,
                this.usage,
                this.description,
                this.permission,
                this.permissionMessage
        );
    }

    public AbstractCommand build() {
        CommandAttribute attribute = buildAttribute();

        return this.isParent
                ? new ParentCommand(this.baseCommand, this.executor, attribute, this.completions, this.reply)
                : new ChildCommand(this.baseCommand, this.executor, attribute, this.completions, this.reply);
    }
}
