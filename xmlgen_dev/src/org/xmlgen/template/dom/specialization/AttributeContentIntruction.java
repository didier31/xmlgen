package org.xmlgen.template.dom.specialization;

import org.w3c.dom.ProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.AttributeContentContext;

public class AttributeContentIntruction extends ContentInstruction
{
	protected AttributeContentIntruction(ProcessingInstruction pi, AttributeContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
	}
	
	private static final long serialVersionUID = -1488609623791079537L;
}
