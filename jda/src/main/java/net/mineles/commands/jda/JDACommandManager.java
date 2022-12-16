package net.mineles.commands.jda;

import net.mineles.commands.common.command.CommandManager;
import net.mineles.commands.common.command.abstraction.AbstractCommand;
import net.mineles.commands.common.command.abstraction.ChildCommand;
import net.mineles.commands.common.command.abstraction.ParentCommand;
import net.mineles.commands.jda.context.JDACommandContextProvider;
import net.mineles.commands.jda.misc.JDAOptionProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class JDACommandManager extends CommandManager<OptionMapping> {

    @NotNull
    private final JDA jda;

    public JDACommandManager(@NotNull JDA jda) {
        super(new JDACompletionProvider(), new JDACommandContextProvider());

        this.jda = jda;
    }

    @Override
    public void initializeRegisteredCommand(@NotNull ParentCommand<OptionMapping> command) {
        for (String alias : command.getAliases()) {
            CommandCreateAction commandCreateAction = this.jda.upsertCommand(alias, command.getDescription());

            if (!command.hasChild()) {
                Method executor = command.getExecutor();

                OptionData[] optionData = JDAOptionProcessor.process(executor);
                commandCreateAction.addOptions(optionData);
            }else {
                for (ChildCommand<OptionMapping> childCommand : command.findAllChild()) {
                    Method method = childCommand.getExecutor();

                    SubcommandData[] subcommands = new SubcommandData[childCommand.getAliases().length];
                    for (int i = 0; i < childCommand.getAliases().length; i++) {
                        SubcommandData subcommandData = new SubcommandData(childCommand.getAliases()[i], childCommand.getDescription());

                        OptionData[] optionData = JDAOptionProcessor.process(method);
                        subcommandData.addOptions(optionData);

                        subcommands[i] = subcommandData;
                    }

                    commandCreateAction.addSubcommands(subcommands);
                }
            }

            commandCreateAction.queue();
        }

        this.jda.addEventListener(new JDACommandExecutor(this, command));
    }
}
