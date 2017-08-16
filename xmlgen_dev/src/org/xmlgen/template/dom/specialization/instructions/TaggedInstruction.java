package org.xmlgen.template.dom.specialization.instructions;

import org.xmlgen.Xmlgen;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

@SuppressWarnings("serial")
public class TaggedInstruction extends ExpansionInstruction
{
	protected TaggedInstruction(String data, TaggedContext taggedContext, int line, int column, Xmlgen xmlgen)
	{
		super(data, line, column, xmlgen);
		setLabel(taggedContext);
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
	
	/**
	 * Sets the label.
	 *
	 * @param label
	 *           the new label
	 */
	private void setLabel(String label)
	{
		this.label = label;
	}

	private void setLabel(TaggedContext taggedContext)
	{
		String label = taggedContext == null || taggedContext.label() == null ? "" : taggedContext.label().Label().getText();
		setLabel(label);
	}
	
	private String label;
}
