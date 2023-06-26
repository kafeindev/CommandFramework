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

package com.github.kafeindev.commands.common.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandAttribute {
    @NotNull
    public static CommandAttribute of(@NotNull String[] aliases, @Nullable String usage, @Nullable String description,
                                      @Nullable String permission, @NotNull String permissionMessage) {
        return new CommandAttribute(aliases, usage, description, permission, permissionMessage);
    }

    @NotNull
    private final String[] aliases;

    @NotNull
    private final String description;

    @Nullable
    private final String permission;

    @NotNull
    private final String permissionMessage;

    private String usage;
    private boolean usingDefaultUsage;

    private CommandAttribute(@NotNull String[] aliases, @Nullable String usage, @Nullable String description,
                             @Nullable String permission, @NotNull String permissionMessage) {
        this.aliases = aliases;
        this.permission = permission;
        this.permissionMessage = permissionMessage;

        this.usingDefaultUsage = usage == null;
        this.usage = usage == null ? "/" + aliases[0] : usage;
        this.description = description == null ? "No description provided" : description;
    }

    @NotNull
    public String[] getAliases() {
        return this.aliases;
    }

    @NotNull
    public String getDescription() {
        return this.description;
    }

    @NotNull
    public String getUsage() {
        return this.usage;
    }

    public boolean isUsingDefaultUsage() {
        return this.usingDefaultUsage;
    }

    public void setUsage(@NotNull String usage) {
        this.usage = usage;
        this.usingDefaultUsage = false;
    }

    @Nullable
    public String getPermission() {
        return this.permission;
    }

    @NotNull
    public String getPermissionMessage() {
        return this.permissionMessage;
    }
}
