package org.xmlgen.expansion;

import java.util.Stack;

import org.xmlgen.template.dom.specialization.CapturesInstruction;

public class LocalContext
{
	public Stack<CapturesInstruction> getCapturesStack()
	{
		return capturesStack;
	}

	private Stack<CapturesInstruction> capturesStack = new Stack<CapturesInstruction>();
}
