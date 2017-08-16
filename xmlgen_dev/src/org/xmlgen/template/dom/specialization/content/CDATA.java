package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.located.LocatedCDATA;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class CDATA extends LocatedCDATA implements Expandable
{
	public CDATA(String str, Xmlgen xmlgen)
	{
		super(str);
		this.xmlgen = xmlgen;
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		return Util.expand(this, expansionContext);
	}
	
	private Xmlgen xmlgen;
}
