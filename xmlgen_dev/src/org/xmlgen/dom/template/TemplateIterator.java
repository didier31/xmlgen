/*
 * 
 */
package org.xmlgen.dom.template;

import org.jdom2.Content;
import org.xmlgen.dom.DomIterator;

/**
 * Is a renaming of super class for type segregation in order to avoid confusion.
 */
public class TemplateIterator extends DomIterator
{
	/**
	 * Instantiates a new iterator.
	 * 
	 * @param node : position of the iterator.
	 */
	public TemplateIterator(Content node)
	{
		super(node);
	}
}
