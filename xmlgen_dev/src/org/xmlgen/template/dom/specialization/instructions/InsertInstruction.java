package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.jdom2.Content;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Context;
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
import org.xmlgen.notifications.Notifications;
import org.xmlgen.parser.pi.PIParser.InsertContext;
import org.xmlgen.template.dom.specialization.content.Element;

@SuppressWarnings("serial")
abstract class InsertInstruction extends ExpansionInstruction
{	
	static public InsertInstruction create(String pi, InsertContext insertContext, int line, int column, Xmlgen xmlgen)
	{
		TerminalNode labelContext = insertContext.Label();
		InsertInstruction insertInstruction;
		if (labelContext != null)
		{
			insertInstruction = new InsertBlockInstruction(pi, labelContext, line, column, xmlgen); 
		}
		else
		{
			insertInstruction = new InsertTemplateInstruction(pi, insertContext.templateCall(), line, column, xmlgen);
		}		
	return insertInstruction;
	}
	
	protected InsertInstruction(String pi, int line, int column, Xmlgen xmlgen)
	{
		super(pi, line, column, xmlgen);
	}
	
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		Xmlgen xmlgen = getXmlgen();
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		
		if (expansionContext.isExecuting())
		{
			trace();
			Element body = getBody(); 			
			List<Content> contents = body.getContent(); 			
			Content instruction = contents.get(0);
			
			TemplateIterator recursiveIt = new TemplateIterator(instruction);
			
			FrameStack frameStack = getXmlgen().getFrameStack();			
			frameStack.pushNumbering();			
			expansionContext.incInsertInProgressCount();
			
			Vector<Cloneable> expanded = expand(body, recursiveIt);
			
			expansionContext.decInsertInProgressCount();			
			frameStack.popNumbering();
			
			return expanded;
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}

	protected Vector<Cloneable> expand(Element body, TemplateIterator recursiveIt)
	{
		Vector<Cloneable> expanded = body.expandMySelf(recursiveIt, false);
		return expanded;
	}
	
	protected Collection<Content> structureOf(StructuralInstruction structuralInstruction)
	{
		TemplateIterator structureIt = new TemplateIterator(structuralInstruction);
		Stack<StructuralInstruction> structures = new Stack<StructuralInstruction>();
		Vector<Content> structure = new Vector<Content>(0); 
		do
		{	
			Content templaceContent = structureIt.current(); 
			Content content = templaceContent.clone();
			structure.addElement(content);
			if (content instanceof StructuralInstruction)
			{
				StructuralInstruction structuralInstr = (StructuralInstruction) content;
				structures.push(structuralInstr);
			}
			else if (content instanceof EndInstruction)
			{
				EndInstruction endInstruction = (EndInstruction) content;
				if (structures.isEmpty())
				{
					// TODO: Notify if necessary the user
				}
				else
				{
					StructuralInstruction relatedStructuralInstruction = structures.peek();
					String startLabel = relatedStructuralInstruction.getLabel();
					String endLabel = endInstruction.getLabel();
					if (!startLabel.equals(endLabel))
					{
						// TODO : Eventually notify the user if not ever done.
					}
					else
					{
						structures.pop();
					}					
				}
			}		
		structureIt.sibling();
		}
		while (structureIt.current() != null && !structures.isEmpty());
		return structure;
	}
	
	protected void trace()
	{
		Message message = new Message(this.getData());
		Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.Template, message);
		Xmlgen xmlgen = getXmlgen();
		Context context = xmlgen.getContext();
		Artifact artefact = new Artifact(context.getXmlTemplate());
		LocationImpl location = new LocationImpl(artefact, -1, getLine(), getColumn());
		ContextualNotification contextualNotification = new ContextualNotification(notification, location);
		Notifications notifications = getXmlgen().getNotifications();
		notifications.add(contextualNotification);
	}
	
	abstract protected Element getBody();
	
	protected Element body = null;
}
