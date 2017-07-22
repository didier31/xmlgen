/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

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
import org.xmlgen.parser.pi.PIParser.EndContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;
import org.xmlgen.template.dom.specialization.content.Element;

// TODO: Auto-generated Javadoc
/**
 * The Class EndInstruction.
 */
@SuppressWarnings("serial")
public class EndInstruction extends StructuralInstruction
{
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		if (expansionContext.isExecuting())
		{
			traceEndInstruction();
			StructuralInstruction structuralInstruction = expansionContext.getRelatedStructure();
			if (structuralInstruction == null)
			{
				/* TODO : the end has no related beginning structure. Hence, This end is too much.
				 * Notify the user for his error.
				*/ 
			}
			else if (structuralInstruction instanceof BeginInstruction)
			{
				close(structuralInstruction, expansionContext);
			}
			else if (structuralInstruction instanceof CapturesInstruction)
			{
				CapturesInstruction capturesInstruction = (CapturesInstruction) structuralInstruction;
				if (!capturesInstruction.isFinished())
				{
					it.set(capturesInstruction);
				}
				else
				{
					close(capturesInstruction, expansionContext);
				}
			}
			else
			{
				// Todo : Internal Error : StructureInstruction class hierarchy
				// inconsistency with this multiple if
				assert (false);
			}
		}
		return new Vector<Cloneable>(0);
	}

	protected void close(StructuralInstruction structuralInstruction, ExpansionContext expansionContext)
	{
		checkEndName();
		Element parent = (Element) getParent();
		parent.popFrame(this);
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

	protected void checkEndName()
	{
		FrameStack frameStack = Context.getInstance().getFrameStack();
		Frame currentFrame = frameStack.peek();
		String frameName = currentFrame.getName();

		if ((frameName == null && getLabel() != null)
				|| frameName != null && getLabel() != null && !frameName.equals(getLabel()))
		{
			Message message = new Message("Expecting the end instruction " + frameName + ", not the one named " + getLabel());
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
		return "end " + getLabel() != null ? getLabel() : "";
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

	@Override
	public boolean isFinished()
	{
		return true;
	}
}
