package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import java.util.Stack;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.jdom2.Content;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.parser.pi.PIParser.InsertContext;
import org.xmlgen.template.dom.specialization.content.Element;

@SuppressWarnings("serial")
public class InsertInstruction extends ExpansionInstruction
{	
	protected InsertInstruction(String pi, InsertContext insertInstruction, int line, int column, Xmlgen xmlgen)
	{
		super(pi, line, column, xmlgen);
		TerminalNode labelContext = insertInstruction.Label();
		label = labelContext != null ? labelContext.getText() : null;
	}
	
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		Xmlgen xmlgen = getXmlgen();
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		
		super.expandMySelf(it);
		
		if (expansionContext.isExecuting())
		{
			BeginInstruction insertBlock = expansionContext.getCurrentBegin(label);
			
			if (insertBlock == null)
			{
				// TODO Notify an error to user : no begin/end with this label 
			}
			
			Collection<Content> structureContent = structureOf(insertBlock);
			
			Element element = new Element("dummy", xmlgen);
			element.addContent(structureContent);
			
			TemplateIterator recursiveIt = new TemplateIterator(insertBlock);
			
			FrameStack frameStack = getXmlgen().getFrameStack();
			
			frameStack.pushNumbering();
			
			Vector<Cloneable> expanded = element.expandMySelf(recursiveIt, false);
			
			frameStack.popNumbering();
			
			return expanded;
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}
	
	protected Collection<Content> structureOf(StructuralInstruction structuralInstruction)
	{
		TemplateIterator structureIt = new TemplateIterator(structuralInstruction);
		Stack<StructuralInstruction> structures = new Stack<StructuralInstruction>();
		Vector<Content> structure = new Vector<Content>(0); 
		do
		{	
			Content templaceContent = structureIt.current(); 
			Content content = templaceContent.clone();
			structure.addElement(content);
			if (content instanceof StructuralInstruction)
			{
				StructuralInstruction structuralInstr = (StructuralInstruction) content;
				structures.push(structuralInstr);
			}
			else if (content instanceof EndInstruction)
			{
				EndInstruction endInstruction = (EndInstruction) content;
				if (structures.isEmpty())
				{
					// TODO: Notify if necessary the user
				}
				else
				{
					StructuralInstruction relatedStructuralInstruction = structures.peek();
					String startLabel = relatedStructuralInstruction.getLabel();
					String endLabel = endInstruction.getLabel();
					if (!startLabel.equals(endLabel))
					{
						// TODO : Eventually notify the user if not ever done.
					}
					else
					{
						structures.pop();
					}					
				}
			}		
		structureIt.sibling();
		}
		while (structureIt.current() != null && !structures.isEmpty());
		return structure;
	}
	
	private String label = null;
}
