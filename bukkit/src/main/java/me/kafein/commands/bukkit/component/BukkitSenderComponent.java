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

package me.kafein.commands.bukkit.component;

import me.kafein.commands.common.component.SenderComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitSenderComponent implements SenderComponent {

    private UUID uuid;

    private final boolean isPlayer;

    public BukkitSenderComponent(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            this.uuid = ((Player) sender).getUniqueId();
            this.isPlayer = true;
        } else {
            this.isPlayer = false;
        }
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return this.isPlayer ? this.uuid : null;
    }

    @Override
    public @NotNull String getName() {
        return isPlayer(() -> {
            Player player = Bukkit.getPlayer(this.uuid);
            return player != null ? player.getName() : "Unknown";
        }, () -> "Console");
    }

    @Override
    public void sendMessage(@NotNull String message) {
        isPlayer(() -> {
            @Nullable Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }, () -> Bukkit.getConsoleSender().sendMessage(message));
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return isPlayer(() -> {
            @Nullable Player player = Bukkit.getPlayer(this.uuid);
            return player != null && player.hasPermission(permission);
        }, () -> true);
    }

    @Override
    public boolean isOnline() {
        return isPlayer(() -> {
            @Nullable Player player = Bukkit.getPlayer(this.uuid);
            return player != null && player.isOnline();
        }, () -> true);
    }

    @Override
    public boolean isPlayer() {
        return this.isPlayer;
    }

    @Override
    public boolean isConsole() {
        return !this.isPlayer;
    }
}
