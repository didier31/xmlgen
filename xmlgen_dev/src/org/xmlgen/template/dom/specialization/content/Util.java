package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;

import org.jdom2.Content;
import org.xmlgen.expansion.ExpansionContext;

public class Util
{
	static public Vector<Cloneable> expand(Content content, ExpansionContext expansionContext)
	{ 
		Vector<Cloneable> expanded = new Vector<Cloneable>(1);
		if (expansionContext.isExecuting())
		{
			Content cloned = content.clone();
			expanded.add(cloned);
		}
		else
		{
			expanded = new Vector<Cloneable>(0);
		}		
		return expanded;
	}
}
