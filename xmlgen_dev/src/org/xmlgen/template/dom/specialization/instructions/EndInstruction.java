/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.List;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.EndContext;
import org.xmlgen.parser.pi.PIParser.ExportsContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

// TODO: Auto-generated Javadoc
/**
 * The Class EndInstruction.
 */
@SuppressWarnings("serial")
public class EndInstruction extends TaggedInstruction
{
	/**
	 * Instantiates a new end instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param endContext
	 *           the end instruction
	 */
	protected EndInstruction(String pi, EndContext endContext, int line, int column, Xmlgen xmlgen)
	{
		super(pi, (TaggedContext) endContext.getParent(), line, column, xmlgen);
		ExportsContext exportContext = endContext.exports(); 
		if (exportContext != null)
		{
			exports = exportContext.Ident();
		}
		else
		{
			exports = null;
		}
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
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
		traceEndInstruction();
		if (structuralInstruction.isExecuting())
		{
			exports();
		}
		structuralInstruction.end();
	}

	protected void exports()
	{
		if (exports != null)
		{
			FrameStack frameStack = getXmlgen().getFrameStack();
			Frame currentFrame = frameStack.peek();
			Frame upperFrame = frameStack.elementAt(frameStack.framesCount() - 2);
			for (TerminalNode export : exports)
			{
				String exportedDataSourceId = export.getText();
				Object dataSource = currentFrame.get(exportedDataSourceId);
				if (dataSource != null || currentFrame.containsKey(exportedDataSourceId))
				{
					upperFrame.put(exportedDataSourceId, dataSource);
					traceExport(exportedDataSourceId);
				}
				else
				{
					// TODO: Notice user mispelled id
				}
			}
		}
	}

	protected void traceExport(String exportedDataSourceId)
	{
		if (getXmlgen().getContext().isTrace())
		{
			String messageStr = "export: " + exportedDataSourceId;
			Message message = new Message(messageStr);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.Instruction,
					message);

			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getColumn(),
					getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			getXmlgen().getNotifications().add(contextual);
		}
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
			getXmlgen().getNotifications().add(contextual);
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
		if (getXmlgen().getContext().isTrace())
		{
			String messageStr = toString();
			Message message = new Message(messageStr);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.Instruction,
					message);

			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getColumn(),
					getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			getXmlgen().getNotifications().add(contextual);
		}
	}

	private List<TerminalNode> exports;
}
