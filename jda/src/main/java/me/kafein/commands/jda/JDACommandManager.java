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

import me.kafein.commands.common.command.CommandManager;
import me.kafein.commands.common.command.abstraction.ChildCommand;
import me.kafein.commands.common.command.abstraction.ParentCommand;
import me.kafein.commands.jda.misc.JDAOptionProcessor;
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
    public void initializeRegisteredCommand(@NotNull ParentCommand command) {
        for (String alias : command.getAliases()) {
            CommandCreateAction commandCreateAction = this.jda.upsertCommand(alias, command.getDescription());

            if (!command.hasChild()) {
                Method executor = command.getExecutor();

                OptionData[] optionData = JDAOptionProcessor.process(executor);
                commandCreateAction.addOptions(optionData);
            } else {
                for (ChildCommand childCommand : command.findAllChild()) {
                    Method method = childCommand.getExecutor();

                    SubcommandData[] subcommands = new SubcommandData[childCommand.getAliases().length];
                    for (int i = 0; i < childCommand.getAliases().length; i++) {
                        String childAlias = childCommand.getAliases()[i];
                        SubcommandData subcommandData = new SubcommandData(childAlias, childCommand.getDescription());

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
