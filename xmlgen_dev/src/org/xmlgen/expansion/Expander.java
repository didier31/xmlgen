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
import org.xmlgen.template.dom.DomIterator;
import org.xmlgen.template.dom.specialization.AttributeContentInstruction;
import org.xmlgen.template.dom.specialization.CapturesInstruction;
import org.xmlgen.template.dom.specialization.ElementContentInstruction;
import org.xmlgen.template.dom.specialization.EndInstruction;
import org.xmlgen.template.dom.specialization.ExpansionInstruction;
import org.xmlgen.template.dom.specialization.InsertInstruction;

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
	 *           the template document
	 * 
	 * @return the outgoing document
	 */
	public Document expand(Document template)
	{
		DomIterator templateIterator = new DomIterator(template.getRootElement());
		pushContext();
		Vector<Cloneable> node = expandDeeper(templateIterator);
		popContext();
		assert node.size() == 1 && node.get(0) instanceof Element;
		Document expandedDocument = new Document((Element) node.get(0), template.getDocType());
		return expandedDocument;
	}

	/**
	 * Expanding deeper consists in visiting in depth and then in width the DOM
	 * document node's subtree.
	 * 
	 * @param templateIterator
	 *           the iterator referencing the actual DOM node.
	 * 
	 * @return the expanded DOM forest.
	 */
	protected Vector<Cloneable> expandDeeper(DomIterator templateIterator)
	{
		Vector<Cloneable> allSibling = expandDeeperOnly(templateIterator);
		if (templateIterator.current() != null)
		{
			templateIterator.sibling();

			if (templateIterator.current() != null)
			{
				Vector<Cloneable> subForest = expandDeeper(templateIterator);
				allSibling.addAll(subForest);
			}
		}
		return allSibling;
	}

	/**
	 * Expands deeper only consists in visiting in depth only.
	 *
	 * @param templateIterator
	 *           the iterator referencing the actual DOM node.
	 * 
	 * @return the expanded DOM forest.
	 */
	protected Vector<Cloneable> expandDeeperOnly(DomIterator templateIterator)
	{
		Vector<Cloneable> allSibling;

		if (templateIterator.current() != null)
		{
			LocalContext context = getContext();
			boolean contextToPop;
			if (!context.getCapturesStack().isEmpty())
			{
				pushContext();
				contextToPop = true;
			}
			else
			{
				contextToPop = false;
			}
			allSibling = computeNode(templateIterator);
			DomIterator deeperIterator = new DomIterator(templateIterator.current());
			deeperIterator.descendant();
			if (deeperIterator.current() != null)
			{
				Vector<Cloneable> subTree = expandDeeper(deeperIterator);
				assert (allSibling.size() > 0);
				setChildren((Element) allSibling.get(0), subTree);
			}
			if (contextToPop)
			{
				popContext();
			}
		}
		else
		{
			allSibling = new Vector<Cloneable>(0);
		}
		return allSibling;
	}

	/**
	 * Compute the given node in parameter : - call the specialized method if the
	 * DOM node is a compiled xmlgen process instruction. - call the xmlgen pi
	 * parser before if not compiled yet. - or just clone the DOM node if it is
	 * ordinary one.
	 *
	 * @param templateIterator
	 *           the iterator referencing the actual DOM node.
	 * 
	 * @return the vector
	 */
	protected Vector<Cloneable> computeNode(DomIterator templateIterator)
	{
		Vector<Cloneable> expandedNode;
		Content root = templateIterator.current();
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
			expandedNode = doLoop(templateIterator);
		}
		else if (root instanceof EndInstruction)
		{
			expandedNode = new Vector<Cloneable>();
		}
		else if (root instanceof InsertInstruction)
		{
			if (expansionContext.getMother() == null)
			{
				// No recursion on the air => Insert is improper.
				// TODO: Notify a Warning
				expandedNode = new Vector<Cloneable>();
			}

			else
			{
				CapturesInstruction motherLoop = expansionContext.getMother();

				if (motherLoop.toIterate())
				{
					Context.getInstance().getFrameStack().pushR(new Frame(""));
					expandedNode = doIterations(new DomIterator(motherLoop));
					Context.getInstance().getFrameStack().pop();
					InsertInstruction insertInstruction = (InsertInstruction) root;
				}
				else
				{
					expandedNode = new Vector<Cloneable>();
				}
			}
		}
		else if (root instanceof LocatedProcessingInstruction
				&& ExpansionInstruction.isExpandPI((LocatedProcessingInstruction) root))
		{
			replaceInDomWithParcedPI(templateIterator);
			expandedNode = computeNode(templateIterator);
		}
		else
		{
			rootClone = root.clone();
			expandedNode = new Vector<Cloneable>(1);
			expandedNode.add(rootClone);
		}
		return expandedNode;
	}

	void replaceInDomWithParcedPI(DomIterator templateIterator)
	{
		assert (templateIterator.current() instanceof LocatedProcessingInstruction);

		LocatedProcessingInstruction pi = (LocatedProcessingInstruction) templateIterator.current();
		ExpansionInstruction ei = ExpansionInstruction.create(pi);
		templateIterator.set(ei);
	}

	/**
	 * Perform a capture xmlgen process instruction meaning to perform an
	 * iteration on declared data sources.
	 *
	 * @param templateIterator
	 *           the iterator referencing the capture instruction.
	 * 
	 * @return the expanded xml nodes.
	 */
	protected Vector<Cloneable> doLoop(DomIterator templateIterator)
	{
		assert (templateIterator.current() instanceof CapturesInstruction);

		CapturesInstruction iterativeInstruction = (CapturesInstruction) templateIterator.current();

		// Stack the template iterator at capture instruction position
		// to eventually be used deeper for an InsertInstruction
		getContext().getCapturesStack().push(iterativeInstruction);

		iterativeInstruction.initialize();

		Vector<Cloneable> expandedNodes = doIterations(templateIterator);

		EndInstruction endInstruction = (EndInstruction) templateIterator.current();
		if (endInstruction != null)
		{
			iterativeInstruction.terminate(endInstruction);
		}
		else
		{
			iterativeInstruction.terminate();
		}

		getContext().getCapturesStack().pop();
		return expandedNodes;
	}

	private Vector<Cloneable> doIterations(DomIterator templateIterator)
	{
		CapturesInstruction iterativeInstruction = (CapturesInstruction) templateIterator.current();
		DomIterator atFirstContentInLoop = new DomIterator(templateIterator.current());
		atFirstContentInLoop.sibling();

		Vector<Cloneable> expandedNodes = new Vector<Cloneable>(0);

		boolean endInstructionNotEncountered = true;

		boolean loopPassed = false;
		boolean isAnInterationToPerform = iterativeInstruction.iterate();
		while (isAnInterationToPerform || !loopPassed)
		{
			templateIterator.set(atFirstContentInLoop.current());

			endInstructionNotEncountered = true;

			while (templateIterator.current() != null && endInstructionNotEncountered)
			{
				Vector<Cloneable> localExpandedNodes = expandDeeperOnly(templateIterator);
				if (isAnInterationToPerform)
				{
					expandedNodes.addAll(localExpandedNodes);
				}
				Content computedContent = templateIterator.current();
				// NB: this test can be performed asap at this time only
				// because LocatedProcessingInstruction is casted in EndInstruction
				// by computeNode() from expandDeeperOnly's call and not before.
				if (computedContent instanceof EndInstruction)
				{
					EndInstruction endInstruction = (EndInstruction) computedContent;
					CapturesInstruction relatedCaptureInstruction = (CapturesInstruction) endInstruction
							.getRelatedIterativeInstruction();
					CapturesInstruction currentCaptureInstruction = expansionContext.getMother();
					if (relatedCaptureInstruction == null)
					{
						endInstruction.setRelatedIterativeInstruction(currentCaptureInstruction);
						endInstructionNotEncountered = false;
					}
					else
					{
						endInstructionNotEncountered = relatedCaptureInstruction != currentCaptureInstruction;
					}
				}

				if (endInstructionNotEncountered && templateIterator.current() != null)
				{
					templateIterator.sibling();
				}
			}
			if (isAnInterationToPerform)
			{
				isAnInterationToPerform = iterativeInstruction.iterate();
			}
			else
			{
				loopPassed = true;
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
	 *           the element content instruction
	 * @return the vector
	 */
	protected Vector<Cloneable> doContentInstruction(ElementContentInstruction elementContentInstruction)
	{
		Object computedValue = elementContentInstruction.eval();
		if (computedValue != null)
		{
			Vector<Cloneable> contents = new Vector<Cloneable>(1);
			if (computedValue instanceof Document)
			{
				contents.add((Document) computedValue);
			}
			else
			{
				Text text = new Text(computedValue.toString());
				contents.add(text);
			}
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

	protected LocalContext getContext()
	{
		return expansionContext.getContext();
	}

	protected void pushContext()
	{
		expansionContext.push();
	}

	protected void popContext()
	{
		expansionContext.pop();
	}

	private ExpansionContext expansionContext = new ExpansionContext();
}
