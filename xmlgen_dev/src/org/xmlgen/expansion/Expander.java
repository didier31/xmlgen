package org.xmlgen.expansion;

import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.template.dom.Iterator;
import org.xmlgen.template.dom.specialization.AttributeContentInstruction;
import org.xmlgen.template.dom.specialization.CapturesInstruction;
import org.xmlgen.template.dom.specialization.ElementContentInstruction;
import org.xmlgen.template.dom.specialization.EndInstruction;
import org.xmlgen.template.dom.specialization.ExpansionInstruction;

public class Expander 
{	
	public Document expand(Document document)
	{
		Iterator iterator = new Iterator(document.getRootElement());
		Vector<Cloneable> node = expandDeeper(iterator);		
		assert node.size() == 1 && node.get(0) instanceof Element;
		Document expandedDocument = new Document((Element) node.get(0), document.getDocType());
		// TODO: Implement DOCTYPE copy.
		return expandedDocument;
	}
	
	protected Vector<Cloneable> expandDeeper(Iterator iterator)
	{
		Vector<Cloneable> allSibling = expandDeeperOnly(iterator);
		if (iterator.current() != null)
		{
			iterator.sibling();			
			
			if (iterator.current() != null)
			{
				Vector<Cloneable> subForest = expandDeeper(iterator);
				allSibling.addAll(subForest);
			}
		}
		return allSibling;
	} 
	
	protected Vector<Cloneable> expandDeeperOnly(Iterator iterator)
	{
		Vector<Cloneable> allSibling;
				
		if (iterator.current() != null)
		{
			allSibling = computeNode(iterator);			
			Iterator deeperIterator = new Iterator(iterator.current());
			deeperIterator.descendant();
			if (deeperIterator.current() != null)
			{
				Vector<Cloneable> subTree = expandDeeper(deeperIterator);
			   assert(allSibling.size() > 0);
			   setChildren((Element) allSibling.get(0), subTree);
			}			
		}
		else
		{
			allSibling = new Vector<Cloneable>(0);
		}
		return allSibling;
	}

	/**
	 * @param root
	 * @param parent
	 * @return
	 */
	protected Vector<Cloneable> computeNode(Iterator iterator)
	{
		Vector<Cloneable> allSibling;
		Content root = iterator.current();
		assert(root != null);
		Content rootClone;
		
		if (root instanceof ElementContentInstruction)
		{
			ElementContentInstruction elementContentInstruction = (ElementContentInstruction) root;			
			allSibling = doContentInstruction(elementContentInstruction);
		}
		else if (root instanceof AttributeContentInstruction)
		{
			AttributeContentInstruction attributeContentInstruction = (AttributeContentInstruction) root;			
			allSibling = doAttributeContentInstruction(attributeContentInstruction);
		}
		else if (root instanceof CapturesInstruction)
		{ 				
			allSibling = doLoop(iterator);
		}
		else if (root instanceof EndInstruction)
		{
			EndInstruction endInstruction = (EndInstruction) root;
			endLoop(endInstruction);			
			allSibling = new Vector<Cloneable>();
		}
		else if (root instanceof ProcessingInstruction && ExpansionInstruction.isExpandPI((ProcessingInstruction) root))
		{
			ProcessingInstruction pi = (ProcessingInstruction) root;
			ExpansionInstruction ei = ExpansionInstruction.create(pi);			
			iterator.set(ei);
			allSibling = computeNode(iterator);
		}
		else
		{
			rootClone = root.clone();
			allSibling = new Vector<Cloneable>(1);
			allSibling.add(rootClone);
		}
		return allSibling;
	}

	protected Vector<Cloneable> doLoop(Iterator loopIterator)
	{		
		assert(loopIterator.current() instanceof CapturesInstruction);
		
		CapturesInstruction capturesInstruction = (CapturesInstruction) loopIterator.current();
		
		String label = capturesInstruction.getLabel();
		Frame newFrame = new Frame(label, 0);
		Context.getInstance().getFrameStack().push(newFrame);
		Vector<Cloneable> expandedNodes = new Vector<Cloneable>(0);						
		
		Iterator atFirstContentInLoop = new Iterator(loopIterator.current());
		atFirstContentInLoop.sibling();
		while (capturesInstruction.iterate())
		{								
			boolean EndInstrNotEncountered = true;			
			loopIterator.set(atFirstContentInLoop.current());
			while (loopIterator.current() != null && EndInstrNotEncountered)
			{
				Content content = loopIterator.current();
				EndInstrNotEncountered = !(content instanceof EndInstruction);
				if (EndInstrNotEncountered)
				{
					Vector<Cloneable> localExpandedNodes = expandDeeperOnly(loopIterator);
					expandedNodes.addAll(localExpandedNodes);
				}
				if (loopIterator.current() != null)
				{
					loopIterator.sibling();
				}
			}
		}
		return expandedNodes;
	}
	
	/**
	 * @param attributeContentInstruction
	 * @return
	 */
	private Vector<Cloneable> doAttributeContentInstruction(AttributeContentInstruction attributeContentInstruction)
	{
		String attributeId = attributeContentInstruction.getAttributeId();
		Element parent = attributeContentInstruction.getParentElement();
		Attribute attribute = parent.getAttribute(attributeId);

		if (attribute != null)
		{
			Object computedValue = attributeContentInstruction.eval();
			Attribute attributeClone = attribute.clone();
			if (computedValue != null)
			{
				attributeClone.setValue(computedValue.toString());
			}
			
			Vector<Cloneable> attributes = new Vector<Cloneable>(1);
			attributes.add(attributeClone);
			return attributes;
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}

	/**
	 * @param elementContentInstruction
	 * @return
	 */
	private Vector<Cloneable> doContentInstruction(ElementContentInstruction elementContentInstruction)
	{
		Object computedValue = elementContentInstruction.eval();
		if (computedValue != null)
		{
			Text text = new Text(computedValue.toString());
			Vector<Cloneable> contents = new Vector<Cloneable>(1);
			contents.add(text);
			return contents;
		}
		else
		{	
			return new Vector<Cloneable>(0);
		}
	}

	private void setChildren(Element toRoot, Vector<Cloneable> children)
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
	 * @param endInstruction
	 */
	protected void endLoop(EndInstruction endInstruction)
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		String frameName = currentFrame.getName();
		
		if (frameName != null && !frameName.equals(endInstruction.getLabel()))
		{
			Message message = new Message("Expecting " + frameName + ", not " + endInstruction.getLabel());
			Notification blockNamesNotCorresponding = new Notification(Module.Expansion, Gravity.Warning, Subject.Template, message);
			Artefact artefact = new Artefact("");
			LocationImpl locationImpl = new LocationImpl(artefact, -1, endInstruction.getColumn(), endInstruction.getLine());
			ContextualNotification contextual = new ContextualNotification(blockNamesNotCorresponding, locationImpl);
			Notifications.getInstance().add(contextual);
		}
	} 
}
