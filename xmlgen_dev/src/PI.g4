grammar PI;

@header {
package org.xmlgen.parser.pi;
}

import Query;

inputPI : (tagged | content | insert | userService | expand | templateDef) EOF
;


templateDef: Ident Def (parameter (',' parameter)*)?
;

Def: [Dd][Ee][Ff];


parameter: Ident ':' typeLiteral
;

expand: '<' Expand '>'
;

userService: '<' Load loadArgument'>'
;

loadArgument: dottedIdent | filename
;

dottedIdent: DottedIdent
;

filename: Filename
;

tagged: captures | begin | end
;

insert: '<' Insert (Label | templateCall) '>'
;

templateCall: Ident (effectiveParameter (',' effectiveParameter)*)?
;

effectiveParameter: (Ident '=')? expression 
;

captures : label=Label? capture (',' capture)*
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

begin: label=Label? Begin guard? definitions?
;

Store: [Ss][Tt][Oo][Rr][Ee]
;

guard: When expression
;

definitions: definition (',' definition)*
;

definition: dataID '=' expression
;

end: (id=Ident? | label=Label?) End exports?
;

exports: Export ':' Ident (',' Ident)*
;

Label: '[' .*? ']'
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

Filename: '##' 
;

fragment
ESC
:
	'\\"'
	| '\\\\'
;

DottedIdent
:
	Ident
	(
		'.' Ident
	)*
;

Insert: [Ii][Nn][Ss][Ee][Rr][Tt]
;

Begin: [Bb][Ee][Gg][Ii][Nn]
;