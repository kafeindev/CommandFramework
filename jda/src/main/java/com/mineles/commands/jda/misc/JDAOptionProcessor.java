package com.mineles.commands.jda.misc;

import com.google.common.collect.ImmutableList;
import com.mineles.commands.common.command.BaseCommand;
import com.mineles.commands.jda.annotation.JDACommandCompletion;
import com.mineles.commands.jda.annotation.JDACommandCompletions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JDAOptionProcessor {

    private static final List<Class<? extends Annotation>> ANNOTATIONS = ImmutableList.of(
            JDACommandCompletion.class, JDACommandCompletions.class
    );

    private JDAOptionProcessor() {
    }

    @NotNull
    public static OptionData[] process(@NotNull BaseCommand baseCommand) {
        Class<?> clazz = baseCommand.getClass();

        List<JDACommandCompletion> completions = new ArrayList<>();

        ANNOTATIONS.forEach(annotationClass -> {
            Annotation annotation = clazz.getAnnotation(annotationClass);
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
            }else if (completion.type() == OptionType.CHANNEL){
                option.setChannelTypes(completion.channelTypes());
            }

            optionData[i] = option;
        }

        return optionData;
    }
}
