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

package me.kafein.commands.common.command.abstraction;

import me.kafein.commands.common.command.BaseCommand;
import me.kafein.commands.common.command.CommandAttribute;
import me.kafein.commands.common.command.completion.RegisteredCompletion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ParentCommand extends AbstractCommand {

    private final List<ChildCommand> child = new ArrayList<>();

    public ParentCommand(@NotNull BaseCommand baseCommand, @NotNull CommandAttribute attribute,
                         @Nullable RegisteredCompletion[] completions, boolean reply) {
        super(baseCommand, attribute, completions, reply);
    }

    public ParentCommand(@NotNull BaseCommand baseCommand, @Nullable Method executor,
                         @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions,
                         boolean reply) {
        super(baseCommand, executor, attribute, completions, reply);
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @NotNull
    public List<ChildCommand> findAllChild() {
        return this.child;
    }

    @NotNull
    public List<String> findAllChildAliases() {
        List<String> aliases = new ArrayList<>();

        for (ChildCommand childCommand : this.child) {
            aliases.addAll(Arrays.asList(childCommand.getAliases()));
        }

        return aliases;
    }

    public Optional<ChildCommand> findChild(String alias) {
        return this.child.stream()
                .filter(c -> c.containsAlias(alias))
                .findFirst();
    }

    public void putChild(ChildCommand childCommand) {
        this.child.add(childCommand);
    }

    public boolean hasChild() {
        return !this.child.isEmpty();
    }
}