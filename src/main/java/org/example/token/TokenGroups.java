package org.example.token;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class TokenGroups {

    /*
    * Map of keywords mapping keywords to token types
    * e.g. "klasa": CLASS (polish config)
    *      "zwróć": RETURN
    */
    public static Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        EnumSet.allOf(TokenType.class)
                .stream()
                .filter(tokenType -> StringUtils.isAlpha(tokenType.getKeyword()))
                .filter(tokenType -> tokenType != TokenType.TRUE && tokenType != TokenType.FALSE)
                .forEach(tokenType -> KEYWORDS.put(tokenType.getKeyword(), tokenType));
    }

    public static final Map<String, TokenType> BOOL_LITERALS = Map.of(
            TokenType.TRUE.getKeyword(), TokenType.TRUE,
            TokenType.FALSE.getKeyword(), TokenType.FALSE);

    public static final Map<String, TokenType> SYMBOLS = new HashMap<>();
}
