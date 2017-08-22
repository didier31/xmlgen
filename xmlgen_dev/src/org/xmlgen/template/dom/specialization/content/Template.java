package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class Template extends org.jdom2.Document
{
	public Template(Element rootElement, org.jdom2.DocType docType)
	{
		super(rootElement, docType);
	}

	public Template(Element rootElement, org.jdom2.DocType docType, String baseURI)
	{
		super(rootElement, docType, baseURI);
	}

	public Template(Element rootElement)
	{
		super(rootElement);
	}

	public Document expand(Xmlgen xmlgen)
	{
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		
		expansionContext.push();
		Content rootContent = getRootElement();
		Expandable expandableContent = (Expandable) rootContent;
		TemplateIterator it = new TemplateIterator(rootContent);
		Vector<Cloneable> node = expandableContent.expandMySelf(it);
		expansionContext.pop();
		
		assert node.size() == 1 && node.get(0) instanceof Element;
		Element root = (Element) node.get(0);
		Document expandedDocument = new Document(root);
		DocType docType = (DocType) getDocType().clone();
		expandedDocument.setDocType(docType);
		
		return expandedDocument;
	}	
}
