package org.xmlgen.template.dom.specialization.instructions;

import java.util.Stack;
import java.util.Vector;

import org.xmlgen.Xmlgen;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
abstract public class StructuralInstruction extends TaggedInstruction
{
	protected StructuralInstruction(String data, String tag, int line, int column, Xmlgen xmlgen)
	{
		super(data, tag, line, column, xmlgen);
	}
	
	protected void initialize()
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();

		int actualInsertInProgressCount = expansionContext.getInsertInProgressCount();
		if (thereIsNoState() || currentState().getInsertInProgressCount() < actualInsertInProgressCount)
		{
			createState();
		}
		State state = currentState();
		if (!state.isInitialized())
		{
			Stack<StructuralInstruction> structuresStack = expansionContext.getContext().getStructuresStack();
			if (!structuresStack.isEmpty())
			{
				state.setExecution(structuresStack.peek().isExecuting());
			}
			structuresStack.push(this);
			String label = getLabel();
			Frame newFrame = new Frame(label);
			pushDatasourcesContext(newFrame);
			state.setInitialized();
		}
	}

	protected void pushDatasourcesContext(Frame newFrame)
	{
		FrameStack frameStack = getXmlgen().getFrameStack();
		frameStack.push(newFrame);
	}

	public void end()
	{
		if (currentState().isInitialized())
		{
			popFrame(this);
			Stack<StructuralInstruction> structureStack = getXmlgen().getExpansionContext().getContext().getStructuresStack();
			structureStack.pop();
		}
		deleteState();
	}

	abstract protected void createState();

	abstract protected State currentState();

	abstract protected boolean thereIsNoState();

	abstract protected void deleteState();

	final public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		Vector<Cloneable> expanded;
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
		initialize();
		if (expansionContext.isExecuting())
		{
			expanded = doExpandMySelf(it);
		}
		else
		{
			expanded = new Vector<Cloneable>(0);
		}
		return expanded;
	}

	abstract protected Vector<Cloneable> doExpandMySelf(TemplateIterator it);

	protected void enableExecution()
	{
		currentState().enableExecution();
	}

	protected void disableExecution()
	{
		currentState().disableExecution();
	}

	protected void setFinished()
	{
		currentState().setFinished();
	}

	protected void setCompletion(boolean isFinished)
	{
		currentState().setCompletion(isFinished);
	}

	protected void setReadyToRun()
	{
		setCompletion(false);
	}
	
	public void setExecuted()
	{
		currentState().setExecuted();
	}

	public boolean isExecuting()
	{
		return currentState().isExecuting();
	}

	public boolean isFinished()
	{
		return currentState().isFinished();
	}

	public boolean isInitialized()
	{
		return currentState().isInitialized();
	}

	public boolean executed()
	{
		return currentState().executed();
	}

	protected class State
	{

		public State()
		{
			ExpansionContext expansionContext = getXmlgen().getExpansionContext();
			setExecution(expansionContext.isExecuting());
			insertInProgressCount = expansionContext.getInsertInProgressCount();
		}

		public void setExecuted()
		{
			executed = true;
		}

		protected void enableExecution()
		{
			isExecuting = true;
		}

		protected void disableExecution()
		{
			isExecuting = false;
		}

		protected void setExecution(boolean isExecuting)
		{
			this.isExecuting = isExecuting;
		}

		protected void setFinished()
		{
			isFinished = true;
		}

		protected void setCompletion(boolean isFinished)
		{
			this.isFinished = isFinished;
		}

		protected void setReadyToRun()
		{
			isFinished = false;
		}

		private void setInitialized()
		{
			isInitialized = true;
		}

		public boolean isExecuting()
		{
			return isExecuting;
		}

		public boolean isFinished()
		{
			return isFinished;
		}

		private boolean isInitialized()
		{
			return isInitialized;
		}

		private int getInsertInProgressCount()
		{
			return insertInProgressCount;
		}

		private boolean isInitialized = false;
		private boolean isExecuting = true;
		private boolean isFinished = true;
		private boolean executed = false;
		private int insertInProgressCount;
		public boolean executed()
		{
			return executed;
		}
	}
}
