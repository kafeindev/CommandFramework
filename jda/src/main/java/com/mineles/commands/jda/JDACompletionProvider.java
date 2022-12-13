package com.mineles.commands.jda;

import com.mineles.commands.common.command.completion.Completion;
import com.mineles.commands.common.command.completion.CompletionProvider;
import com.mineles.commands.common.component.SenderComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class JDACompletionProvider extends CompletionProvider {

    @Override
    public void initialize() {
        /*register(new Completion("@members") {
            @Override
            public List<String> getCompletions(@NotNull SenderComponent sender) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .collect(Collectors.toList());
            }
        });*/
    }

}
