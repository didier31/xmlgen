package org.xmlgen.template.dom.specialization;

import org.jdom2.Content;
import org.jdom2.located.Located;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;

@SuppressWarnings("serial")
public abstract class IterativeInstruction extends ExpansionInstruction implements Tagged
{
	protected IterativeInstruction(LocatedProcessingInstruction pi, String label)
	{
		super(pi);
		setLabel(label);
	}
	
	public void initialize()
	{
		String label = getLabel();
		Frame newFrame = new Frame(label);
		FrameStack frameStack = Context.getInstance().getFrameStack(); 
		frameStack.push(newFrame);
	}

	/**
	 * Iterate.
	 *
	 * @return if something has been effectively iterated.
	 */
	abstract public boolean iterate();
	
	public void terminate()
	{   
		popFrame(this);
	}
	
	abstract public void terminate(Content lastInstruction);

	public String getLabel()
	{
		return label;
	}
	
	// TODO: Auto-generated Javadoc
	/**
	 * Pop the frame on top of the stack.
	 * 
	 * Notify the user if it can be done.
	 *
	 * @param located
	 *          
	 */
	protected void popFrame(Located located)
	{
		FrameStack frameStack = Context.getInstance().getFrameStack();
		if (frameStack.isEmpty())
		{
			Message message = new Message("No more frame to discard");
			Notification noMoreFrameToDiscard = new Notification(Module.Expansion, Gravity.Warning, Subject.Template,
					message);
			
			Artifact artifact = new Artifact("");			
			int line = located.getLine(),
				 column = located.getColumn();			
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
	
	protected void setLabel(String label)
	{
		this.label = label;
	}
			
	String label;
}
