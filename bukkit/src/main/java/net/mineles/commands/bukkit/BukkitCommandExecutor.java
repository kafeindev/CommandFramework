/*
 * MIT License
 *
 * Copyright (c) 2022 Mineles Command Framework
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

package net.mineles.commands.bukkit;

import net.mineles.commands.bukkit.component.BukkitSenderComponent;
import net.mineles.commands.common.command.CommandExecutor;
import net.mineles.commands.common.command.CommandManager;
import net.mineles.commands.common.command.abstraction.ChildCommand;
import net.mineles.commands.common.command.abstraction.ParentCommand;
import net.mineles.commands.common.command.completion.Completion;
import net.mineles.commands.common.command.completion.RegisteredCompletion;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class BukkitCommandExecutor extends BukkitCommand implements CommandExecutor<String> {

    @NotNull
    private final CommandManager<String> manager;

    @NotNull
    private final ParentCommand command;

    public BukkitCommandExecutor(@NotNull CommandManager<String> manager, @NotNull ParentCommand command) {
        super(command.getAliases()[0], command.getDescription(), command.getUsage(), Arrays.asList(command.getAliases()));
        this.command = command;
        this.manager = manager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!command.containsAlias(label)) return true;

        execute(manager, command, args[0], resolveArgs(args), new BukkitSenderComponent(sender), true);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) return command.findAllChildAliases();
        if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
            return null;
        }

        try {
            ChildCommand childCommand = command.findChild(args[0]).orElse(null);
            if (childCommand.getPermission() != null && !sender.hasPermission(childCommand.getPermission())) {
                return null;
            }

            RegisteredCompletion registeredCompletion = childCommand.findCompletion(args).orElse(null);
            Completion completion = manager.findCompletion(registeredCompletion.getName()).orElse(null);
            return completion.getCompletions(new BukkitSenderComponent(sender));
        } catch (NullPointerException e) {
            return null;
        }
    }

    private String[] resolveArgs(@NotNull String[] args) {
        if (args.length == 0) return args;

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }
}
