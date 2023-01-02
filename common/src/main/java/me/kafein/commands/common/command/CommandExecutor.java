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

import me.kafein.commands.common.component.SenderComponent;
import me.kafein.commands.common.predicates.DefaultParameterPredicates;
import me.kafein.commands.common.command.abstraction.AbstractCommand;
import me.kafein.commands.common.command.abstraction.ParentCommand;
import me.kafein.commands.common.command.context.CommandContextResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface CommandExecutor<T> {

    default void execute(@NotNull CommandManager<T> manager, @NotNull ParentCommand command,
                         @Nullable String subCommand, @NotNull T[] args, @NotNull SenderComponent sender,
                         boolean contextIsRequired) {
        if (subCommand == null || !command.findChild(subCommand).isPresent()) {
            command.execute(sender, resolveContexts(manager.getContextResolver(), command, args, sender, contextIsRequired));
        } else {
            if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                sender.sendMessage(command.getPermissionMessage());
                return;
            }

            command.findChild(subCommand).ifPresent(childCommand -> {
                childCommand.execute(sender, resolveContexts(manager.getContextResolver(), childCommand, args, sender, contextIsRequired));
            });
        }
    }

    @Nullable
    default Object[] resolveContexts(@NotNull CommandContextResolver<T> resolver, @NotNull AbstractCommand command,
                                     @NotNull T[] args, @NotNull SenderComponent sender, boolean contextIsRequired) {
        Method executor = command.getExecutor();

        Parameter[] parameters = executor.getParameters();
        if (contextIsRequired && args.length != getAvailableArgumentSize(parameters)) {
            sender.sendMessage(command.getUsage());
            return null;
        }

        return resolver.resolve(sender, parameters, args);
    }

    default int getAvailableArgumentSize(@NotNull Parameter[] parameters) {
        int availableArgument = 0;
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            if (DefaultParameterPredicates.IS_DEFAULT_PARAMETER.test(type)) {
                continue;
            }

            availableArgument++;
        }

        return availableArgument;
    }
}
