package org.xmlgen.template.dom.specialization.instructions;

import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

@SuppressWarnings("serial")
abstract public class StructuralInstruction extends ExpansionInstruction implements Tagged
{
	protected StructuralInstruction(String data, TaggedContext taggedContext, int line, int column)
	{
		super(data, line, column);
		setLabel(taggedContext);
	}

	/**
	 * Sets the label.
	 *
	 * @param label
	 *           the new label
	 */
	protected void setLabel(String label)
	{
		this.label = label;
	}

	protected void setLabel(TaggedContext taggedContext)
	{
		String label = taggedContext == null || taggedContext.label() == null ? "" : taggedContext.label().Label().getText();
		setLabel(label);
	}
	
	protected void initialize(ExpansionContext expansionContext)
	{
		expansionContext.getContext().getStructuresStack().push(this);
		String label = getLabel();
		Frame newFrame = new Frame(label); 
		pushDatasourcesContext(newFrame);
	}
	
	protected void pushDatasourcesContext(Frame newFrame)
	{
		FrameStack frameStack = Context.getInstance().getFrameStack();
		frameStack.push(newFrame);
	}
	
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
	
	public void end(ExpansionContext expansionContext)
	{
		expansionContext.getContext().getStructuresStack().pop();
		enableExecution();
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}
	
	protected void enableExecution()
	{
		isExecuting = true;
	}

	protected void disableExecution()
	{
		isExecuting = false;
	}
	
	public boolean isExecuting()
	{
		return isExecuting;
	}
	
	public boolean isFinished()
	{
		return true;
	}
	
	private String label;
	
	private boolean isExecuting = true;
}
