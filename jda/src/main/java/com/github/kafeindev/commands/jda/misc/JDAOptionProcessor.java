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

package com.github.kafeindev.commands.jda.misc;

import com.github.kafeindev.commands.jda.annotation.JDACommandCompletion;
import com.github.kafeindev.commands.jda.annotation.JDACommandCompletions;
import com.github.kafeindev.commands.common.command.Command;
import com.github.kafeindev.commands.jda.JDACommandManager;
import com.google.common.collect.ImmutableList;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class JDAOptionProcessor {
    private static final List<Class<? extends Annotation>> ANNOTATIONS = ImmutableList.of(
            JDACommandCompletion.class, JDACommandCompletions.class
    );

    private JDAOptionProcessor() {
    }

    @Nullable
    public static OptionData[] process(@NotNull JDACommandManager commandManager, @NotNull Command command) {
        Method method = command.getExecutor();
        if (method == null) {
            return null;
        }

        List<JDACommandCompletion> completions = new ArrayList<>();

        ANNOTATIONS.forEach(annotationClass -> {
            Annotation annotation = method.getAnnotation(annotationClass);
            if (annotation == null) {
                return;
            }

            if (annotation instanceof JDACommandCompletion) {
                completions.add((JDACommandCompletion) annotation);
            } else if (annotation instanceof JDACommandCompletions) {
                JDACommandCompletions completionsAnnotation = (JDACommandCompletions) annotation;
                Collections.addAll(completions, completionsAnnotation.value());
            }
        });

        OptionData[] optionData = new OptionData[completions.size()];
        for (int i = 0; i < completions.size(); i++) {
            JDACommandCompletion completion = completions.get(i);

            OptionData option = new OptionData(completion.type(),
                    completion.name(), completion.description(),
                    completion.required(), completion.autoComplete()
            );

            if (completion.type() == OptionType.NUMBER || completion.type() == OptionType.INTEGER) {
                option.setMinValue(completion.min());
                option.setMaxValue(completion.max());
            } else if (completion.type() == OptionType.CHANNEL) {
                option.setChannelTypes(completion.channelTypes());
            }

            if (completion.type().canSupportChoices()) {
                command.findCompletion(i + 1).flatMap(registeredCompletion ->
                        commandManager.findCompletion(registeredCompletion.getName())).ifPresent(c -> {
                    option.setAutoComplete(true);
                    //option.addChoices(resolveChoices(c.complete(null)));
                });
            }

            optionData[i] = option;
        }
        return optionData;
    }

    @NotNull
    public static List<net.dv8tion.jda.api.interactions.commands.Command.Choice> resolveChoices(@NotNull List<String> choices) {
        return choices.stream()
                .map(s -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(s, s))
                .collect(Collectors.toList());
    }
}
