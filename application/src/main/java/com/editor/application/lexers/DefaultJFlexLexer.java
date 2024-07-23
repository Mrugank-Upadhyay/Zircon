package com.editor.application.lexers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public abstract class DefaultJFlexLexer implements Lexer {

    protected long tokenStart;
    protected int tokenLength;
    protected int offset;

    /**
     * Helper method to create and return a new Token from of TokenType
     * tokenStart and tokenLength will be modified to the newStart and
     * newLength params
     */
    protected Token token(TokenType type, long tStart, int tLength,
                          int newStart, int newLength) {
        tokenStart = newStart;
        tokenLength = newLength;
        return new Token(type, (int) tStart + offset, tLength);
    }

    /**
     * Create and return a Token of given type from start with length
     * offset is added to start
     */
    protected Token token(TokenType type, long start, int length) {
        return new Token(type, (int) start + offset, length);
    }

    /**
     * Create and return a Token of given type.  start is obtained from {@link DefaultJFlexLexer#yychar()}
     * and length from {@link DefaultJFlexLexer#yylength()}
     * offset is added to start
     */
    protected Token token(TokenType type) {
        return new Token(type, yychar() + offset, yylength());
    }

    /**
     * Create and return a Token of given type and pairValue.
     * start is obtained from {@link DefaultJFlexLexer#yychar()}
     * and length from {@link DefaultJFlexLexer#yylength()}
     * offset is added to start
     */
    protected Token token(TokenType type, int pairValue) {
        return new Token(type, yychar() + offset, yylength(), (byte) pairValue);
    }
    public void parse(String text, int offset, List<Token> tokens) {
        this.offset = offset;
        try {
            Reader reader = new StringReader(text);
            yyreset(reader);
            var t = yylex();
            while (t != null) {
                tokens.add(t);
                t = yylex();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Failed to parse.");
        }
    }


    /**
     * This will be called to reset the the lexer.
     * This is created automatically by JFlex.
     */
    public abstract void yyreset(Reader reader);

    /**
     * This is called to return the next Token from the Input Reader
     * @return next token, or null if no more tokens.
     * @throws java.io.IOException
     */
    public abstract Token yylex() throws java.io.IOException;

    /**
     * Returns the character at position <tt>pos</tt> from the
     * matched text.
     *
     * It is equivalent to yytext().charAt(pos), but faster
     *
     * @param pos the position of the character to fetch.
     *            A value from 0 to yylength()-1.
     *
     * @return the character at position pos
     */
    public abstract char yycharat(int pos);

    /**
     * Returns the length of the matched text region.
     * This method is automatically implemented by JFlex lexers
     */
    public abstract int yylength();

    /**
     * Returns the text matched by the current regular expression.
     * This method is automatically implemented by JFlex lexers
     */
    public abstract String yytext();

    /**
     * Return the char number from beginning of input stream.
     * This is NOT implemented by JFlex, so the code must be
     * added to create this and return the private yychar field
     */
    public abstract int yychar();
}
