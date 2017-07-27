package org.xmlgen.template.dom.specialization.content;

import java.util.Stack;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Namespace;
import org.jdom2.located.LocatedElement;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;
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
		Vector<Cloneable> expanded = expandMySelf(it, expansionContext, true);
		return expanded;
	}

	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext, boolean regular)
	{
		if (expansionContext.isExecuting())
		{
			Vector<Cloneable> thisClone = Util.expand(this, expansionContext);
			Vector<Cloneable> expanded = new Vector<Cloneable>(10);
			TemplateIterator childrenIt = new TemplateIterator(this);
			childrenIt.descendant();
			expansionContext.push();
			Stack<StructuralInstruction> structureStack = expansionContext.getContext().getStructuresStack();
			boolean isFinished;
			/*
			 * Do while there are unfinished iterative statements
			 */
			do
			{
				/*
				 * Walks through element's children, expanding them
				 */
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

				/*
				 * Walks through the last structural instructions of the context,
				 * starting by the innermost to outermost and terminate the finished
				 * ones. and terminates them. Stops when the first unfinished one.
				 */
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

			/*
			 * Now the current local expansion context is accomplished, pop it
			 */
			expansionContext.pop();
			if (regular)
			{
				/*
				 * attach expanded content to this cloned element
				 */
				setChildren((Element) thisClone.get(0), expanded);
				return thisClone;
			}
			else
			{
				return expanded;
			}
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
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
}
