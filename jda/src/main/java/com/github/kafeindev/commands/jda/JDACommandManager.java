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

package com.github.kafeindev.commands.jda;

import com.github.kafeindev.commands.jda.misc.JDAOptionProcessor;
import com.github.kafeindev.commands.common.command.Command;
import com.github.kafeindev.commands.common.command.CommandManager;
import com.github.kafeindev.commands.common.command.context.resolver.DefaultContextResolver;
import com.github.kafeindev.commands.common.command.convert.DefaultCommandConverter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

public final class JDACommandManager extends CommandManager<OptionMapping> {
    @NotNull
    private final JDA jda;

    public JDACommandManager(@NotNull JDA jda) {
        super(new DefaultCommandConverter(), new JDACompletionProvider(),
                new DefaultContextResolver<>(new JDAContextProvider()));
        this.jda = jda;
        this.jda.addEventListener(new JDACommandExecutor(this));

        this.jda.getGatewayPool().schedule(this::initialize, 2, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void initializeRegisteredCommand(@NotNull Command command) {}

    private void initialize() {
        this.commands.forEach(this::initializeCommand);
    }

    private void initializeCommand(@NotNull Command command) {
        for (String alias : command.getAliases()) {
            CommandCreateAction commandCreateAction = this.jda.upsertCommand(alias, command.getDescription());

            if (!command.hasSubCommands()) {
                OptionData[] optionData = JDAOptionProcessor.process(this, command);
                if (optionData != null) {
                    commandCreateAction.addOptions(optionData);
                }
            } else {
                command.getSubCommands().forEach(subCommand -> applySubCommands(commandCreateAction, subCommand));
            }
            commandCreateAction.queue();
        }
    }

    private void applySubCommands(@NotNull CommandCreateAction action, @NotNull Command subCommand) {
        if (subCommand.hasSubCommands()) {
            for (String alias : subCommand.getAliases()) {
                SubcommandGroupData subcommandGroupData = new SubcommandGroupData(alias, subCommand.getDescription());
                subCommand.getSubCommands().forEach(subSubCommand -> {
                    SubcommandData[] subcommands = createJDASubCommandData(subSubCommand);
                    subcommandGroupData.addSubcommands(subcommands);
                });
                action.addSubcommandGroups(subcommandGroupData);
            }
        } else {
            SubcommandData[] subcommands = createJDASubCommandData(subCommand);
            action.addSubcommands(subcommands);
        }
    }

    @NotNull
    private SubcommandData[] createJDASubCommandData(@NotNull Command subCommand) {
        SubcommandData[] subcommands = new SubcommandData[subCommand.getAliases().length];
        for (int i = 0; i < subCommand.getAliases().length; i++) {
            String childAlias = subCommand.getAliases()[i];
            SubcommandData subcommandData = new SubcommandData(childAlias, subCommand.getDescription());

            OptionData[] optionData = JDAOptionProcessor.process(this, subCommand);
            if (optionData != null) {
                subcommandData.addOptions(optionData);

                subcommands[i] = subcommandData;
            }
        }
        return subcommands;
    }
}
