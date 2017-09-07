package org.xmlgen.template.dom.specialization.instructions;

import java.util.Stack;
import java.util.Vector;

import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public abstract class IterativeInstruction extends StructuralInstruction
{
	protected IterativeInstruction(String data, String label, int line, int column, Xmlgen xmlgen)
	{
		super(data, label, line, column, xmlgen);
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		State currentState = currentState();
		boolean notInitialized = !currentState.isInitialized();
		if (notInitialized)
		{
			currentState().setCompletion(false);
			currentState.setInitialized();
		}
	}
	
	@Override
	protected Vector<Cloneable> doExpandMySelf(TemplateIterator it)
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
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
	
	protected State currrentState()
	{
		return states.peek();
	}
	
	@Override
	protected void createState()
	{
		State currentState = new State();
		states.push(currentState);
	}

	@Override
	protected void deleteState()
	{
		states.pop();
	}

	@Override
	protected State currentState()
	{
		return states.peek();
	}

	@Override
	protected boolean thereIsNoState()
	{
		return states.isEmpty();
	}
	
	private Stack<State> states = new Stack<State>();
	
	protected class State extends StructuralInstruction.State
	{
		private void setInitialized()
		{
			isInitialized = true;
		}
		
		private boolean isInitialized()
		{
			return isInitialized;
		}
		
		private boolean isInitialized = false;
	}
}
