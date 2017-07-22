package org.xmlgen.expansion;

import java.util.Stack;

import org.xmlgen.template.dom.specialization.instructions.StructuralInstruction;

public class LocalContext
{
	public Stack<StructuralInstruction> getStructuresStack()
	{
		return structuresStack;
	}

	private Stack<StructuralInstruction> structuresStack = new Stack<StructuralInstruction>();	
}
