/*
 * 
 */
package org.xmlgen.template.dom.specialization;

import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.EndContext;

// TODO: Auto-generated Javadoc
/**
 * The Class EndInstruction.
 */
public class EndInstruction extends ExpansionInstruction
{

	/**
	 * Instantiates a new end instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param endInstruction
	 *           the end instruction
	 */
	protected EndInstruction(LocatedProcessingInstruction pi, EndContext endInstruction)
	{
		super(pi);
		setLabel(endInstruction.label().Ident().getText());
	}

	/**
	 * Instantiates a new end instruction.
	 *
	 * @param label
	 *           the label
	 * @param target
	 *           the target
	 * @param data
	 *           the data
	 */
	public EndInstruction(String label, String target, String data)
	{
		super(target, data);
		setLabel(label);
	}

	/**
	 * Sets the label.
	 *
	 * @param label
	 *           the new label
	 */
	protected void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/** The label. */
	private String label;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8264616535449826911L;
}
