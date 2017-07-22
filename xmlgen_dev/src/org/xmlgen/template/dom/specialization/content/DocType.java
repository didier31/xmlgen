package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.located.LocatedDocType;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class DocType extends LocatedDocType implements Expandable
{
	public DocType(String elementName)
	{
		super(elementName);
	}

	public DocType(String elementName, String systemID)
	{
		super(elementName, systemID);
	}
	
	public DocType(String elementName, String publicID, String systemID)
	{
		super(elementName, publicID, systemID);
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		return Util.expand(this, expansionContext);
	}

}
