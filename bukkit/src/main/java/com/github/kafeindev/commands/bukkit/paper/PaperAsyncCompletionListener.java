/*
 * MIT License
 *
 * Copyright (c) 2023 Kafein's CommandFramework
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

package com.github.kafeindev.commands.bukkit.paper;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.github.kafeindev.commands.bukkit.BukkitCommandManager;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class PaperAsyncCompletionListener implements Listener {
    @NotNull
    private final BukkitCommandManager manager;

    public PaperAsyncCompletionListener(@NotNull BukkitCommandManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncCompletion(AsyncTabCompleteEvent event) {
        String buffer = event.getBuffer();
        if ((!event.isCommand() && !buffer.startsWith("/")) || buffer.indexOf(' ') == -1) {
            return;
        }

        String[] split = buffer.split(" ");
        String alias = split[0].substring(1);
        this.manager.findCommand(alias).ifPresent(command -> {
            String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);

            Command bukkitCommand = this.manager.getBukkitCommand(alias);
            if (bukkitCommand != null) {
                event.setCompletions(bukkitCommand.tabComplete(event.getSender(), alias, args));
            }
        });
    }
}
