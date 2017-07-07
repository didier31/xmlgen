grammar PI;

@header {
package org.xmlgen.parser.pi;
}

import Query;

inputPI : (captures | content | end | insert) EOF
;

insert: Insert;

Insert: [<][Ii][Nn][Ss][Ee][Rr][Tt][>];


captures : ('['? label ']'?)? capture (',' capture)*
;

capture : dataID ':' expression
;

dataID : Ident
;

content : attributeContent | elementContent
;

attributeContent : ATTRIBUTE attributeID '=' expression
;

elementContent : expression
;

attributeID : prefix? Ident
;

end: END ('['? label ']'?)? 
;

label: Ident
;

END: [Ee][nN][Dd]
;

ATTRIBUTE: [aA][tT][tT][rR]
;

prefix: Ident ':'
;
