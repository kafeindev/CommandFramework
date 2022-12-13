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
import com.mineles.commands.common.command.completion.Completion;
import com.mineles.commands.common.command.completion.CompletionProvider;
import com.mineles.commands.common.command.context.CommandContext;
import com.mineles.commands.common.command.context.CommandContextProvider;
import com.mineles.commands.common.command.context.CommandContextResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CommandManager {

    private final List<ParentCommand> commands = new ArrayList<>();

    private final CommandConverter converter = new CommandConverter();

    @NotNull
    private final CompletionProvider completionProvider;

    @NotNull
    private final CommandContextResolver contextResolver;

    protected CommandManager(@NotNull CompletionProvider completionProvider, @NotNull CommandContextProvider contextProvider) {
        this.completionProvider = completionProvider;
        completionProvider.initialize();

        contextProvider.initialize();
        this.contextResolver = new CommandContextResolver(contextProvider);
    }

    public Optional<ParentCommand> findCommand(@NotNull String alias) {
        return this.commands.stream()
                .filter(command -> command.containsAlias(alias))
                .findFirst();
    }

    public Optional<ParentCommand> findCommandByChildAliases(@NotNull String alias) {
        return this.commands.stream()
                .filter(command -> command.findChild(alias).isPresent())
                .findFirst();
    }

    public Optional<Completion> findCompletion(@NotNull String completion) {
        return this.completionProvider.find(completion);
    }

    public void registerCommand(@NotNull BaseCommand... baseCommands) {
        for (BaseCommand baseCommand : baseCommands) {
            ParentCommand command = this.converter.convert(baseCommand);

            Optional<ParentCommand> existingCommand = findCommand(command.getAliases()[0]);
            if (existingCommand.isPresent()) {
                command = existingCommand.get();
            }

            for (Method method : baseCommand.getClass().getDeclaredMethods()) {
                method.setAccessible(true);

                ChildCommand childCommand = this.converter.convert(baseCommand, method);
                if (childCommand != null) {
                    command.putChild(childCommand);
                }
            }

            registerCommand(command);
        }
    }

    public void registerCommand(@NotNull ParentCommand command) {
        initializeRegisteredCommand(command);

        this.commands.add(command);
    }

    public void registerCompletion(@NotNull Completion completion) {
        this.completionProvider.register(completion);
    }

    public void registerContext(@NotNull CommandContext context) {
        getContextProvider().register(context);
    }

    public abstract void initializeRegisteredCommand(@NotNull ParentCommand command);

    @NotNull
    public CommandContextProvider getContextProvider() {
        return this.contextResolver.getProvider();
    }

    @NotNull
    public CommandContextResolver getContextResolver() {
        return this.contextResolver;
    }

    @NotNull
    public CompletionProvider getCompletionProvider() {
        return this.completionProvider;
    }
}
