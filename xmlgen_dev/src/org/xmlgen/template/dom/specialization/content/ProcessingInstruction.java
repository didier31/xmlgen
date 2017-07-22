package org.xmlgen.template.dom.specialization.content;

import java.util.Map;
import java.util.Vector;

import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class ProcessingInstruction extends LocatedProcessingInstruction implements Expandable
{

	public ProcessingInstruction(String target, String data)
	{
		super(target, data);
	}

	public ProcessingInstruction(String target)
	{
		super(target);
	}

	public ProcessingInstruction(String target, Map<String, String> data)
	{
		super(target, data);
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		return Util.expand(this, expansionContext);
	}
}
