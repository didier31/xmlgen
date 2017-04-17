package org.xmlgen.template.dom.specialization;

import org.w3c.dom.ProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.ElementContentContext;

public class ElementContentInstruction extends ContentInstruction
{
	protected ElementContentInstruction(ProcessingInstruction pi, ElementContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
	}
	
	private static final long serialVersionUID = -5491980343490480468L;
}