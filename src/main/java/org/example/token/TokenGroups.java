package org.example.token;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class TokenGroups {

    /*
    * Map of keywords mapping keywords to token types
    * e.g. "nowy": NEW_MASCULINE (polish config)
    */
    public static final Map<String, TokenType> KEYWORDS = EnumSet.allOf(TokenType.class)
            .stream()
            .filter(tokenType -> StringUtils.isAlpha(tokenType.getKeyword()))
            .filter(tokenType -> tokenType != TokenType.BOOL_TRUE && tokenType != TokenType.BOOL_FALSE)
            .collect(Collectors.toMap(TokenType::getKeyword, tokenType -> tokenType));

    public static final Map<String, TokenType> BOOL_LITERALS = Map.of(
            TokenType.BOOL_TRUE.getKeyword(), TokenType.BOOL,
            TokenType.BOOL_FALSE.getKeyword(), TokenType.BOOL);
}
