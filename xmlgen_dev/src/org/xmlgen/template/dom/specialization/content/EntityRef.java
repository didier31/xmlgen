package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.located.LocatedEntityRef;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class EntityRef extends LocatedEntityRef implements Expandable
{
	public EntityRef(String name, String publicID, String systemID, Xmlgen xmlgen)
	{
		super(name, publicID, systemID);
		this.xmlgen = xmlgen;
	}

	public EntityRef(String name, Xmlgen xmlgen)
	{
		super(name);
		this.xmlgen = xmlgen;
	}

	public EntityRef(String name, String systemID, Xmlgen xmlgen)
	{
		super(name, systemID);
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
