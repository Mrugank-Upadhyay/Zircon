package com.editor.application.lexers

enum class TokenType {
    OPERATOR,   // Language operators
    DELIMITER,  // Delimiters.  Constructs that are not necessarily operators for a language
    KEYWORD,    // language reserved keywords
    KEYWORD2,   // Other language reserved keywords, like C #defines
    IDENTIFIER, // identifiers, variable names, class names
    NUMBER,     // numbers in various formats
    STRING,     // String
    STRING2,    // For highlighting meta chars within a String
    COMMENT,    // comments
    COMMENT2,   // special stuff within comments
    REGEX,      // regular expressions
    REGEX2,     // special chars within regular expressions
    TYPE,       // Types, usually not keywords, but supported by the language
    TYPE2,      // Types from standard libraries
    TYPE3,      // Types for users
    DEFAULT,    // any other text
    WARNING,    // Text that should be highlighted as a warning
    ERROR,      // Text that signals an error
}