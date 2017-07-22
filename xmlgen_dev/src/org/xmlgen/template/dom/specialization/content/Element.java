package org.xmlgen.template.dom.specialization.content;

import java.util.Stack;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Namespace;
import org.jdom2.located.Located;
import org.jdom2.located.LocatedElement;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
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
import org.xmlgen.template.dom.specialization.instructions.StructuralInstruction;

@SuppressWarnings("serial")
public class Element extends LocatedElement implements Expandable
{

	public Element(String name)
	{
		super(name);
	}

	public Element(String name, String uri)
	{
		super(name, uri);
	}

	public Element(String name, String prefix, String uri)
	{
		super(name, prefix, uri);
	}

	public Element(String name, Namespace namespace)
	{
		super(name, namespace);
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		Vector<Cloneable> thisClone = Util.expand(this, expansionContext);
		if (expansionContext.isExecuting())
		{
		Vector<Cloneable> expanded = new Vector<Cloneable>(10);
		TemplateIterator childrenIt = new TemplateIterator(this);
		childrenIt.descendant();
		expansionContext.push();
		Stack<StructuralInstruction> structureStack = expansionContext.getContext().getStructuresStack();
		boolean isFinished;
		do
		{
			while (childrenIt.current() != null)
			{
				Object current = childrenIt.current();
				Expandable expandable = (Expandable) childrenIt.current();
				Vector<Cloneable> localExpanded = expandable.expandMySelf(childrenIt, expansionContext);
				expanded.addAll(localExpanded);
				if (childrenIt.current() == current)
				{
					childrenIt.sibling();
				}
			}
			
			isFinished = true;			
			while (!structureStack.isEmpty() && isFinished)
			{				
				StructuralInstruction structuralInstruction = structureStack.peek();
				isFinished = structuralInstruction.isFinished();
				if (isFinished)
				{
					structuralInstruction.end(expansionContext);
				}
				else
				{
					childrenIt.set(structuralInstruction);
				}
			}		
		}
		while (!structureStack.isEmpty());

		expansionContext.pop();
		setChildren((Element) thisClone.get(0), expanded);
		}
		return thisClone;
	}

	protected void setChildren(Element toRoot, Vector<Cloneable> children)
	{
		toRoot.getContent().clear();
		for (Cloneable child : children)
		{
			if (child instanceof Attribute)
			{
				Attribute attribute = (Attribute) child;
				toRoot.setAttribute(attribute);
			}
			else
			{
				Content childContent = (Content) child;
				toRoot.addContent(childContent);
			}
		}
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
}
