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

package com.github.kafeindev.commands.common.command.context.resolver;

import com.github.kafeindev.commands.common.command.context.CommandContext;
import com.github.kafeindev.commands.common.command.context.provider.ContextProvider;
import com.github.kafeindev.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;

public final class DefaultContextResolver<T> extends ContextResolver<T> {
    public DefaultContextResolver(@NotNull ContextProvider<T> provider) {
        super(provider);
    }

    @Override
    public @Nullable Object[] resolve(@NotNull SenderComponent sender, @Nullable Parameter[] parameters,
                                      @Nullable T[] args) {
        Object[] result = new Object[parameters.length];

        int argIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();

            T arg = args != null && args.length > argIndex ? args[argIndex] : null;
            try {
                CommandContext<T> context = getProvider().find(type)
                        .orElseThrow(() -> new IllegalArgumentException("Cannot find context for type " + type.getName()));

                Object handledContext = context.handle(sender, args, arg, parameter);
                if (handledContext == null) {
                    return null;
                }
                result[i] = handledContext;
            } catch (NullPointerException e) {
                result[i] = null;
            }

            if (!(getProvider().isDefaultParameter(type))) {
                argIndex++;
            }
        }

        return result;
    }
}
