lexer grammar StyleLexer;

// Whitespace
NEWLINE            : '\r\n' | 'r' | '\n' ;
WS                 : [\t ]+ -> skip ;

// Keywords
INHERIT            : 'inherit' ;

// Literals
STR                : '"' .*? '"' ;
INT                : '0'|[1-9][0-9]* ;
FLOAT              : '0'|[1-9][0-9]* '.' [0-9]+ ;
INT_PCT            : '0%'|[1-9][0-9]* '%' ;
FLOAT_PCT          : '0%'|[1-9][0-9]* '.' [0-9]+ '%' ;

// Operators
PLUS               : '+' ;
MINUS              : '-' ;
ASTERISK           : '*' ;
DIVISION           : '/' ;
LPAREN             : '(' ;
RPAREN             : ')' ;
LBRACKET           : '[' ;
RBRACKET           : ']' ;
LBRACE             : '{' ;
RBRACE             : '}' ;
LT                 : '<' ;
GT                 : '>' ;
TILDE              : '~' ;
COLON              : ':' ;
SEMICOLON          : ';' ;

// Identifiers
CLASSNAME          : [A-Za-z0-9_-]+ ;
TYPE               : [A-Z][A-Za-z0-9]+ ;
PROPERTY           : [a-z][A-Za-z0-9_-]* ;