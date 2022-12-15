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

package net.mineles.commands.common.command.context;

import net.mineles.commands.common.component.SenderComponent;
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
    public Object[] resolve(@NotNull SenderComponent sender, @NotNull Parameter[] parameters, @NotNull T[] args) {
        Object[] result = new Object[parameters.length];

        int argIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();

            CommandContext<T> context = this.provider.find(type)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find context for type " + type.getName()));

            Object handledContext = args.length > 0
                    ? context.handle(sender, args, args[argIndex], parameter)
                    : null;
            if (handledContext == null) {
                return null;
            } else {
                if (!(SenderComponent.class.isAssignableFrom(type) || type.equals(String[].class))) {
                    argIndex++;
                }
                result[i] = handledContext;
            }
        }

        return result;
    }

    @NotNull
    public CommandContextProvider<T> getProvider() {
        return this.provider;
    }
}
