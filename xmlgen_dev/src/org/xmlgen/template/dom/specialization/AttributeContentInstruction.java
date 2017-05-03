package org.xmlgen.template.dom.specialization;

import org.jdom2.ProcessingInstruction;
import org.w3c.dom.Node;
import org.xmlgen.parser.pi.PIParser.AttributeContentContext;

public class AttributeContentInstruction extends ContentInstruction
{	
	protected AttributeContentInstruction(ProcessingInstruction pi, AttributeContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
		attributeId = parsedPI.attributeID().Ident().getText();
	}
	
	public String getAttributeId()
	{
		return attributeId;
	}

	public Node getAttributeNode()
	{
		return attrNode;
	}
	
	private static final long serialVersionUID = -1488609623791079537L;
	private String attributeId;
	private Node attrNode;
}
