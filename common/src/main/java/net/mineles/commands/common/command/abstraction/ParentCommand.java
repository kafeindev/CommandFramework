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

package net.mineles.commands.common.command.abstraction;

import net.mineles.commands.common.command.BaseCommand;
import net.mineles.commands.common.command.CommandAttribute;
import net.mineles.commands.common.command.completion.RegisteredCompletion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ParentCommand<T> extends AbstractCommand<T> {

    private final List<ChildCommand<T>> child = new ArrayList<>();

    public ParentCommand(@NotNull BaseCommand baseCommand, @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions) {
        super(baseCommand, attribute, completions);
    }

    public ParentCommand(@NotNull BaseCommand baseCommand, @Nullable Method executor,
                         @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions) {
        super(baseCommand, executor, attribute, completions);
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @NotNull
    public List<ChildCommand<T>> findAllChild() {
        return this.child;
    }

    @NotNull
    public List<String> findAllChildAliases() {
        List<String> aliases = new ArrayList<>();

        for (ChildCommand<T> childCommand : this.child) {
            aliases.addAll(Arrays.asList(childCommand.getAliases()));
        }

        return aliases;
    }

    public Optional<ChildCommand<T>> findChild(String alias) {
        return this.child.stream()
                .filter(c -> c.containsAlias(alias))
                .findFirst();
    }

    public void putChild(ChildCommand<T> childCommand) {
        this.child.add(childCommand);
    }

    public boolean hasChild() {
        return !this.child.isEmpty();
    }
}