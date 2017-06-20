/*
 * 
 */
package org.xmlgen.template.dom;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;

// TODO: Auto-generated Javadoc
/**
 * The Class Iterator.
 */
public class Iterator
{

	/** The node. */
	Content node;

	/**
	 * Instantiates a new iterator.
	 *
	 * @param node
	 *           the node
	 */
	public Iterator(Content node)
	{
		set(node);
	}

	/**
	 * Sets the.
	 *
	 * @param node
	 *           the node
	 */
	public void set(Content node)
	{
		this.node = node;
	}

	/**
	 * Current.
	 *
	 * @return the content
	 */
	public Content current()
	{
		return node;
	}

	/**
	 * Descendant.
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
	 * Sibling.
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
}
