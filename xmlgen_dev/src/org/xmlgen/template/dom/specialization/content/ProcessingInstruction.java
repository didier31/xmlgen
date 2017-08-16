package org.xmlgen.template.dom.specialization.content;

import java.util.Map;
import java.util.Vector;

import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class ProcessingInstruction extends LocatedProcessingInstruction implements Expandable
{

	public ProcessingInstruction(String target, String data, Xmlgen xmlgen)
	{
		super(target, data);
		this.xmlgen = xmlgen;
	}

	public ProcessingInstruction(String target, Xmlgen xmlgen)
	{
		super(target);
		this.xmlgen = xmlgen;
	}

	public ProcessingInstruction(String target, Map<String, String> data, Xmlgen xmlgen)
	{
		super(target, data);
		this.xmlgen = xmlgen;
	}

	/**
	 * Is implemented in order to copy all others Processing Instruction than xmlgen ones. 
	 */
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		Xmlgen xmlgen = getXmlgen();
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		if (expansionContext.isExecuting())
		{
			return Util.expand(this, expansionContext);
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}

	protected Xmlgen getXmlgen()
	{
		return xmlgen;
	}
	
 private Xmlgen xmlgen;
}
