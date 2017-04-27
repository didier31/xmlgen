package org.xmlgen.template.dom.specialization;

import org.w3c.dom.ProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.EndContext;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;

public class EndInstruction extends ExpansionInstruction 
{
	protected EndInstruction(ProcessingInstruction pi, EndContext endInstruction) 
	{
		super(pi);
		setLabel(endInstruction.label().Ident().getText());
	}
	
	public EndInstruction(String label, CoreDocumentImpl ownerDoc, String target, String data)
	{
		super(ownerDoc, target, data);
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
