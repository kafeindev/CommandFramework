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

import com.github.kafeindev.commands.common.command.base.BaseCommand;
import com.github.kafeindev.commands.common.command.annotation.Subcommand;
import com.github.kafeindev.commands.common.command.completion.Completion;
import com.github.kafeindev.commands.common.command.completion.CompletionProvider;
import com.github.kafeindev.commands.common.command.context.CommandContext;
import com.github.kafeindev.commands.common.command.context.provider.ContextProvider;
import com.github.kafeindev.commands.common.command.context.resolver.ContextResolver;
import com.github.kafeindev.commands.common.command.convert.CommandConverter;
import com.github.kafeindev.commands.common.reflect.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class CommandManager<T> {
    protected final List<Command> commands = new LinkedList<>();

    @NotNull
    private final CommandConverter converter;

    @NotNull
    private final CompletionProvider completionProvider;

    @NotNull
    private final ContextResolver<T> contextResolver;

    protected CommandManager(@NotNull CommandConverter converter, @NotNull CompletionProvider completionProvider,
                             @NotNull ContextResolver<T> contextResolver) {
        this.converter = converter;
        this.contextResolver = contextResolver;

        this.completionProvider = completionProvider;
        this.completionProvider.initialize();
    }

    public abstract void initializeRegisteredCommand(@NotNull Command command);

    public void registerCommand(@NotNull BaseCommand... baseCommands) {
        for (BaseCommand baseCommand : baseCommands) {
            Command command = this.converter.convert(baseCommand);
            applySubCommands(baseCommand, command);

            if (command.isSubCommand()) {
                registerSubcommand(command);
            } else {
                registerCommand(command);
            }
        }
    }

    public void registerCommand(@NotNull Command command) {
        initializeRegisteredCommand(command);

        this.commands.add(command);
    }

    private void registerSubcommand(@NotNull Command command) {
        Optional<Command> optionalParent = findCommand(command.getParentCommands().get(0));
        if (!optionalParent.isPresent()) {
            throw new IllegalStateException("Parent command not found for subcommand " + command.getAliases()[0]);
        }

        Command parent = optionalParent.get();
        parent.putSubCommand(command);
    }

    private void applySubCommands(@NotNull BaseCommand baseCommand, @NotNull Command command) {
        ReflectionUtils.getMethodsAnnotatedWith(baseCommand.getClass(), Subcommand.class, true).forEach(method -> {
            Command subCommand = this.converter.convert(baseCommand, method);
            if (subCommand == null) {
                return;
            }

            if (!subCommand.isSubCommand()) {
                subCommand.putParentCommands(command.getParentCommands());
                subCommand.putParentCommand(command.getAliases()[0]);
            }
            updateUsage(subCommand);
            command.putSubCommand(subCommand);
        });
    }

    private void updateUsage(@NotNull Command command) {
        if (!command.getAttribute().isUsingDefaultUsage()) {
            return;
        }

        StringBuilder usage = new StringBuilder("/")
                .append(command.getParentCommands().stream()
                        .reduce((s, s2) -> s + " " + s2)
                        .orElse(""))
                .append(" ")
                .append(command.getAliases()[0]);
        for (int i = 0; i < calculateRequiredArgsCount(command); i++) {
            usage.append(" <arg-").append(i).append(">");
        }
        command.getAttribute().setUsage(usage.toString());
    }

    public int calculateRequiredArgsCount(@NotNull Command command) {
        int requiredArgsCount = 0;

        for (Parameter parameter : command.getExecutor().getParameters()) {
            Class<?> type = parameter.getType();
            if (getContextProvider().isDefaultParameter(type)) {
                continue;
            }

            requiredArgsCount++;
        }
        return requiredArgsCount;
    }

    @NotNull
    public Optional<Command> findCommand(@NotNull String alias) {
        return this.commands.stream()
                .filter(command -> command.containsAlias(alias))
                .findFirst();
    }

    @NotNull
    public Optional<Command> findCommand(@NotNull String alias, @NotNull String[] subs) {
        Optional<Command> command = findCommand(alias);
        return command.flatMap(parent -> parent.findSubCommand(subs));
    }

    @NotNull
    public Optional<Completion> findCompletion(@NotNull String completion) {
        return this.completionProvider.find(completion);
    }

    public void registerCompletion(@NotNull Completion completion) {
        this.completionProvider.register(completion);
    }

    public void registerContext(@NotNull Class<?> clazz, @NotNull CommandContext<T> context) {
        getContextProvider().put(clazz, context);
    }

    @NotNull
    public ContextProvider<T> getContextProvider() {
        return this.contextResolver.getProvider();
    }

    @NotNull
    public ContextResolver<T> getContextResolver() {
        return this.contextResolver;
    }

    @NotNull
    public CompletionProvider getCompletionProvider() {
        return this.completionProvider;
    }
}
