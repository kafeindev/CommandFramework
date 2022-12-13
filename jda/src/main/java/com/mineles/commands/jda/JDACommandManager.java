package com.mineles.commands.jda;

import com.mineles.commands.common.command.BaseCommand;
import com.mineles.commands.common.command.CommandManager;
import com.mineles.commands.common.command.abstraction.ParentCommand;
import com.mineles.commands.jda.misc.JDAOptionProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

public final class JDACommandManager extends CommandManager {

    @NotNull
    private final JDA jda;

    public JDACommandManager(@NotNull JDA jda) {
        super(new JDACompletionProvider(), new JDACommandContextProvider());

        this.jda = jda;
    }

    @Override
    public void initializeRegisteredCommand(@NotNull ParentCommand command) {
        for (String alias : command.getAliases()) {
            CommandCreateAction commandCreateAction = this.jda.upsertCommand(alias, command.getDescription());

            OptionData[] optionData = JDAOptionProcessor.process(command.getBaseCommand());
            commandCreateAction.addOptions(optionData);

            commandCreateAction.queue();
        }

        JDACommandExecutor executor = new JDACommandExecutor(this, command);
        this.jda.addEventListener(executor);
    }
}
