package org.xmlgen.template.dom.specialization;

import org.jdom2.ProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.EndContext;

public class EndInstruction extends ExpansionInstruction 
{
	protected EndInstruction(ProcessingInstruction pi, EndContext endInstruction) 
	{
		super(pi);
		setLabel(endInstruction.label().Ident().getText());
	}
	
	public EndInstruction(String label, String target, String data)
	{
		super(target, data);
		setLabel(label);
	}
	
	protected void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	private String label;
	
	private static final long serialVersionUID = 8264616535449826911L;	
}
