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

import com.github.kafeintr.commands.common.command.Command;
import com.github.kafeintr.commands.common.command.CommandExecutor;
import com.github.kafeintr.commands.common.command.CommandManager;
import com.github.kafeintr.commands.jda.component.JDASenderComponent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public final class JDACommandExecutor extends ListenerAdapter implements CommandExecutor<OptionMapping> {
    @NotNull
    private final CommandManager<OptionMapping> manager;

    public JDACommandExecutor(@NotNull CommandManager<OptionMapping> manager) {
        this.manager = manager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        }

        String commandName = event.getName();
        this.manager.findCommand(commandName).ifPresent(command -> {
            JDASenderComponent senderComponent = new JDASenderComponent(member, event);
            event.deferReply().setEphemeral(true).queue();

            OptionMapping[] args = event.getOptions().toArray(new OptionMapping[0]);
            if (event.getSubcommandGroup() != null || event.getSubcommandName() != null) {
                if (command.getPermission() != null && !senderComponent.hasPermission(command.getPermission())) {
                    senderComponent.sendMessage(command.getPermissionMessage());
                    return;
                }

                String[] subCommandArgs = event.getSubcommandGroup() != null
                        ? new String[]{event.getSubcommandGroup(), event.getSubcommandName()}
                        : new String[]{event.getSubcommandName()};
                Command subCommand = command.forceFindSubCommand(subCommandArgs);

                Object[] resolvedContexts = resolveContexts(manager, subCommand, senderComponent, args, false);
                subCommand.execute(senderComponent, resolvedContexts);
            } else {
                Object[] resolvedContexts = resolveContexts(manager, command, senderComponent, args, false);
                command.execute(senderComponent, resolvedContexts);
            }
        });
    }
}
