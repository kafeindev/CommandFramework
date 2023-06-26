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

package com.github.kafeindev.commands.bukkit;

import com.github.kafeindev.commands.bukkit.component.BukkitSenderComponent;
import com.github.kafeindev.commands.common.command.Command;
import com.github.kafeindev.commands.common.command.CommandExecutor;
import com.github.kafeindev.commands.common.command.CommandManager;
import com.github.kafeindev.commands.common.command.completion.RegisteredCompletion;
import com.github.kafeindev.commands.common.component.SenderComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class BukkitCommandExecutor extends BukkitCommand implements CommandExecutor<String> {
    @NotNull
    private final CommandManager<String> manager;

    @NotNull
    private final Command command;

    public BukkitCommandExecutor(@NotNull CommandManager<String> manager, @NotNull Command command) {
        super(command.getAliases()[0], command.getDescription(), command.getUsage(), Arrays.asList(command.getAliases()));
        this.command = command;
        this.manager = manager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!command.containsAlias(label)) return true;

        BukkitSenderComponent senderComponent = BukkitSenderComponent.from(sender);
        if (args.length == 0) {
            Object[] resolvedContexts = resolveContexts(manager, command, senderComponent, args, true);
            command.execute(senderComponent, resolvedContexts);
        } else {
            if (command.getPermission() != null && !senderComponent.hasPermission(command.getPermission())) {
                sender.sendMessage(command.getPermissionMessage());
                return false;
            }

            Command subCommand = command.forceFindSubCommand(args);
            String[] subArgs = Arrays.copyOfRange(args, subCommand.getParentCommands().size(), args.length);
            Object[] resolvedContexts = resolveContexts(manager, subCommand, senderComponent, subArgs, true);
            subCommand.execute(senderComponent, resolvedContexts);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args)
            throws IllegalArgumentException {
        SenderComponent senderComponent = BukkitSenderComponent.from(sender);
        if (command.getPermission() != null && !senderComponent.hasPermission(command.getPermission())) {
            return null;
        }

        return args.length == 1
                ? getCompletions(manager, command, senderComponent, args)
                : getCompletions(manager, command.forceFindSubCommand(args), senderComponent, args);
    }

    @Nullable
    private List<String> getCompletions(@NotNull CommandManager<String> manager, @NotNull Command command,
                                        @NotNull SenderComponent sender, @NotNull String[] args) {
        if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
            return null;
        }

        String lastArg = args[args.length - 1];
        List<String> completions = new ArrayList<>(command.matchSubCommandsAliases(lastArg));

        int completionIndex = args.length - command.getParentCommands().size();
        Optional<RegisteredCompletion> registeredCompletion = command.findCompletion(completionIndex);
        if (!registeredCompletion.isPresent()) {
            return completions;
        }

        manager.findCompletion(registeredCompletion.get().getName())
                .ifPresent(completion -> completions.addAll(completion.complete(sender, lastArg)));
        return completions;
    }
}
