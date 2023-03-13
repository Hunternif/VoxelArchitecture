grammar StyleGrammar;

// Rules
stylesheet : (styleRule | comment | NEWLINE)* EOF ;

styleRule : selector LBRACE NEWLINE* ruleBody RBRACE ;

ruleBody : (declaration | comment)* ;

declaration : property=ID COLON value=propValue SEMICOLON? comment? NEWLINE* ;

propValue : numExpression # numValue
          | INHERIT       # inheritValue
          | ID            # enumValue
          | STR           # strValue ;


// Selectors
selector : left=selector right=selector                            # andSelector
         | parent=selector GT child=selector                       # childSelector
         | LBRACKET ancestor=selector RBRACKET descendant=selector # descendantSelector
         | DOT classname=ID                                        # classSelector
         | ID                                                      # typeSelector
         | left=selector COMMA right=selector                      # orSelector
         | MULT                                                    # anySelector ;


// Arithmetic expressions with numbers
numExpression : left=numExpression op=(DIV|MULT) right=numExpression   # numBinaryOperation
              | left=numExpression op=(PLUS|MINUS) right=numExpression # numBinaryOperation
              | left=numExpression op=TILDE right=numExpression        # numBinaryOperation
              | LPAREN numExpression RPAREN                            # numParenExpression
              | MINUS numExpression                                    # numMinusExpression
              | INT                                                    # intLiteral
              | INT_PCT                                                # intPctLiteral
              | FLOAT                                                  # floatLiteral
              | FLOAT_PCT                                              # floatPctLiteral ;

comment : LINE_COMMENT | BLOCK_COMMENT ;


// Whitespace
NEWLINE             : ('\r\n' | '\r' | '\n') -> skip ;
WS                  : [\t ]+ -> skip ;

// Keywords
INHERIT             : 'inherit' ;

// Literals
STR                 : (SINGLEQUOTE (~['])*? (SINGLEQUOTE | NEWLINE | EOF))
                    | (DOUBLEQUOTE (~["])*? (DOUBLEQUOTE | NEWLINE | EOF)) ;
INT                 : '0'|[1-9][0-9]* ;
FLOAT               : ('0'|[1-9][0-9]*) '.' [0-9]+ ;
INT_PCT             : '0%'|[1-9][0-9]* '%' ;
FLOAT_PCT           : ('0'|[1-9][0-9]*) '.' [0-9]+ '%' ;

// Operators
PLUS                : '+' ;
MINUS               : '-' ;
MULT                : '*' ;
DIV                 : '/' ;
LPAREN              : '(' ;
RPAREN              : ')' ;
LBRACKET            : '[' ;
RBRACKET            : ']' ;
LBRACE              : '{' ;
RBRACE              : '}' ;
LT                  : '<' ;
GT                  : '>' ;
TILDE               : '~' ;
COLON               : ':' ;
SEMICOLON           : ';' ;
DOT                 : '.' ;
COMMA               : ',' ;
SINGLEQUOTE         : '\'' ;
DOUBLEQUOTE         : '"' ;

// Identifiers
ID                  : [A-Za-z_][A-Za-z0-9_-]* ;

// Comments
LINE_COMMENT        : ('//'|'#') ~( '\r' | '\n' )* ;
BLOCK_COMMENT       : '/*' .*? '*/' ;

// Invalid Identifier
INVALID             : . ;
