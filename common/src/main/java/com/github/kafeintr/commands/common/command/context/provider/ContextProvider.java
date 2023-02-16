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

package com.github.kafeintr.commands.common.command.context.provider;

import com.github.kafeintr.commands.common.command.context.CommandContext;
import com.github.kafeintr.commands.common.component.SenderComponent;
import com.github.kafeintr.commands.common.manager.AbstractManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class ContextProvider<T> extends AbstractManager<Class<?>, CommandContext<T>> {
    protected final Set<Class<?>> defaultParameterClasses = new HashSet<>();

    public void initialize(){
        // Default parameters
        registerDefaultParameter(String[].class, (sender, args, value, parameter) -> args);
        registerDefaultParameter(SenderComponent.class, (sender, args, value, parameter) -> sender);

        put(String.class, (sender, args, value, parameter) -> value);
    }

    public boolean isDefaultParameter(@NotNull Class<?> clazz) {
        return this.defaultParameterClasses.contains(clazz);
    }

    public void registerDefaultParameter(@NotNull Class<?> clazz, @NotNull CommandContext<T> context) {
        this.defaultParameterClasses.add(clazz);

        put(clazz, context);
    }
}
