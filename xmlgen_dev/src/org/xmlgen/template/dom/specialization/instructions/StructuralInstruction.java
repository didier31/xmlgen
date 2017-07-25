package org.xmlgen.template.dom.specialization.instructions;

import java.util.Stack;
import java.util.Vector;

import org.jdom2.located.Located;
import org.xmlgen.context.Context;
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
	protected StructuralInstruction(String data, TaggedContext taggedContext, int line, int column)
	{
		super(data, taggedContext, line, column);
	}

	protected void initialize(ExpansionContext expansionContext)
	{
		createState(expansionContext);
		State state = currentState();
		Stack<StructuralInstruction> structuresStack = expansionContext.getContext().getStructuresStack();
		if (!structuresStack.isEmpty())
		{
			state.setExecution(structuresStack.peek().isExecuting());
		}
		expansionContext.getContext().getStructuresStack().push(this);
		String label = getLabel();
		Frame newFrame = new Frame(label);
		FrameStack frameStack = Context.getInstance().getFrameStack();
		frameStack.push(newFrame);
	}
	
	public void end(ExpansionContext expansionContext)
	{
		popFrame(this);
		expansionContext.getContext().getStructuresStack().pop();
		deleteState();
	}

	abstract protected void createState(ExpansionContext expansionContext);

	abstract protected State currentState();

	abstract protected void deleteState();

	final public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		Vector<Cloneable> expanded;
		initialize(expansionContext);
		if (isExecuting())
		{	
			expanded = doExpandMySelf(it, expansionContext);
		}
		else
		{
			 expanded = new Vector<Cloneable>(0);
		}
		return expanded;
	}
	
	abstract protected Vector<Cloneable> doExpandMySelf(TemplateIterator it, ExpansionContext expansionContext);
	
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
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		assert (!currentFrame.containsKey(id));
		currentFrame.put(id, value);
		traceReferenceDeclaration(id, currentFrame);
	}

	protected void traceReferenceDeclaration(String id, Frame frame)
	{
		if (Context.getInstance().isTrace())
		{
			Message message = new Message(frame.toString() + " adding " + id);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			Notifications.getInstance().add(notification);
		}
	}

	protected void enableExecution()
	{
		currentState().isExecuting = true;
	}

	protected void disableExecution()
	{
		currentState().isExecuting = false;
	}

	protected void setFinished()
	{
		currentState().isFinished = true;
	}

	protected void setCompletion(boolean isFinished)
	{
		currentState().isFinished = isFinished;
	}

	protected void setReadyToRun()
	{
		currentState().isFinished = false;
	}

	public boolean isExecuting()
	{
		return currentState().isExecuting;
	}

	public boolean isFinished()
	{
		return currentState().isFinished;
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
		FrameStack frameStack = Context.getInstance().getFrameStack();
		if (frameStack.isEmpty())
		{
			Message message = new Message("No more frame to discard");
			Notification noMoreFrameToDiscard = new Notification(Module.Expansion, Gravity.Warning, Subject.Template,
					message);

			Artifact artifact = new Artifact("");
			int line = located.getLine(), column = located.getColumn();
			LocationImpl locationImpl = new LocationImpl(artifact, -1, column, line);
			ContextualNotification contextual = new ContextualNotification(noMoreFrameToDiscard, locationImpl);

			Notifications.getInstance().add(contextual);
		}
		else
		{
			popFrame();
		}
	}

	protected void popFrame()
	{
		FrameStack frameStack = Context.getInstance().getFrameStack();
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
		if (Context.getInstance().isTrace())
		{
			Message message = new Message("pop " + frame);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			Notifications.getInstance().add(notification);
		}
	}
	
	protected class State
	{

		public State(ExpansionContext expansionContext)
		{
			setExecution(expansionContext.isExecuting());
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

		public boolean isExecuting()
		{
			return isExecuting;
		}

		public boolean isFinished()
		{
			return isFinished;
		}

		private boolean isFinished = false;
		private boolean isExecuting = true;
	}
}
