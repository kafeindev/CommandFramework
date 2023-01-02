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

package com.github.kafeintr.commands.common.command.abstraction;

import com.github.kafeintr.commands.common.command.BaseCommand;
import com.github.kafeintr.commands.common.command.CommandAttribute;
import com.github.kafeintr.commands.common.command.completion.RegisteredCompletion;
import com.github.kafeintr.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public abstract class AbstractCommand {

    @NotNull
    private final BaseCommand baseCommand;

    @Nullable
    private final Method executor;

    @NotNull
    private final CommandAttribute attribute;

    @Nullable
    private final RegisteredCompletion[] completions;

    private final boolean reply;

    protected AbstractCommand(@NotNull BaseCommand baseCommand, @NotNull CommandAttribute attribute,
                              @Nullable RegisteredCompletion[] completions, boolean reply) {
        this(baseCommand, null, attribute, completions, reply);
    }

    protected AbstractCommand(@NotNull BaseCommand baseCommand, @Nullable Method executor,
                              @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions,
                              boolean reply) {
        this.baseCommand = baseCommand;
        this.executor = executor;
        this.attribute = attribute;
        this.completions = completions;
        this.reply = reply;
    }

    public abstract boolean isChild();

    public void execute(@NotNull SenderComponent sender, @Nullable Object[] parameters) {
        if (executor == null) {
            sender.sendMessage(getUsage());
            return;
        } else if (getPermission() != null && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        try {
            executor.invoke(this.baseCommand, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public BaseCommand getBaseCommand() {
        return this.baseCommand;
    }

    @Nullable
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

    public boolean reply() {
        return this.reply;
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
