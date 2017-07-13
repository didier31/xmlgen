package org.xmlgen.template.dom.specialization;

import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.InsertContext;

@SuppressWarnings("serial")
public class InsertInstruction extends ExpansionInstruction
{	
	protected InsertInstruction(LocatedProcessingInstruction pi, InsertContext insert)
	{
		super(pi);
	}
}
