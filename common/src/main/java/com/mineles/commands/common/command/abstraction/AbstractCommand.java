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

package com.mineles.commands.common.command.abstraction;

import com.mineles.commands.common.command.BaseCommand;
import com.mineles.commands.common.command.CommandAttribute;
import com.mineles.commands.common.command.completion.RegisteredCompletion;
import com.mineles.commands.common.command.context.CommandContextResolver;
import com.mineles.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

public abstract class AbstractCommand<T> {

    @NotNull
    private final BaseCommand baseCommand;

    @NotNull
    private final Method executor;

    @NotNull
    private final CommandAttribute attribute;

    @Nullable
    private final RegisteredCompletion[] completions;

    protected AbstractCommand(@NotNull BaseCommand baseCommand, @NotNull Method executor,
                              @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions) {
        this.baseCommand = baseCommand;
        this.executor = executor;
        this.attribute = attribute;
        this.completions = completions;
    }

    public abstract boolean isChild();

    public void execute(@NotNull SenderComponent sender, @NotNull CommandContextResolver<T> contextResolver, @NotNull T[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        try {
            Object[] resolvedParameters;
            if (isChild()) {
                Parameter[] parameters = this.executor.getParameters();
                if (args.length != getAvailableArgumentSize(parameters)) {
                    sender.sendMessage(getUsage());
                    return;
                }

                resolvedParameters = contextResolver.resolve(sender, parameters, args);
            } else {
                resolvedParameters = new Object[]{sender, args};
            }

            if (resolvedParameters != null) {
                this.executor.invoke(this.baseCommand, resolvedParameters);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Failed to execute command", e);
        }
    }

    public int getAvailableArgumentSize(@NotNull Parameter[] parameters) {
        int availableArgument = 0;
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            if (type.equals(SenderComponent.class) || type.equals(String[].class)) {
                continue;
            }

            availableArgument++;
        }

        return availableArgument;
    }

    @NotNull
    public BaseCommand getBaseCommand() {
        return this.baseCommand;
    }

    @NotNull
    public Method getExecutor() {
        return this.executor;
    }

    @NotNull
    public String[] getAliases() {
        return this.attribute.getAliases();
    }

    public boolean containsAlias(String alias) {
        for (String s : getAliases()) {
            if (s.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public String getDescription() {
        return this.attribute.getDescription();
    }

    @NotNull
    public String getUsage() {
        return this.attribute.getUsage();
    }

    @Nullable
    public RegisteredCompletion[] getCompletions() {
        return this.completions;
    }

    public Optional<RegisteredCompletion> findCompletion(String[] args) {
        if (this.completions == null) {
            return Optional.empty();
        }

        for (RegisteredCompletion completion : completions) {
            if (completion.getIndex() == args.length - 1) {
                return Optional.of(completion);
            }
        }

        return Optional.empty();
    }

    @Nullable
    public String getPermission() {
        return this.attribute.getPermission();
    }

    @NotNull
    public String getPermissionMessage() {
        return this.attribute.getPermissionMessage();
    }
}
