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

package me.kafein.commands.jda;

import me.kafein.commands.common.command.CommandExecutor;
import me.kafein.commands.common.command.CommandManager;
import me.kafein.commands.common.command.abstraction.ParentCommand;
import me.kafein.commands.jda.component.JDASenderComponent;
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
    private final ParentCommand command;

    public JDACommandExecutor(@NotNull CommandManager<OptionMapping> manager, @NotNull ParentCommand command) {
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

        InteractionHook reply;
        if (command.reply()) {
            reply = event.deferReply().setEphemeral(true).complete();
        }else {
            reply = null;
        }

        OptionMapping[] args = event.getOptions().toArray(new OptionMapping[0]);
        execute(manager, command, event.getSubcommandName(), args, new JDASenderComponent(member, event, reply), false);
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
