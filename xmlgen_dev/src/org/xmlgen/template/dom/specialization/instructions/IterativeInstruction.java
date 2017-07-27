package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

@SuppressWarnings("serial")
public abstract class IterativeInstruction extends StructuralInstruction
{
	protected IterativeInstruction(String data, TaggedContext taggedContext, int line, int column)
	{
		super(data, taggedContext, line, column);
	}

	protected boolean isInitialized(ExpansionContext expansionContext)
	{
		boolean isInitialized;
		StructuralInstruction structuralInstruction = expansionContext.getMotherStructure();

		if (this == structuralInstruction)
		{
			isInitialized = true;
		}
		else
		{
			isInitialized = false;
		}
		return isInitialized;
	}

	@Override
	final protected void initialize(ExpansionContext expansionContext)
	{

		if (!isInitialized(expansionContext))
		{
			super.initialize(expansionContext);
			doInitialisation(expansionContext);
		}
	}

	@Override
	public void end(ExpansionContext expansionContext)
	{
		super.end(expansionContext);
	}

	abstract protected void doInitialisation(ExpansionContext expansionContext);

	@Override
	protected Vector<Cloneable> doExpandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		iterate(expansionContext);
		if (isFinished())
		{
			disableExecution();
		}
		return new Vector<Cloneable>(0);
	}

	/**
	 * Iterate.
	 *
	 * @return if something has been effectively iterated.
	 */

	final public void iterate(ExpansionContext expansionContext)
	{
		boolean isFinished = iterateImpl(expansionContext);
		setCompletion(isFinished);
	}

	abstract protected boolean iterateImpl(ExpansionContext expansionContext);
}
