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

package com.github.kafeintr.commands.common.command.context;

import com.github.kafeintr.commands.common.component.SenderComponent;
import com.github.kafeintr.commands.common.predicates.DefaultParameterPredicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;

public final class CommandContextResolver<T> {

    @NotNull
    private final CommandContextProvider<T> provider;

    public CommandContextResolver(@NotNull CommandContextProvider<T> provider) {
        this.provider = provider;
    }

    @Nullable
    public Object[] resolve(@NotNull SenderComponent sender, @NotNull Parameter[] parameters,
                            @Nullable T[] args, boolean argsRequired) {
        Object[] result = new Object[parameters.length];

        int argIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();

            CommandContext<T> context = this.provider.find(type)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find context for type " + type.getName()));

            if (argsRequired && (args == null || argIndex >= args.length)) {
                return null;
            }

            T arg = args != null && args.length > argIndex ? args[argIndex] : null;
            try {
                Object handledContext = context.handle(sender, args, arg, parameter);

                result[i] = handledContext;
            } catch (NullPointerException e) {
                result[i] = null;
            }

            if (!(DefaultParameterPredicates.IS_DEFAULT_PARAMETER.test(type))) {
                argIndex++;
            }
        }

        return result;
    }

    @NotNull
    public CommandContextProvider<T> getProvider() {
        return this.provider;
    }
}
