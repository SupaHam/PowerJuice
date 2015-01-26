package com.supaham.powerjuice.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

/**
 * Represents a message ({@link String}) represented by a node ({@link String}) that supports multiple {@link Locale}s.
 */
public class Message {

    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private final MessageManager manager;
    @Getter
    private final String node;
    private final Map<Locale, String> messages = new HashMap<>();

    public Message(@NonNull MessageManager manager, @NonNull String node, @NonNull String message) {
        this.manager = manager;
        this.node = node;
        this.messages.put(DEFAULT_LOCALE, message);
    }

    /**
     * Checks whether a {@link Locale} is supported by this {@link Message}.
     *
     * @param locale local to check.
     * @return whether the {@code locale} is supported.
     */
    public boolean supports(@NonNull Locale locale) {
        return this.messages.containsKey(locale);
    }

    /**
     * Gets the message from the default locale ({@link Locale#getDefault()}).
     *
     * @return the message, not null.
     */
    public String getMessage() {
        return this.messages.get(DEFAULT_LOCALE);
    }

    /**
     * Gets the message belonging to a {@link Locale}.
     *
     * @param locale locale to get message for.
     * @return message belonging to {@code locale}, nullable.
     */
    public String getMessage(@NonNull Locale locale) {
        return this.messages.get(locale);
    }

    /**
     * Adds a new {@link Locale} to this {@link Message}.
     *
     * @param locale  locale to add.
     * @param message message in the {@code locale}.
     * @return the old message belonging to the {@code locale}.
     */
    public String addLocale(@NonNull Locale locale, @NonNull String message) {
        return this.messages.put(locale, message);
    }

    /**
     * Sends the {@link #getMessage()} to a {@link CommandSender}.
     *
     * @param sender command sender to send this {@link Message} to.
     * @param args   the arguments to pass to this message.
     */
    public void send(@NonNull CommandSender sender, Object... args) {
        sender.sendMessage(getParsedMessage(args));
    }

    /**
     * Sends a message belonging to a {@link Locale} to a {@link CommandSender}.
     * @param sender sender to send this {@link Message} to.
     * @param locale locale to get message for.
     * @param args the arguments to pass to this message.
     */
    public void send(@NonNull CommandSender sender, Locale locale, Object... args) {
        sender.sendMessage(getParsedMessage(locale, args));
    }

    public String getParsedMessage(Object... args) {
        return getParsedMessage(null, args);
    }

    public String getParsedMessage(Locale locale, Object... args) {
        if (locale == null) locale = DEFAULT_LOCALE;
        String message = getMessage(locale);
        Validate.notNull(message, "Could not find message for locale " + locale.toLanguageTag());
        return _getParsedMessage(message, args);
    }

    protected String _getParsedMessage(@NonNull String message, Object... args) {
        return manager.parseMessage(String.format(message, args));
    }
}
