grammar PI;

@header {
package org.xmlgen.parser.pi;
}

import Query;

inputPI : (tagged | content | insert) EOF
;

tagged: label? (captures | begin | end)
;

insert: Insert Label?;

captures : capture (',' capture)*
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

begin: Begin  guard? definitions?
;

guard: When expression
;

definitions: definition (',' definition)*
;

definition: dataID '=' expression
;

end: End
;

label: Label
;

Label: '[' Ident ']'
;

End: [Ee][nN][Dd]
;

When: [Ww][Hh][Ee][Nn]
;

ATTRIBUTE: [aA][tT][tT][rR]
;

prefix: Ident ':'
;

Insert: [<][Ii][Nn][Ss][Ee][Rr][Tt][>];

Begin: [Bb][Ee][Gg][Ii][Nn]
;