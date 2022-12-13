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

package com.mineles.commands.common.command.context;

import com.mineles.commands.common.component.SenderComponent;
import com.mineles.commands.common.manager.AbstractManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.Optional;

public abstract class CommandContextProvider extends AbstractManager<Class<?>, CommandContext> {

    protected CommandContextProvider() {
        register(new CommandContext(String[].class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                return args;
            }
        });
        register(new CommandContext(String.class) {
            @Override
            public Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                return value;
            }
        });
        register(new CommandContext(SenderComponent.class) {
            @Override
            public Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                return sender;
            }
        });
        register(new CommandContext(Enum.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) parameter.getType();
                for (Enum<?> e : enumClass.getEnumConstants()) {
                    if (e.name().equalsIgnoreCase(value)) {
                        return e;
                    }
                }

                sender.sendMessage("§cInvalid value: " + value);
                return null;
            }
        });
        register(new CommandContext(char.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                return value.length() == 1 ? value.charAt(0) : null;
            }
        });
        register(new CommandContext(boolean.class) {
            @Override
            public Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                return Boolean.parseBoolean(value);
            }
        });
        register(new CommandContext(Boolean.class) {
            @Override
            public Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                return Boolean.parseBoolean(value);
            }
        });
        register(new CommandContext(byte.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Byte.parseByte(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(Byte.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Byte.parseByte(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(short.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Short.parseShort(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(Short.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Short.parseShort(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(int.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(Integer.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(long.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(Long.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(float.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(Float.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(double.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
        register(new CommandContext(Double.class) {
            @Override
            public @Nullable Object handle(@NotNull SenderComponent sender, @NotNull String[] args,
                                           @NotNull String value, @NotNull Parameter parameter) {
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + value);
                    return null;
                }
            }
        });
    }

    public abstract void initialize();

    @Override
    public Optional<CommandContext> find(@NotNull Class<?> key) {
        Optional<CommandContext> optional = super.find(key);
        if (optional.isPresent()) {
            return optional;
        }

        for (CommandContext context : findAll()) {
            if (context.getClazz().isAssignableFrom(key)) {
                return Optional.of(context);
            }
        }
        return Optional.empty();
    }

    public void register(@NotNull CommandContext context) {
        put(context.getClazz(), context);
    }
}
