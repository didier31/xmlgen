package org.xmlgen.expansion;

import java.util.Stack;

import org.xmlgen.template.dom.specialization.CapturesInstruction;

@SuppressWarnings("serial")
public class ExpansionContext extends Stack<Context>
{
	public CapturesInstruction getMother()
	{
		int i = size() - 1;
		CapturesInstruction foundInstr = null;
		while (i >= 0 && foundInstr == null)
		{
			Stack<CapturesInstruction> captureStack = elementAt(i).getCapturesStack();
			if (captureStack.size() > 0)
			{
				foundInstr = captureStack.peek();				
			}
			i--;
		}
	return foundInstr;
	}	

	public void push()
	{
		push(new Context());
	}
	
	public Context getContext()
	{
		return peek();
	}
}
