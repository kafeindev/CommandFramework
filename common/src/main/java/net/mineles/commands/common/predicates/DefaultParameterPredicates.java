package net.mineles.commands.common.predicates;

import com.google.common.collect.ImmutableList;
import net.mineles.commands.common.component.SenderComponent;

import java.util.List;
import java.util.function.Predicate;

public final class DefaultParameterPredicates {

    private static final List<Class<?>> DEFAULT_PARAMETER_CLASSES = ImmutableList.of(
            SenderComponent.class, String[].class
    );

    public static final Predicate<Class<?>> IS_DEFAULT_PARAMETER = clazz -> DEFAULT_PARAMETER_CLASSES.stream()
            .anyMatch(defaultParameterClazz -> clazz.equals(defaultParameterClazz) || defaultParameterClazz.isAssignableFrom(clazz));

    private DefaultParameterPredicates() {}

}
