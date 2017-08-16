package org.xmlgen.template.dom.specialization.content;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.located.LocatedText;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.Expandable;
import org.xmlgen.expansion.ExpansionContext;

@SuppressWarnings("serial")
public class Text extends LocatedText implements Expandable
{

	public Text(String str, Xmlgen xmlgen)
	{
		super(str);
		this.xmlgen = xmlgen;
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		ExpansionContext expansionContext = xmlgen.getExpansionContext();	
		 Matcher m = spaces.matcher(this.getText());
		 boolean spacesOnly = m.matches();
		if (spacesOnly)
		{
			return new Vector<Cloneable>(0);
		}
		else
		{
			return Util.expand(this, expansionContext);
		}
	}
	
	private Pattern spaces = Pattern.compile("\\p{Space}*");
	private Xmlgen xmlgen;
}
