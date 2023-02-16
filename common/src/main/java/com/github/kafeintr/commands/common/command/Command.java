/*
 * MIT License
 *
 * Copyright (c) 2023 Kafein's CommandFramework
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

package com.github.kafeintr.commands.common.command;

import com.github.kafeintr.commands.common.command.base.BaseCommand;
import com.github.kafeintr.commands.common.command.completion.RegisteredCompletion;
import com.github.kafeintr.commands.common.component.SenderComponent;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface Command {
    void execute(@NotNull SenderComponent sender, @Nullable Object[] parameters);

    @NotNull BaseCommand getBaseCommand();

    @Nullable Method getExecutor();

    @NotNull CommandAttribute getAttribute();

    @NotNull
    default String[] getAliases() {
        return getAttribute().getAliases();
    }

    boolean containsAlias(@NotNull String alias);

    @NotNull
    default String getDescription() {
        return getAttribute().getDescription();
    }

    @NotNull
    default String getUsage() {
        return getAttribute().getUsage();
    }

    default void setUsage(@NotNull String usage) {
        getAttribute().setUsage(usage);
    }

    @Nullable
    default String getPermission() {
        return getAttribute().getPermission();
    }

    @NotNull
    default String getPermissionMessage() {
        return getAttribute().getPermissionMessage();
    }

    @NotNull List<String> getParentCommands();

    void putParentCommand(@NotNull String parent);

    void putParentCommands(@NotNull List<String> parents);

    default boolean isSubCommand() {
        return !getParentCommands().isEmpty();
    }

    @NotNull Set<Command> getSubCommands();

    @NotNull
    default Set<String> getSubCommandsAliases() {
        return getSubCommands().stream()
                .map(Command::getAliases)
                .map(ImmutableSet::copyOf)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @NotNull
    default Set<String> matchSubCommandsAliases(@NotNull String alias) {
        Set<String> aliases = getSubCommandsAliases();

        boolean match = !alias.isEmpty();
        if (match) {
            aliases = aliases.stream()
                    .filter(a -> a.startsWith(alias) || a.equalsIgnoreCase(alias))
                    .collect(Collectors.toSet());
        }
        return aliases;
    }

    @NotNull Optional<Command> findSubCommand(@NotNull String sub);

    @NotNull Optional<Command> findSubCommand(@NotNull String... subs);

    @NotNull Command forceFindSubCommand(@NotNull String... subs);

    void putSubCommand(@NotNull Command command);

    void removeSubCommand(@NotNull Command command);

    default boolean hasSubCommands() {
        return !getSubCommands().isEmpty();
    }

    @Nullable RegisteredCompletion[] getCompletions();

    @NotNull Optional<RegisteredCompletion> findCompletion(int index);
}
