package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class InsertInstruction extends ExpansionInstruction
{	
	protected InsertInstruction(String pi, int line, int column)
	{
		super(pi, line, column);
	}
	
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		super.expandMySelf(it, expansionContext);
		if (expansionContext.isExecuting())
		{
			BeginInstruction insertBlock = expansionContext.getCurrentBegin();
			TemplateIterator recursiveIt = new TemplateIterator(insertBlock);  
			Vector<Cloneable> expanded = insertBlock.expandMySelf(recursiveIt, expansionContext);
			return expanded;
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}
}
