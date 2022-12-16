package net.mineles.commands.jda;

import net.mineles.commands.common.command.context.CommandContextProvider;
import net.mineles.commands.jda.component.JDASenderComponent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public final class JDACommandContextProvider extends CommandContextProvider<OptionMapping> {

    @Override
    public void initialize() {
        put(JDASenderComponent.class, (sender, args, value, parameter) -> sender);
        put(OptionMapping.class, (sender, args, value, parameter) -> value);

        put(User.class, (sender, args, value, parameter) -> value.getAsUser());
        put(Member.class, (sender, args, value, parameter) -> value.getAsMember());
        put(Role.class, (sender, args, value, parameter) -> value.getAsRole());

        put(Channel.class, (sender, args, value, parameter) -> value.getAsGuildChannel());
        put(GuildChannel.class, (sender, args, value, parameter) -> value.getAsGuildChannel());

        put(MessageChannel.class, (sender, args, value, parameter) -> value.getAsMessageChannel());
        put(GuildMessageChannel.class, (sender, args, value, parameter) -> value.getAsMessageChannel());

        put(TextChannel.class, (sender, args, value, parameter) -> value.getAsTextChannel());
        put(NewsChannel.class, (sender, args, value, parameter) -> value.getAsNewsChannel());
        put(ThreadChannel.class, (sender, args, value, parameter) -> value.getAsThreadChannel());

        put(AudioChannel.class, (sender, args, value, parameter) -> value.getAsAudioChannel());
        put(VoiceChannel.class, (sender, args, value, parameter) -> value.getAsVoiceChannel());
        put(StageChannel.class, (sender, args, value, parameter) -> value.getAsStageChannel());

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