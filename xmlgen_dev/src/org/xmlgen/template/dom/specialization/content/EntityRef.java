package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.located.LocatedEntityRef;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class EntityRef extends LocatedEntityRef implements Expandable
{
	public EntityRef(String name, String publicID, String systemID)
	{
		super(name, publicID, systemID);
	}

	public EntityRef(String name)
	{
		super(name);
	}

	public EntityRef(String name, String systemID)
	{
		super(name, systemID);
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		return Util.expand(this, expansionContext);
	}
}
