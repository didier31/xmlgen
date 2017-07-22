package org.xmlgen.expansion;

import java.util.Vector;

import org.xmlgen.dom.template.TemplateIterator;

public interface Expandable
{
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext); 
}
