grammar Cmdline;

@header {
package org.xmlgen.parser.cmdline;  
}

import Query;

cmdline : cmd+ EOF
;

cmd : (dataSource | template | schema | output) 
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

Ident : (Letter | '_') (Letter | [0-9] | '_')*
;
fragment Letter : [a-zA-Z]
;

