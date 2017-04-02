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

content : expression
;

end: END labels?
; 

labels: label (',' label)*
;

label: Ident
;

END: [Ee][nN][Dd]
;
