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

import com.github.kafeindev.commands.jda.component.JDASenderComponent;
import com.github.kafeindev.commands.common.command.context.provider.ContextProvider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public final class JDAContextProvider extends ContextProvider<OptionMapping> {
    @Override
    public void initialize() {
        super.initialize();

        put(JDASenderComponent.class, (sender, args, value, parameter) -> sender);
        put(SlashCommandInteractionEvent.class, (sender, args, value, parameter) -> ((JDASenderComponent) sender).getEvent());
        put(OptionMapping.class, (sender, args, value, parameter) -> value);

        put(User.class, (sender, args, value, parameter) -> value.getAsUser());
        put(Member.class, (sender, args, value, parameter) -> value.getAsMember());
        put(Role.class, (sender, args, value, parameter) -> value.getAsRole());

        put(Channel.class, (sender, args, value, parameter) -> value.getAsChannel());
        put(GuildChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asGuildMessageChannel());

        put(MessageChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asGuildMessageChannel());
        put(GuildMessageChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asGuildMessageChannel());

        put(TextChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asTextChannel());
        put(NewsChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asNewsChannel());
        put(ThreadChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asThreadChannel());

        put(AudioChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asAudioChannel());
        put(StageChannel.class, (sender, args, value, parameter) -> value.getAsChannel().asStageChannel());

        put(String.class, (sender, args, value, parameter) -> value.getAsString());

        put(boolean.class, (sender, args, value, parameter) -> value.getAsBoolean());
        put(Boolean.class, (sender, args, value, parameter) -> value.getAsBoolean());

        put(int.class, (sender, args, value, parameter) -> value.getAsInt());
        put(Integer.class, (sender, args, value, parameter) -> value.getAsInt());

        put(long.class, (sender, args, value, parameter) -> value.getAsLong());
        put(Long.class, (sender, args, value, parameter) -> value.getAsLong());

        put(double.class, (sender, args, value, parameter) -> value.getAsDouble());
        put(Double.class, (sender, args, value, parameter) -> value.getAsDouble());
    }
}