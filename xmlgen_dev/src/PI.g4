grammar PI;

@header {
package org.xmlgen.parser.pi;
}

import Query;

inputPI : (structuralInstruction | content | insert) EOF
;

insert: Insert;

structuralInstruction: (label (captures | begin)) | end
;

captures : capture (',' capture)*
;

begin: Begin
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

end: END label
;

label: ('['? Ident ']'?)?
;

END: [Ee][nN][Dd]
;

ATTRIBUTE: [aA][tT][tT][rR]
;

prefix: Ident ':'
;

Insert: [<][Ii][Nn][Ss][Ee][Rr][Tt][>];

Begin: [Bb][Ee][Gg][Ii][Nn]
;