/*
 * MIT License
 *
 * Copyright (c) 2022 FarmerPlus
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

package net.mineles.commands.bukkit;

import net.mineles.commands.common.command.context.CommandContextProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class BukkitCommandContextProvider extends CommandContextProvider<String> {

    @Override
    public void initialize() {
        put(Player.class, (sender, args, value, parameter) -> {
            Player target = Bukkit.getServer().getPlayer(value);
            if (target == null) {
                sender.sendMessage("§cPlayer " + value + " not found.");
                return null;
            }

            return target;
        });

        put(Enum.class, (sender, args, value, parameter) -> {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) parameter.getType();
            for (Enum<?> e : enumClass.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(value)) {
                    return e;
                }
            }

            sender.sendMessage("§cInvalid value: " + value);
            return null;
        });

        put(char.class, (sender, args, value, parameter) -> value.length() == 1 ? value.charAt(0) : null);

        put(boolean.class, (sender, args, value, parameter) -> Boolean.parseBoolean(value));
        put(Boolean.class, (sender, args, value, parameter) -> Boolean.parseBoolean(value));

        put(byte.class, (sender, args, value, parameter) -> {
            try {
                return Byte.parseByte(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
        put(Byte.class, (sender, args, value, parameter) -> {
            try {
                return Byte.parseByte(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });

        put(short.class, (sender, args, value, parameter) -> {
            try {
                return Short.parseShort(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
        put(Short.class, (sender, args, value, parameter) -> {
            try {
                return Short.parseShort(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });

        put(int.class, (sender, args, value, parameter) -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
        put(Integer.class, (sender, args, value, parameter) -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });

        put(long.class, (sender, args, value, parameter) -> {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
        put(Long.class, (sender, args, value, parameter) -> {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });

        put(float.class, (sender, args, value, parameter) -> {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
        put(Float.class, (sender, args, value, parameter) -> {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });

        put(double.class, (sender, args, value, parameter) -> {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
        put(Double.class, (sender, args, value, parameter) -> {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + value);
                return null;
            }
        });
    }

}
