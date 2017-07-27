/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.xmlgen.context.Context;
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
import org.xmlgen.parser.pi.PIParser.EndContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

// TODO: Auto-generated Javadoc
/**
 * The Class EndInstruction.
 */
@SuppressWarnings("serial")
public class EndInstruction extends TaggedInstruction
{
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		StructuralInstruction structuralInstruction = expansionContext.getRelatedStructure();
		if (structuralInstruction == null)
		{
			/*
			 * TODO : the end has no related beginning structure. Hence, This end
			 * is too much. Notify the user for his error.
			 */
		}
		else if (structuralInstruction.isFinished())
		{
			traceEndInstruction();
			close(structuralInstruction, expansionContext);
		}
		else if (structuralInstruction.isExecuting())
		{
			it.set(structuralInstruction);
		}
		return new Vector<Cloneable>(0);
	}

	protected void close(StructuralInstruction structuralInstruction, ExpansionContext expansionContext)
	{
		checkEndLabel(structuralInstruction);
		structuralInstruction.end(expansionContext);
	}

	/**
	 * Instantiates a new end instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param endContext
	 *           the end instruction
	 */
	protected EndInstruction(String pi, EndContext endContext, int line, int column)
	{
		super(pi, (TaggedContext) endContext.getParent(), line, column);
	}

	protected void checkEndLabel(StructuralInstruction structuralInstruction)
	{
		String structureName = structuralInstruction.getLabel();
		if ((structureName == null && getLabel() != null)
				|| structureName != null && getLabel() != null && !structureName.equals(getLabel()))
		{
			Message message = new Message(
					"Expecting the end instruction " + structureName + ", not the one named " + getLabel());
			Notification blockNamesNotCorresponding = new Notification(Module.Expansion, Gravity.Warning, Subject.Template,
					message);
			Artifact artifact = new Artifact("End instruction");
			LocationImpl locationImpl = new LocationImpl(artifact, -1, getColumn(), getLine());
			ContextualNotification contextual = new ContextualNotification(blockNamesNotCorresponding, locationImpl);
			Notifications.getInstance().add(contextual);
		}
	}

	@Override
	public String toString()
	{
		String label = getLabel() != null ? getLabel() : "";
		return "end " + label;
	}

	public void traceEndInstruction()
	{
		if (Context.getInstance().isTrace())
		{
			String messageStr = toString();
			Message message = new Message(messageStr);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.Instruction,
					message);

			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getColumn(),
					getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications.getInstance().add(contextual);
		}
	}
}
