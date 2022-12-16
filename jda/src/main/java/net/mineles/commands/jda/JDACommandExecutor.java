package net.mineles.commands.jda;

import net.mineles.commands.common.command.CommandExecutor;
import net.mineles.commands.common.command.CommandManager;
import net.mineles.commands.common.command.abstraction.ParentCommand;
import net.mineles.commands.jda.component.JDASenderComponent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public final class JDACommandExecutor extends ListenerAdapter implements CommandExecutor<OptionMapping> {

    @NotNull
    private final CommandManager<OptionMapping> manager;

    @NotNull
    private final ParentCommand<OptionMapping> command;

    public JDACommandExecutor(@NotNull CommandManager<OptionMapping> manager, @NotNull ParentCommand<OptionMapping> command) {
        this.manager = manager;
        this.command = command;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }

        String commandName = event.getName();
        if (!command.containsAlias(commandName)) return;

        InteractionHook reply = event.deferReply().setEphemeral(true).complete();

        OptionMapping[] args = event.getOptions().toArray(new OptionMapping[0]);
        execute(manager, command, event.getSubcommandName(), args, new JDASenderComponent(member, reply));
    }

    /*@Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }

        TextChannel channel = event.getTextChannel();
        SenderComponent senderComponent = new JDASenderComponent(member, channel);

        String[] subCommands = event.getSubcommandName() != null
                ? new String[]{event.getSubcommandName()}
                : new String[0];

        List<Command.Choice> choices = tabComplete(this.manager, this.command, senderComponent, subCommands).stream()
                .map(choice -> new Command.Choice(choice, choice))
                .collect(Collectors.toList());
        event.replyChoices(choices).queue();
    }*/
}
