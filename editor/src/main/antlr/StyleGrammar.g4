grammar StyleGrammar;

// Rules
stylesheet : (styleRule NEWLINE*)+ EOF ;

styleRule : selector LBRACE NEWLINE* ruleBody RBRACE ;

ruleBody : declaration+ ;

declaration : property=ID COLON value=propValue SEMICOLON? NEWLINE* ;

propValue : dimExpression # dimValue
          | numExpression # numValue
          | ID            # enumValue
          | STR           # strValue ;


// Selectors
selector : selector COMMA selector             # listSelector
         | selector dotClass                   # andSelector
         | selector GT selector                # childSelector
         | LBRACKET selector RBRACKET selector # descendantSelector
         | dotClass                            # classSelector
         | ID                                  # typeSelector ;

dotClass : DOT ID ;


// Arithmetic expressions with numbers
numExpression : left=numExpression op=(DIV|MULT) right=numExpression   # numBinaryOperation
              | left=numExpression op=(PLUS|MINUS) right=numExpression # numBinaryOperation
              | LPAREN numExpression RPAREN                            # numParenExpression
              | MINUS numExpression                                    # numMinusExpression
              | INT                                                    # intLiteral
              | FLOAT                                                  # floatLiteral ;

// Arithmetic expressions with dimensions (which can use %)
dimExpression : left=dimExpression op=(DIV|MULT) right=numExpression   # dimMultOperation
              | left=dimExpression op=(PLUS|MINUS) right=dimExpression # dimAddOperation
              | numExpression                                          # numberAsDim
              | INT_PCT                                                # intPctLiteral
              | FLOAT_PCT                                              # floatPctLiteral ;


// Whitespace
NEWLINE             : '\r\n' | '\r' | '\n' ;
WS                  : [\t ]+ -> skip ;

// Keywords
INHERIT             : 'inherit' ;

// Literals
STR                 : '"' (~[\r\n])*? '"' ;
INT                 : '0'|[1-9][0-9]* ;
FLOAT               : '0'|[1-9][0-9]* '.' [0-9]+ ;
INT_PCT             : '0%'|[1-9][0-9]* '%' ;
FLOAT_PCT           : '0%'|[1-9][0-9]* '.' [0-9]+ '%' ;

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
QUOTE               : '"' ;

// Identifiers
ID                  : [A-Za-z0-9_-]+ ;

// Comments
COMMENT             : '//' .*? (NEWLINE | EOF) ;
BLOCK_COMMENT       : '/*' .*? '*/' ;