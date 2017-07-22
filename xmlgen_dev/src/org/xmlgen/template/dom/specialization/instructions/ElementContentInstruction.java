/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.jdom2.Text;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.parser.pi.PIParser.ElementContentContext;

// TODO: Auto-generated Javadoc
/**
 * The Class ElementContentInstruction.
 */
public class ElementContentInstruction extends ContentInstruction
{
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		if (expansionContext.isExecuting())
		{
			Object computedValue = eval();
			if (computedValue != null)
			{
				Vector<Cloneable> contents = new Vector<Cloneable>(1);
				Text text = new Text(computedValue.toString());
				contents.add(text);
				return contents;
			}
			else
			{
				return new Vector<Cloneable>(0);
			}
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}

	/**
	 * Instantiates a new element content instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param parsedPI
	 *           the parsed PI
	 */
	protected ElementContentInstruction(ElementContentContext parsedPI, int line, int column)
	{
		super(parsedPI.expression().getText(), line, column);
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5491980343490480468L;
}
