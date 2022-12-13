package com.mineles.commands.jda;

import com.mineles.commands.common.command.CommandExecutor;
import com.mineles.commands.common.command.CommandManager;
import com.mineles.commands.common.command.abstraction.ParentCommand;
import com.mineles.commands.common.component.SenderComponent;
import com.mineles.commands.jda.component.JDASenderComponent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class JDACommandExecutor extends ListenerAdapter implements CommandExecutor {

    @NotNull
    private final CommandManager manager;

    @NotNull
    private final ParentCommand command;

    public JDACommandExecutor(@NotNull CommandManager manager, @NotNull ParentCommand command) {
        this.manager = manager;
        this.command = command;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }

        MessageChannel channel = event.getChannel();
        SenderComponent senderComponent = new JDASenderComponent(member, channel);

        String commandName = event.getName();
        String[] subCommands = event.getSubcommandName() != null
                ? new String[]{event.getSubcommandName()}
                : new String[0];

        execute(this.manager, this.command, senderComponent, commandName, subCommands);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }

        MessageChannel channel = event.getChannel();
        SenderComponent senderComponent = new JDASenderComponent(member, channel);

        String[] subCommands = event.getSubcommandName() != null
                ? new String[]{event.getSubcommandName()}
                : new String[0];

        List<Command.Choice> choices = tabComplete(this.manager, this.command, senderComponent, subCommands).stream()
                .map(choice -> new Command.Choice(choice, choice))
                .collect(Collectors.toList());
        event.replyChoices(choices).queue();
    }
}
