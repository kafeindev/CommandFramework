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

package net.mineles.commands.common.command.abstraction;

import net.mineles.commands.common.command.BaseCommand;
import net.mineles.commands.common.command.CommandAttribute;
import net.mineles.commands.common.command.completion.RegisteredCompletion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public final class ChildCommand extends AbstractCommand {

    public ChildCommand(@NotNull BaseCommand baseCommand, @NotNull CommandAttribute attribute,
                        @Nullable RegisteredCompletion[] completions) {
        super(baseCommand, attribute, completions);
    }

    public ChildCommand(@NotNull BaseCommand baseCommand, @Nullable Method executor,
                        @NotNull CommandAttribute attribute, @Nullable RegisteredCompletion[] completions) {
        super(baseCommand, executor, attribute, completions);
    }

    @Override
    public boolean isChild() {
        return true;
    }

}
