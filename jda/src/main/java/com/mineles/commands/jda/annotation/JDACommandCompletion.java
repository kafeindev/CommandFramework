package com.mineles.commands.jda.annotation;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface JDACommandCompletion {

    OptionType type();

    String name();

    String description();

    boolean required() default false;

    boolean autoComplete() default false;

    long min() default 0;

    long max() default 100;

    ChannelType[] channelTypes() default {ChannelType.TEXT};

}