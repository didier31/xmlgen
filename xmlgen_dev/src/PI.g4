grammar PI;

@header {
package org.xmlgen.parser.pi;
}

import Query;

inputPI : (captures | content | end) EOF
;

captures : (label ':')? capture (',' capture)*
;

capture : dataID '=' expression
;

dataID : Ident
;

content : attributeContent | elementContent
;

attributeContent : ATTRIBUTE attributeID '=' expression
;

elementContent : expression
;

attributeID : Ident
;

end: END label? 
;

label: Ident
;

END: [Ee][nN][Dd]
;

ATTRIBUTE: [aA][tT][tT][rR]
;
