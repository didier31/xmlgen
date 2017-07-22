package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.DocType;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class Template extends org.jdom2.Document
{
	public Template(Element rootElement, DocType docType)
	{
		super(rootElement, docType);
	}

	public Template(Element rootElement, DocType docType, String baseURI)
	{
		super(rootElement, docType, baseURI);
	}

	public Template(Element rootElement)
	{
		super(rootElement);
	}

	public Document expand()
	{
		expansionContext.push();
		Content rootContent = getRootElement();
		Expandable expandableContent = (Expandable) rootContent;
		TemplateIterator it = new TemplateIterator(rootContent);
		Vector<Cloneable> node = expandableContent.expandMySelf(it, expansionContext);
		expansionContext.pop();
		assert node.size() == 1 && node.get(0) instanceof Element;
		Document expandedDocument = new Document((Element) node.get(0), getDocType());
		return expandedDocument;
	}

	private ExpansionContext expansionContext = new ExpansionContext();
}
