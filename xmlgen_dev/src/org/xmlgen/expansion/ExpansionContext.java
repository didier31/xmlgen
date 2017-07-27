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

	public BeginInstruction getCurrentBegin(String label)
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
					if (label != null)
					{
						String instrLabel = foundInstr.getLabel(); 
						if (!instrLabel.equals(label))
						{
							foundInstr = null;
						}
					}
				}
				catch (ClassCastException e)
				{
				}
				j--;
			}
			i--;
		}
		return foundInstr;
	}

	public StructuralInstruction getMotherStructure()
	{
		LocalContext localContext = getContext();
		Stack<StructuralInstruction> structuresStack = localContext.getStructuresStack();
		if (structuresStack.isEmpty())
		{
			return null;
		}
		else
		{
			StructuralInstruction structuralInstruction = structuresStack.peek();
			return structuralInstruction;
		}
	}

	protected StructuralInstruction getGrandmotherStructure()
	{
		LocalContext localContext = getContext();
		Stack<StructuralInstruction> structuresStack = localContext.getStructuresStack();
		StructuralInstruction structuralInstruction;
		
		if (structuresStack.size() < 2)
		{
			structuralInstruction = null;
		}
		else
		{
			structuralInstruction = structuresStack.elementAt(structuresStack.size() - 2);
		}
		return structuralInstruction;
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
