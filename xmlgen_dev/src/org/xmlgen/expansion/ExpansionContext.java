package org.xmlgen.expansion;

import java.util.Stack;

import org.xmlgen.template.dom.specialization.instructions.BeginInstruction;
import org.xmlgen.template.dom.specialization.instructions.StructuralInstruction;

@SuppressWarnings("serial")
public class ExpansionContext extends Stack<LocalContext>
{
	public boolean isExecuting()
	{
		StructuralInstruction structureInstruction = getMotherStructure();
		if (structureInstruction == null)
		{
			return true;
		}
		else
		{
			boolean isExecuting = structureInstruction.isExecuting();
			return isExecuting;
		}
	}
	
	public StructuralInstruction getRelatedStructure()
	{
		Stack<StructuralInstruction> currentStructuresStack = getContext().getStructuresStack();
		if (currentStructuresStack.isEmpty())
		{
			return null;
		}
		else
		{
			return getContext().getStructuresStack().peek();
		}
	}	
	
	public BeginInstruction getCurrentBegin()
	{
		int i = size() - 1;
		BeginInstruction foundInstr = null;
		while (i >= 0 && foundInstr == null)
		{
			Stack<StructuralInstruction> structuresStack = elementAt(i).getStructuresStack();
			int j = structuresStack.size() - 1;
			while (j > 0 && foundInstr == null)
			{			
				try
				{
					foundInstr = (BeginInstruction) structuresStack.elementAt(j);
				}
				catch (ClassCastException e)
				{}
				j--;
			}
			i--;
		}
	return foundInstr;
	}	
	
	public StructuralInstruction getMotherStructure()
	{
		int i = size() - 1;
		StructuralInstruction foundInstr = null;
		while (i >= 0 && (foundInstr == null))
		{
			Stack<StructuralInstruction> structuresStack = elementAt(i).getStructuresStack();
			if (!structuresStack.isEmpty())
			{
				foundInstr = structuresStack.peek();
			}
			i--;
		}
	return foundInstr;		
	}
	
	public void push()
	{
		push(new LocalContext());
	}
	
	public LocalContext getContext()
	{
		return peek();
	}
}
