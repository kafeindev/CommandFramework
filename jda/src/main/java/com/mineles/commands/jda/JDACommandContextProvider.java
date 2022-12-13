package com.mineles.commands.jda;

import com.mineles.commands.common.command.context.CommandContext;
import com.mineles.commands.common.command.context.CommandContextProvider;
import com.mineles.commands.common.component.SenderComponent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;

public final class JDACommandContextProvider extends CommandContextProvider {

    @Override
    public void initialize() {
        /*register(new CommandContext(Member.class) {
            @Override
            public Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                 @NotNull String value, @NotNull Parameter parameter) {
                Player target = Bukkit.getServer().getPlayer(value);
                if (target == null) {
                    sender.sendMessage("§cPlayer " + value + " not found.");
                    return null;
                }

                return target;
            }
        });*/
    }

}