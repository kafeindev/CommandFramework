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
import com.mineles.commands.common.command.completion.RegisteredCompletion;
import com.mineles.commands.common.command.context.CommandContextResolver;
import com.mineles.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CommandExecutor {

    default void execute(@NotNull CommandManager<String> manager,
                         @NotNull ParentCommand<String> command, @NotNull SenderComponent sender,
                         @NotNull String label, @NotNull String[] args) {
        if (!command.containsAlias(label)) return;

        CommandContextResolver<String> contextResolver = manager.getContextResolver();
        if (args.length == 0 || !command.findChild(args[0]).isPresent()) {
            command.execute(sender, contextResolver, args);
        } else {
            if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                sender.sendMessage(command.getPermissionMessage());
                return;
            }

            ChildCommand<String> childCommand = command.findChild(args[0]).get();
            childCommand.execute(sender, contextResolver, resolveArgs(args));
        }
    }

    @Nullable
    default List<String> tabComplete(@NotNull CommandManager<String> manager, @NotNull ParentCommand<String> command,
                                     @NotNull SenderComponent sender, @NotNull String[] args) {
        if (args.length == 1) return command.findAllChildAliases();
        if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
            return null;
        }

        try {
            ChildCommand<String> childCommand = command.findChild(args[0]).orElse(null);
            if (childCommand.getPermission() != null && !sender.hasPermission(childCommand.getPermission())) {
                return null;
            }

            RegisteredCompletion registeredCompletion = childCommand.findCompletion(args).orElse(null);
            Completion completion = manager.findCompletion(registeredCompletion.getName()).orElse(null);
            return completion.getCompletions(sender);
        }catch (NullPointerException e) {
            return null;
        }
    }

    default String[] resolveArgs(@NotNull String[] args) {
        if (args.length == 0) return args;

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }
}
