/*
 * 
 */
package org.xmlgen.expansion;

import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.notifications.Artifact;
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

/**
 * The class Expand expands a template XML document with xmlgen process
 * instructions into the outgoing document, given data sources.
 * 
 * @author Didier Garcin
 * 
 */
public class Expander
{

	/**
	 * Expand the template
	 *
	 * @param template
	 *        the template document
	 *        
	 * @return the outgoing document
	 */
	public Document expand(Document template)
	{
		Iterator iterator = new Iterator(template.getRootElement());
		Vector<Cloneable> node = expandDeeper(iterator);
		assert node.size() == 1 && node.get(0) instanceof Element;
		Document expandedDocument = new Document((Element) node.get(0), template.getDocType());
		// TODO: Implement DOCTYPE copy.
		return expandedDocument;
	}

	/**
	 * Expanding deeper consists in visiting in depth and then in width the DOM
	 * document node's subtree.
	 * 
	 * @param iterator
	 *        the iterator referencing the actual DOM node.
	 *        
	 * @return the expanded DOM forest.
	 */
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

	/**
	 * Expands deeper only consists in visiting in depth only.
	 *
	 * @param iterator
	 *        the iterator referencing the actual DOM node.
	 *        
	 * @return the expanded DOM forest.
	 */
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
				assert (allSibling.size() > 0);
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
	 * Compute the given node in parameter : 
	 * - call the specialized method if the DOM node is a compiled xmlgen process instruction. 
	 * - call the xmlgen pi parser before if not compiled yet. 
	 * - or just clone the DOM node if it is ordinary one.
	 *
	 * @param iterator
	 *        the iterator
	 *        
	 * @return the vector
	 */
	protected Vector<Cloneable> computeNode(Iterator iterator)
	{
		Vector<Cloneable> expandedNode;
		Content root = iterator.current();
		Content rootClone;

		if (root == null)
		{
			expandedNode = new Vector<Cloneable>();
		}
		else if (root instanceof ElementContentInstruction)
		{
			ElementContentInstruction elementContentInstruction = (ElementContentInstruction) root;
			expandedNode = doContentInstruction(elementContentInstruction);
		}
		else if (root instanceof AttributeContentInstruction)
		{
			AttributeContentInstruction attributeContentInstruction = (AttributeContentInstruction) root;
			expandedNode = doAttributeContentInstruction(attributeContentInstruction);
		}
		else if (root instanceof CapturesInstruction)
		{
			expandedNode = doLoop(iterator);
		}
		else if (root instanceof EndInstruction)
		{
			EndInstruction endInstruction = (EndInstruction) root;
			endLoop(endInstruction);
			expandedNode = new Vector<Cloneable>();
		}
		else if (root instanceof LocatedProcessingInstruction
				&& ExpansionInstruction.isExpandPI((LocatedProcessingInstruction) root))
		{
			LocatedProcessingInstruction pi = (LocatedProcessingInstruction) root;
			ExpansionInstruction ei = ExpansionInstruction.create(pi);
			iterator.set(ei);
			expandedNode = computeNode(iterator);
		}
		else
		{
			rootClone = root.clone();
			expandedNode = new Vector<Cloneable>(1);
			expandedNode.add(rootClone);
		}
		return expandedNode;
	}

	/**
	 * Perform a capture xmlgen process instruction 
	 * meaning to perform an iteration on declared data sources.
	 *
	 * @param loopIterator
	 *        the iterator referencing the capture instruction.
	 *        
	 * @return the expanded xml nodes.
	 */
	protected Vector<Cloneable> doLoop(Iterator loopIterator)
	{
		assert (loopIterator.current() instanceof CapturesInstruction);

		CapturesInstruction capturesInstruction = (CapturesInstruction) loopIterator.current();	

		String label = capturesInstruction.getLabel();
		Frame newFrame = new Frame(label, 0);
		FrameStack frameStack = Context.getInstance().getFrameStack(); 
		frameStack.push(newFrame);
		// Initializes references in the just new created frame in stack
		capturesInstruction.initialize();
		
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
	 * Do attribute content instruction.
	 *
	 * @param attributeContentInstruction
	 *           the attribute content instruction
	 * @return the vector
	 */
	protected Vector<Cloneable> doAttributeContentInstruction(AttributeContentInstruction attributeContentInstruction)
	{
		Attribute attribute = attributeContentInstruction.getAttribute();

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

	// TODO: Auto-generated Javadoc
	/**
	 * Do content instruction.
	 *
	 * @param elementContentInstruction
	 *        the element content instruction
	 * @return the vector
	 */
	protected Vector<Cloneable> doContentInstruction(ElementContentInstruction elementContentInstruction)
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

	// TODO: Auto-generated Javadoc
	/**
	 * Sets the children.
	 *
	 * @param toRoot
	 *           the to root
	 * @param children
	 *           the children
	 */
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

	// TODO: Auto-generated Javadoc
	/**
	 * End loop.
	 *
	 * @param endInstruction
	 *           the end instruction
	 */
	protected void endLoop(EndInstruction endInstruction)
	{
		FrameStack frameStack = Context.getInstance().getFrameStack();
		Frame currentFrame = frameStack.peek();
		String frameName = currentFrame.getName();

		if (frameName == null || frameName.equals(endInstruction.getLabel()))
		{
			if (frameStack.isEmpty())
			{
				Message message = new Message("No more frame to discard");
				Notification noMoreFrameToDiscard = new Notification(Module.Expansion, Gravity.Warning, Subject.Template,
						message);
				Artifact artifact = new Artifact("End instruction");
				LocationImpl locationImpl = new LocationImpl(artifact, -1, endInstruction.getColumn(),
						endInstruction.getLine());
				ContextualNotification contextual = new ContextualNotification(noMoreFrameToDiscard, locationImpl);
				Notifications.getInstance().add(contextual);
			}
			else
			{
				frameStack.pop();
			}
		}
		else
		{
			Message message = new Message("Expecting " + frameName + ", not " + endInstruction.getLabel());
			Notification blockNamesNotCorresponding = new Notification(Module.Expansion, Gravity.Warning, Subject.Template,
					message);
			Artifact artifact = new Artifact("End instruction");
			LocationImpl locationImpl = new LocationImpl(artifact, -1, endInstruction.getColumn(),
					endInstruction.getLine());
			ContextualNotification contextual = new ContextualNotification(blockNamesNotCorresponding, locationImpl);
			Notifications.getInstance().add(contextual);
		}
	}

}
