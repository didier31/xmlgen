/*
 * 
 */
package org.xmlgen.dom;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Parent;
import org.xmlgen.template.dom.specialization.content.Element;

// TODO: Auto-generated Javadoc
/**
 * An iterator class for DOM 
 */
abstract public class DomIterator
{
	/**
	 * Instantiates a new iterator.
	 * 
	 * @param node : position of the iterator.
	 */
	protected DomIterator(Content node)
	{
		set(node);
	}

	/**
	 * Sets the position of the iterator.
	 *
	 * @param node : position of the iterator after call.
	 * 
	 */
	public void set(Content node)
	{
		this.node = node;
	}

	/**
	 * Gets the content at current position.
	 *
	 * @return the current content node.
	 */
	public Content current()
	{
		return node;
	}

	/**
	 * Fowards the position to the first child of the current position.
	 * 
	 */
	public void descendant()
	{
		if (node != null && node instanceof Element)
		{
			Element element = (Element) node;
			List<Content> children = element.getContent();
			if (children.isEmpty())
			{
				node = null;
			}
			else
			{
				node = children.get(0);
			}
		}
		else
		{
			node = null;
		}
	}

	/**
	 * Forward to the next sibling.
	 */
	public void sibling()
	{
		Parent parent = node.getParent();
		int idx = parent.indexOf(node);
		if (idx < parent.getContent().size() - 1)
		{
			node = parent.getContent(idx + 1);
		}
		else
		{
			node = null;
		}
	}
	

	/** 
	 * the node representing the current position
	 */
	private Content node;
}
