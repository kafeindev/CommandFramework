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

import com.github.kafeintr.commands.common.command.Command;
import com.github.kafeintr.commands.common.command.CommandAttribute;
import com.github.kafeintr.commands.common.command.base.BaseCommand;
import com.github.kafeintr.commands.common.command.completion.RegisteredCompletion;
import com.github.kafeintr.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractCommand implements Command {
    @NotNull
    private final BaseCommand baseCommand;

    @Nullable
    private final Method executor;

    @NotNull
    private final CommandAttribute attribute;

    @Nullable
    private final RegisteredCompletion[] completions;

    private final List<String> parentCommands;

    private final Set<Command> subcommands;

    protected AbstractCommand(@NotNull BaseCommand baseCommand, @Nullable Method executor,
                              @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions,
                              @Nullable List<String> parentCommands) {
        this.baseCommand = baseCommand;
        this.executor = executor;
        this.attribute = attribute;
        this.completions = completions;
        this.parentCommands = parentCommands == null ? new LinkedList<>() : parentCommands;
        this.subcommands = new LinkedHashSet<>();
    }

    @Override
    public void execute(@NotNull SenderComponent sender, @Nullable Object[] parameters) {
        if (this.executor == null) {
            sender.sendMessage(getUsage());
            return;
        } else if (getPermission() != null && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        try {
            if (parameters != null) {
                this.executor.invoke(this.baseCommand, parameters);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("An error occurred while executing command: " + getAliases()[0], e);
        }
    }

    @Override
    public @NotNull BaseCommand getBaseCommand() {
        return this.baseCommand;
    }

    @Override
    public @Nullable Method getExecutor() {
        return this.executor;
    }

    @Override
    public @NotNull CommandAttribute getAttribute() {
        return this.attribute;
    }

    @Override
    public boolean containsAlias(@NotNull String alias) {
        for (String s : getAliases()) {
            if (s.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull List<String> getParentCommands() {
        return this.parentCommands;
    }

    @Override
    public void putParentCommand(@NotNull String parent) {
        getParentCommands().add(parent);
    }

    @Override
    public void putParentCommands(@NotNull List<String> parents) {
        getParentCommands().addAll(parents);
    }

    @Override
    public @NotNull Set<Command> getSubCommands() {
        return this.subcommands;
    }

    @Override
    public @NotNull Optional<Command> findSubCommand(@NotNull String sub) {
        return getSubCommands().stream()
                .filter(c -> c.containsAlias(sub))
                .findFirst();
    }

    @Override
    public @NotNull Optional<Command> findSubCommand(@NotNull String... subs) {
        if (subs.length == 1) {
            return findSubCommand(subs[0]);
        } else {
            String[] newSubs = new String[subs.length - 1];
            System.arraycopy(subs, 1, newSubs, 0, subs.length - 1);
            return findSubCommand(subs[0]).flatMap(c -> c.findSubCommand(newSubs));
        }
    }

    @Override
    public @NotNull Command forceFindSubCommand(@NotNull String... subs) {
        if (subs.length == 1) {
            return findSubCommand(subs[0]).orElse(this);
        }

        String[] newSubs = new String[subs.length - 1];
        System.arraycopy(subs, 1, newSubs, 0, subs.length - 1);
        return findSubCommand(subs[0])
                .orElse(this)
                .forceFindSubCommand(newSubs);
    }

    @Override
    public void putSubCommand(@NotNull Command command) {
        List<String> parents = command.getParentCommands();
        if (parents.size() == 1 || parents.size() - 1 == getParentCommands().size()) {
            getSubCommands().add(command);
        } else {
            String[] parentArray = Arrays.copyOfRange(parents.toArray(new String[0]), 1, parents.size());
            findSubCommand(parentArray).ifPresent(c -> c.getSubCommands().add(command));
        }
    }

    @Override
    public void removeSubCommand(@NotNull Command command) {
        List<String> parents = command.getParentCommands();
        if (parents.size() == 1 || parents.size() - 1 == getParentCommands().size()) {
            getSubCommands().remove(command);
        } else {
            String[] parentArray = Arrays.copyOfRange(parents.toArray(new String[0]), 1, parents.size());
            findSubCommand(parentArray).ifPresent(c -> c.getSubCommands().remove(command));
        }
    }

    @Override
    public @Nullable RegisteredCompletion[] getCompletions() {
        return this.completions;
    }

    @Override
    public @NotNull Optional<RegisteredCompletion> findCompletion(int index) {
        if (this.completions == null) {
            return Optional.empty();
        }

        for (RegisteredCompletion completion : completions) {
            if (completion.getIndex() == index) {
                return Optional.of(completion);
            }
        }
        return Optional.empty();
    }
}
