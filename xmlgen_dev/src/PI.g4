grammar PI;

@header {
package org.xmlgen.parser.pi;
}

import Query;

inputPI : (tagged | content | insert | userService | expand) EOF
;

expand: '<' Expand '>'
;

userService: '<' Load dottedIdent '>'
;

dottedIdent
:
	Ident
	(
		'.' Ident
	)*
;

tagged: label? (captures | begin | end)
;

insert: '<' Insert Label? '>'
;

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

end: End exports?
;

exports: Export ':' Ident (',' Ident)*
;

label: Label
;

Label: '[' Ident ']'
;

End: [Ee][nN][Dd]
;

Expand: [E][Xx][Pp][Aa][Nn][Dd]
;

Export: [Ee][Xx][Pp][Oo][Rr][Tt]
;

When: [Ww][Hh][Ee][Nn]
;

ATTRIBUTE: [aA][tT][tT][rR]
;

prefix: Ident ':'
;

Load: [Ll][Oo][Aa][Dd]
;

Insert: [Ii][Nn][Ss][Ee][Rr][Tt]
;

Begin: [Bb][Ee][Gg][Ii][Nn]
;