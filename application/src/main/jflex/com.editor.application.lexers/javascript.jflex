package com.editor.application.lexers;

%%

%public
%class JavaScriptLexer
%extends DefaultJFlexLexer
%final
%unicode
%char
%type Token

%{
    /**
     * Create an empty lexer, yyreset will be called later to reset and assign
     * the reader
     */
    public JavaScriptLexer() {
        super();
    }

    @Override
    public int yychar() {
        return (int) yychar;
    }

    private static final byte PARAN     = 1;
    private static final byte BRACKET   = 2;
    private static final byte CURLY     = 3;
    
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]+

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} 

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
StringCharacter  = [^\r\n\"\\]
SStringCharacter = [^\r\n\'\\]

%state STRING SSTRING

%%

<YYINITIAL> {

  /* keywords */
  "break"                        |
  "case"                         |
  "catch"                        |
  "continue"                     |
  "do"                           |
  "else"                         |
  "finally"                      |
  "for"                          |
  "default"                      |
  "delete"                       |
  "new"                          |
  "goto"                         |
  "if"                           |
  "switch"                       |
  "return"                       |
  "while"                        |
  "this"                         |
  "try"                          |
  "var"                          |
  "let"                          |
  "const"                        |
  "function"                     |
  "with"                         |
  "in"                           |
  
  /* boolean literals */
  "true"                         |
  "false"                        |
  
  /* null literal */
  "null"                         { return token(TokenType.KEYWORD); }

  /* standard / builtin functions */
  "Infinity"                     |
  "NaN"                          |
  "undefined"                    |
  "decodeURI"                    |
  "encodeURIComponent"           |
  "escape"                       |
  "eval"                         |
  "isFinite"                     |
  "isNaN"                        |
  "parseFloat"                   |
  "parseInt"                     |
  "unescape"                     { return token(TokenType.KEYWORD2); }

  /* Built-in Types*/
  "Array"                        |
  "Boolean"                      |
  "Date"                         |
  "Math"                         |
  "Number"                       |
  "Object"                       |
  "RegExp"                       |
  "String"                       |
  {Identifier} ":"               { return token(TokenType.TYPE); }

  /* delimiters */

  "("                            { return token(TokenType.DELIMITER,  PARAN); }
  ")"                            { return token(TokenType.DELIMITER, -PARAN); }
  "{"                            { return token(TokenType.DELIMITER,  CURLY); }
  "}"                            { return token(TokenType.DELIMITER, -CURLY); }
  "["                            { return token(TokenType.DELIMITER,  BRACKET); }
  "]"                            { return token(TokenType.DELIMITER, -BRACKET); }

  /* operators */
  ";"                            |
  ","                            | 
  "."                            | 
  "="                            | 
  ">"                            | 
  "<"                            |
  "!"                            | 
  "~"                            | 
  "?"                            | 
  ":"                            | 
  "=="                           | 
  "<="                           | 
  ">="                           | 
  "!="                           | 
  "&&"                           | 
  "||"                           | 
  "++"                           | 
  "--"                           | 
  "+"                            | 
  "-"                            | 
  "*"                            | 
  "/"                            | 
  "&"                            | 
  "|"                            | 
  "^"                            | 
  "%"                            | 
  "<<"                           | 
  ">>"                           | 
  ">>>"                          | 
  "+="                           | 
  "-="                           | 
  "*="                           | 
  "/="                           | 
  "&="                           | 
  "|="                           | 
  "^="                           | 
  "%="                           | 
  "<<="                          | 
  ">>="                          | 
  ">>>="                         { return token(TokenType.OPERATOR); } 
  
  /* string literal */
  \"                             {  
                                    yybegin(STRING); 
                                    tokenStart = yychar; 
                                    tokenLength = 1; 
                                 }

  \'                             {
                                    yybegin(SSTRING);
                                    tokenStart = yychar;
                                    tokenLength = 1;
                                 }

  /* numeric literals */

  {DecIntegerLiteral}            |
  {DecLongLiteral}               |
  
  {HexIntegerLiteral}            |
  {HexLongLiteral}               |
 
  {OctIntegerLiteral}            |
  {OctLongLiteral}               |
  
  {FloatLiteral}                 |
  {DoubleLiteral}                |
  {DoubleLiteral}[dD]            { return token(TokenType.NUMBER); }
  
  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }

  /* whitespace */
  {WhiteSpace}                   { }

  /* identifiers */ 
  {Identifier}                   { return token(TokenType.IDENTIFIER); }
}

<STRING> {
  \"                             { 
                                     yybegin(YYINITIAL); 
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }
  
  {StringCharacter}+             { tokenLength += yylength(); }

  \\[0-3]?{OctDigit}?{OctDigit}  { tokenLength += yylength(); }
  
  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

<SSTRING> {
  \'                             {
                                     yybegin(YYINITIAL);
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }

  {SStringCharacter}+            { tokenLength += yylength(); }

  \\[0-3]?{OctDigit}?{OctDigit}  { tokenLength += yylength(); }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

/* error fallback */
.|\n                             {  }
<<EOF>>                          { return null; }
