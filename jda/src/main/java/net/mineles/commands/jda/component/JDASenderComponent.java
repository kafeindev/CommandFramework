package net.mineles.commands.jda.component;

import net.mineles.commands.common.component.SenderComponent;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public final class JDASenderComponent implements SenderComponent {

    private final Member member;
    private final InteractionHook reply;

    public JDASenderComponent(@NotNull Member member, @NotNull InteractionHook reply) {
        this.member = member;
        this.reply = reply;
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return this.member.getUser().getName();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sendMessage(message, true);
    }

    public void sendMessage(@NotNull String message, boolean ephemeral) {
        if (ephemeral) {
            reply.setEphemeral(true).editOriginal(message).queue();
        } else {
            TextChannel channel = this.reply.getInteraction().getTextChannel();
            Message jdaMessage = channel.sendMessage(message).complete();

            jdaMessage.delete().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        Permission jdaPermission = Permission.valueOf(permission.toUpperCase(Locale.ROOT));

        return this.member.hasPermission(jdaPermission);
    }

    @Override
    public boolean isOnline() {
        return this.member.getOnlineStatus() == OnlineStatus.ONLINE;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @NotNull
    public InteractionHook getReply() {
        return this.reply;
    }

    @NotNull
    public Member getMember() {
        return this.member;
    }
}
