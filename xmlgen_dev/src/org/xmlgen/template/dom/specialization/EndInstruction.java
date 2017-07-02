/*
 * 
 */
package org.xmlgen.template.dom.specialization;

import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.context.Context;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.EndContext;

// TODO: Auto-generated Javadoc
/**
 * The Class EndInstruction.
 */
public class EndInstruction extends ExpansionInstruction
{

	/**
	 * Instantiates a new end instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param endInstruction
	 *           the end instruction
	 */
	protected EndInstruction(LocatedProcessingInstruction pi, EndContext endInstruction)
	{
		super(pi);
		if (endInstruction.label() != null)
		{
			setLabel(endInstruction.label().Ident().getText());
		}
		else
		{
			setLabel(null);
		}
	}

	/**
	 * Instantiates a new end instruction.
	 *
	 * @param label
	 *           the label
	 * @param target
	 *           the target
	 * @param data
	 *           the data
	 */
	public EndInstruction(String label, String target, String data)
	{
		super(target, data);
		setLabel(label);
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

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public String toString()
	{
		return "end loop" + label != null ? label : "";
	}

	public void traceEndInstruction()
	{
		if (Context.getInstance().isTrace())
		{
			Message message = new Message(toString());
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			
			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getColumn(),
					getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications.getInstance().add(contextual);
		}		
	}
	
	/** The label. */
	private String label;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8264616535449826911L;
}
