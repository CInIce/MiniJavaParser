grammar MiniJava;

goal:
mainClass ( classDeclaration )* EOF;

mainClass:
'class' name = identifier '{' 'public' 'static' 'void' 'main' '(' 'String' '[' ']' identifier ')' '{' statement '}' '}';

classDeclaration:
'class' name = identifier ( 'extends' parent = identifier )? '{' ( varDeclaration )* ( methodDeclaration )* '}';

varDeclaration:
type identifier ';';

methodDeclaration:
'public' type identifier '(' argumentList ')' '{' ( varDeclaration )* ( statement )* 'return' expression ';' '}';

argumentList:
    ( type identifier ( ',' type identifier )* )? ;

type:
intArrayType
|booleanType
|intType
|identifier;

intArrayType: ('int' '[' ']');

booleanType: 'boolean';

intType: 'int';

statement :('{' ( block = statement )* '}')
| ('if'     '(' ifExp = expression ')' ifStmt = statement 'else' elseStmt = statement)
| ('while' '(' whileExp = expression ')' whileStmt = statement)
| ('System.out.println' '(' sysoExp = expression ')' ';')
| (assign = identifier '=' assignExp = expression ';')
| (assignArray = identifier '[' indexExp = expression ']' '=' valueExp = expression ';');

expression:
opExp1 = expression op = ( '&&' | '<' | '+' | '-' | '*' ) opExp2 = expression
|arrayExp = expression '[' indexExp = expression ']'
|sizeExp = expression '.' 'length'
|classCallExp = expression '.' methodName = identifier '(' ( expression ( ',' expression )* )? ')'
|INT
|BOOLEAN
| expId = identifier
| thisExp = 'this'
|'new' 'int' '[' newArray = expression ']'
|'new' newId = identifier '(' ')'
|'!' notExp = expression
|'(' primaryExp = expression ')'
 ;


identifier: IDENTIFIER;

INT:'0'| ('1'..'9')('0'..'9')*;
BOOLEAN:'true'|'false';
IDENTIFIER:('_'|('a'..'z')|('A'..'Z'))('_'|('a'..'z')|('A'..'Z')|('0'..'9'))*;
 


WS: (' ' | '\n'| '\r' | '\t')+ -> skip;
SLCOMMENT: '//'~('\n'|'\r')+ -> skip;
MLCOMMENT: '/*' .*? '*/' -> skip;