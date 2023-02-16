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

package com.github.kafeintr.commands.common.command.misc;

import com.github.kafeintr.commands.common.command.completion.RegisteredCompletion;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public final class CommandPatternProcessor {
    private CommandPatternProcessor() {}

    @NotNull
    public static List<String> processParents(@NotNull String value) {
        String[] split = CommandPatternProvider.PIPE.split(value);
        return new LinkedList<>(ImmutableList.copyOf(split));
    }

    @NotNull
    public static String[] processAlias(@NotNull String value) {
        return CommandPatternProvider.PIPE.split(value);
    }

    @NotNull
    public static RegisteredCompletion[] processCompletion(@NotNull String value) {
        final String[] split1 = processAlias(value);

        final RegisteredCompletion[] completions = new RegisteredCompletion[split1.length];
        for (int i = 0; i < split1.length; i++) {
            final String[] split2 = split1[i].split("-");

            completions[i] = new RegisteredCompletion(Integer.parseInt(split2[0]), split2[1]);
        }

        return completions;
    }
}
