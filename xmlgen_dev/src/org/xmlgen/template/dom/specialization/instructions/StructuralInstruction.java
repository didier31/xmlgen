package org.xmlgen.template.dom.specialization.instructions;

import java.util.Stack;
import java.util.Vector;

import org.jdom2.located.Located;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

@SuppressWarnings("serial")
abstract public class StructuralInstruction extends TaggedInstruction
{
	protected StructuralInstruction(String data, TaggedContext taggedContext, int line, int column, Xmlgen xmlgen)
	{
		super(data, taggedContext, line, column, xmlgen);
	}

	protected void initialize()
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();

		int actualInsertInProgressCount = expansionContext.getInsertInProgressCount();
		if (thereIsNoState() || currentState().getInsertInProgressCount() < actualInsertInProgressCount)
		{
			createState(expansionContext);
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

	public void newInstance()
	{
	}

	public void end()
	{
		if (currentState().isInitialized())
		{
			popFrame(this);
			Stack<StructuralInstruction> structureStack = getXmlgen().getExpansionContext().getContext()
					.getStructuresStack();
			structureStack.pop();
		}
		deleteState();
	}

	abstract protected void createState(ExpansionContext expansionContext);

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

	/**
	 * Adds the to current frame.
	 *
	 * @param id
	 *           the id
	 * @param value
	 *           the value
	 */
	protected void addToCurrentFrame(String id, Object value)
	{
		FrameStack frameStack = getXmlgen().getFrameStack();
		Frame currentFrame = frameStack.peek();
		traceReferenceDeclaration(id, value, currentFrame);
		currentFrame.put(id, value);
	}

	protected void traceReferenceDeclaration(String id, Object value, Frame frame)
	{
		if (getXmlgen().getContext().isTrace())
		{
			Message message = new Message(frame.toString() + " += [" + id + "]");
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			Notifications notifications = getXmlgen().getNotifications();
			notifications.add(notification);
			message = new Message(id + " = " + value);
			notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource, message);
			notifications.add(notification);
		}
	}

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

	
	/**
	 * Pop the frame on top of the stack.
	 * 
	 * Notify the user if it can be done.
	 *
	 * @param located
	 * 
	 */
	public void popFrame(Located located)
	{
		FrameStack frameStack = getXmlgen().getFrameStack();
		if (frameStack.isEmpty())
		{
			Message message = new Message("No more frame to discard");
			Notification noMoreFrameToDiscard = new Notification(Module.Expansion, Gravity.Warning, Subject.Template,
					message);

			Artifact artifact = new Artifact("");
			int line = located.getLine(), column = located.getColumn();
			LocationImpl locationImpl = new LocationImpl(artifact, -1, column, line);
			ContextualNotification contextual = new ContextualNotification(noMoreFrameToDiscard, locationImpl);

			getXmlgen().getNotifications().add(contextual);
		}
		else
		{
			popFrame();
		}
	}

	protected void popFrame()
	{
		FrameStack frameStack = getXmlgen().getFrameStack();
		Frame framePoped = frameStack.peek();
		frameStack.pop();
		tracePop(framePoped);
	}

	/**
	 * 
	 * Trace Frame operation
	 * 
	 * @param op
	 * @param frame
	 */
	protected void tracePop(Frame frame)
	{
		if (getXmlgen().getContext().isTrace())
		{
			Message message = new Message("pop " + frame);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			getXmlgen().getNotifications().add(notification);
		}
	}

	protected class State
	{

		public State()
		{
			ExpansionContext expansionContext = getXmlgen().getExpansionContext();
			setExecution(expansionContext.isExecuting());
			insertInProgressCount = expansionContext.getInsertInProgressCount();
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
		private int insertInProgressCount;
	}
}
