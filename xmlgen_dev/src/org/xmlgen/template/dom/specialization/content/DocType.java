package org.xmlgen.template.dom.specialization.content;

import org.jdom2.located.LocatedDocType;

@SuppressWarnings("serial")
public class DocType extends LocatedDocType
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
}
