package com.supaham.powerjuice.language;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.language.Theme.THEME_ESCAPE_CHAR;
import static com.supaham.powerjuice.language.Theme.THEME_MARKER;

/**
 * Represents a {@link Message} manager.
 */
public class MessageManager {

    @Getter
    private Map<String, Message> messages = new HashMap<>();

    private final Map<Character, Theme> themes;

    public MessageManager() {
        this(null);
    }

    public MessageManager(Map<Character, Theme> themes) {
        this.themes = themes == null ? new HashMap<Character, Theme>() : themes;
    }

    public String parseMessage(@NotNull final String message) {
        final StringBuilder buffer = new StringBuilder(message.length() + 10);
        char previousChar = ' ';
        for (int i = 0; i < message.length() - 1; i++) {
            final char currentChar = message.charAt(i);
            if (currentChar == THEME_MARKER && previousChar != THEME_ESCAPE_CHAR) {
                Theme theme = themes.get(message.charAt(i + 1));
                if (theme != null) {
                    buffer.append(theme.getTheme());
                    i++;
                } else {
                    buffer.append(currentChar);
                }
            } else {
                buffer.append(currentChar);
            }
            previousChar = currentChar;
        }
        if (!message.isEmpty()) {
            buffer.append(message.charAt(message.length() - 1));
        }
        return buffer.toString();
    }

    /**
     * Gets a {@link Message} by node.
     *
     * @param node node of the {@link Message} to get
     * @return the {@link Message} object
     */
    public Message getMessage(@NotNull String node) {
        return messages.get(node);
    }

    /**
     * Adds a {@link Message} to this {@link MessageManager}.
     *
     * @param message message to add
     */
    public void addMessage(@NotNull Message message) {
        this.messages.put(message.getNode(), message);
    }

    /**
     * Gets a {@link Theme} by {@link Character}.
     *
     * @param character character of the theme to get
     * @return the {@link Theme} belonging to the {@code character}
     */
    public Theme getTheme(@NotNull Character character) {
        return this.themes.get(character);
    }

    /**
     * Adds a {@link Theme} to this {@link MessageManager}.
     *
     * @param theme theme to add
     * @return old theme that was replaced
     */
    public Theme addTheme(@NotNull Theme theme) {
        return this.themes.put(theme.getCode(), theme);
    }
}
