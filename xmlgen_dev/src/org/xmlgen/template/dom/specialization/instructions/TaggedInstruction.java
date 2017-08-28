package org.xmlgen.template.dom.specialization.instructions;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.xmlgen.Xmlgen;

@SuppressWarnings("serial")
public class TaggedInstruction extends ExpansionInstruction
{
	protected TaggedInstruction(String data, String tag, int line, int column, Xmlgen xmlgen)
	{
		super(data, line, column, xmlgen);
		setLabel(tag);
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
		
		this.label = label != null ? label : "" ;
	}
	
	static protected String getLabel(TerminalNode terminalNode)
	{
		String labelStr = terminalNode != null ? terminalNode.getText() : "";			
		return labelStr;
	}
	
	private String label;
}
