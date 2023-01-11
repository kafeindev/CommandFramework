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

package com.github.kafeintr.commands.jda;

import com.github.kafeintr.commands.common.command.CommandExecutor;
import com.github.kafeintr.commands.common.command.CommandManager;
import com.github.kafeintr.commands.common.command.abstraction.ChildCommand;
import com.github.kafeintr.commands.common.command.abstraction.ParentCommand;
import com.github.kafeintr.commands.common.command.completion.Completion;
import com.github.kafeintr.commands.common.command.completion.RegisteredCompletion;
import com.github.kafeintr.commands.jda.component.JDASenderComponent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }

        String commandName = event.getName();
        if (!command.containsAlias(commandName)) return;

        int index = event.getOptions().size();

        Optional<RegisteredCompletion> optionalRegisteredCompletion;
        if (event.getSubcommandName() == null) {
            optionalRegisteredCompletion = command.findCompletion(index);
        }else {
            String subcommandName = event.getSubcommandName();

            Optional<ChildCommand> childCommand = command.findChild(subcommandName);
            if (!childCommand.isPresent()) {
                return;
            }

            optionalRegisteredCompletion = childCommand.get().findCompletion(index);
        }

        if (optionalRegisteredCompletion.isPresent()) {
            Optional<Completion> optionalCompletion = manager.findCompletion(optionalRegisteredCompletion.get().getName());
            if (optionalCompletion.isPresent()) {
                Completion completion = optionalCompletion.get();

                List<String> completions = completion.getCompletions(new JDASenderComponent(member, null, null));
                event.replyChoices(completions.stream()
                                .map(s -> new Command.Choice(s, s))
                                .collect(Collectors.toList()))
                        .queue();
            }
        }
    }
}
