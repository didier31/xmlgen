grammar Cmdline;

@header {
package org.xmlgen.parser.cmdline;
}

import Query;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

cmdline : cmd* EOF
;

cmd : dataSource | template | schema | output
;

dataSource : Ident '=' Filename
;

output : '--output' Filename
;

schema : '--schema' Filename
;

template : '--template' Filename
;

Filename : '\'' String '\'' | String
;

String : [^\\'] (Escape|.)*? [^\\'] 
;

fragment Escape : '\\\\' | '\\\''
;

// Rigourously the same as Acceleo Query Language
Ident : (Letter | '_') (Letter | [0-9] | '_')* 
;
fragment Letter : [a-zA-Z]
;

