package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jdom2.Content;
import org.xmlgen.Xmlgen;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.template.dom.specialization.content.Element;

@SuppressWarnings("serial")
public class InsertBlockInstruction extends InsertInstruction
{
	public InsertBlockInstruction(String pi, TerminalNode labelContext, int line, int column, Xmlgen xmlgen)
	{
		super(pi, line, column, xmlgen);
		setLabel(labelContext.getText());
	}

	@Override
	protected Element getBody()
	{
		if (body == null)
		{
			BeginInstruction insertedBegin = getBegin();
			if (insertedBegin != null)
			{
				Collection<Content> structureContent = structureOf(insertedBegin);
				String elementName = getLabel().replaceAll("[\\[\\]]", "");
				body = new Element(elementName, getXmlgen());
				body.addContent(structureContent);
			}
			else
			{
				body = null;
			}
		}
		return body;
	}

	protected BeginInstruction getBegin()
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
		BeginInstruction insertedBegin = expansionContext.getBegin(getLabel());
		if (insertedBegin == null)
		{
			// TODO Notify an error to user : no begin/end with this label
		}
		return insertedBegin;
	}

	protected void setLabel(String label)
	{
		this.label = label;
	}

	protected String getLabel()
	{
		return label;
	}

	private String label;
}
