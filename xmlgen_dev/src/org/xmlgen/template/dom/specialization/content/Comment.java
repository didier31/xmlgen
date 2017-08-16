package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.located.LocatedComment;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class Comment extends LocatedComment implements Expandable
{
	public Comment(String text, Xmlgen xmlgen)
	{
		super(text);
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
