/*
 * 
 */
package org.xmlgen.template.dom.specialization;

import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.ElementContentContext;

// TODO: Auto-generated Javadoc
/**
 * The Class ElementContentInstruction.
 */
public class ElementContentInstruction extends ContentInstruction
{

	/**
	 * Instantiates a new element content instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param parsedPI
	 *           the parsed PI
	 */
	protected ElementContentInstruction(LocatedProcessingInstruction pi, ElementContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5491980343490480468L;
}
