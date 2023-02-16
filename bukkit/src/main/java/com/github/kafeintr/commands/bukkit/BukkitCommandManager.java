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

package com.github.kafeintr.commands.bukkit;

import com.github.kafeintr.commands.bukkit.paper.PaperAsyncCompletionListener;
import com.github.kafeintr.commands.common.command.Command;
import com.github.kafeintr.commands.common.command.CommandManager;
import com.github.kafeintr.commands.common.command.context.resolver.DefaultContextResolver;
import com.github.kafeintr.commands.common.command.convert.DefaultCommandConverter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public final class BukkitCommandManager extends CommandManager<String> {
    @NotNull
    private final Plugin plugin;

    @NotNull
    private final CommandMap commandMap;

    public BukkitCommandManager(@NotNull Plugin plugin) {
        super(new DefaultCommandConverter(), new BukkitCompletionProvider(),
                new DefaultContextResolver<>(new BukkitContextProvider()));
        this.plugin = plugin;
        this.commandMap = createCommandMap();

        try {
            Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");

            PluginManager pluginManager = plugin.getServer().getPluginManager();
            pluginManager.registerEvents(new PaperAsyncCompletionListener(this), plugin);
        } catch (ClassNotFoundException ignored) {
        }
    }

    @Override
    public void initializeRegisteredCommand(@NotNull Command command) {
        BukkitCommandExecutor executor = new BukkitCommandExecutor(this, command);

        String fallBackPrefix = this.plugin.getName().toLowerCase(Locale.ROOT);
        this.commandMap.register(fallBackPrefix, executor);
    }

    @NotNull
    private CommandMap createCommandMap() {
        Server server = Bukkit.getServer();

        try {
            Method commandMapMethod = server.getClass().getDeclaredMethod("getCommandMap");
            commandMapMethod.setAccessible(true);

            return (CommandMap) commandMapMethod.invoke(server);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get command map from bukkit server.", e);
        }
    }

    @Nullable
    public org.bukkit.command.Command getBukkitCommand(@NotNull String name) {
        return this.commandMap.getCommand(name);
    }
}
