package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

@SuppressWarnings("serial")
public abstract class IterativeInstruction extends StructuralInstruction
{
	protected IterativeInstruction(String data,TaggedContext taggedContext, int line, int column)
	{
		super(data, taggedContext, line, column);
	}
	
	@Override
	final protected void initialize(ExpansionContext expansionContext)
	{		
		if (!isInitialized())
		{
		super.initialize(expansionContext);
		doInitialisation(expansionContext);
		isInitialized = true;
		}
	}
	
	@Override
	public void end(ExpansionContext expansionContext)
	{
		super.end(expansionContext);
		isInitialized = false;
	}
	
	protected boolean isInitialized()
	{
		return isInitialized;
	}
	
	abstract protected void doInitialisation(ExpansionContext expansionContext); 

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{	
		initialize(expansionContext);
		if (expansionContext.isExecuting())
		{
			boolean isIterating = iterate(expansionContext);
			if (!isIterating)
			{
				disableExecution();
			}
		}
		return new Vector<Cloneable>(0);
	}
	
	/**
	 * Iterate.
	 *
	 * @return if something has been effectively iterated.
	 */

	final public boolean iterate(ExpansionContext expansionContext)
	{
		boolean isIterating = iterateImpl(expansionContext);
		return isIterating;
	}
	
	abstract protected boolean iterateImpl(ExpansionContext expansionContext);
	
	public String getLabel()
	{
		return label;
	}
	
	
	protected void setLabel(String label)
	{
		this.label = label;
	}
			
	private String label;
	
	private boolean isInitialized;
}
