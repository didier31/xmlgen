package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import java.util.List;
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
		TerminalNode identContext = insertInstruction.Ident();
		blockId = identContext != null ? identContext.getText() : null;
		if (blockId != null)
		{
			BeginInstruction insertedBegin = getBegin(blockId);
			setInsertedBlock(insertedBegin);
		}
	}
	
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		Xmlgen xmlgen = getXmlgen();
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		
		if (expansionContext.isExecuting())
		{
			/**
			 * structureElement == null means, it is the label that sets it at expansion-time. 
			 */
			if (insertedBlock == null)
			{
				BeginInstruction insertedBegin = getBegin(expansionContext);
				setInsertedBlock(insertedBegin);
			}
			
			List<Content> block = insertedBlock.getContent(); 
			
			BeginInstruction beginInstruction = (BeginInstruction) block.get(0);
			beginInstruction.newInstance();
			
			TemplateIterator recursiveIt = new TemplateIterator(beginInstruction);
			
			FrameStack frameStack = getXmlgen().getFrameStack();
			
			frameStack.pushNumbering();
			
			expansionContext.incInsertInProgressCount();
			Vector<Cloneable> expanded = insertedBlock.expandMySelf(recursiveIt, false);
			expansionContext.decInsertInProgressCount();
			
			frameStack.popNumbering();
			
			return expanded;
		}
		else
		{
			return new Vector<Cloneable>(0);
		}
	}

	protected BeginInstruction getBegin(String blockId)
	{ 
		assert(blockId != null);
		BeginInstruction insertedBegin;
		boolean constainsKey = getXmlgen().containsBlock(blockId);
		if (constainsKey)
		{
			insertedBegin = getXmlgen().getBlock(blockId);
		}
		else
		{
		   // TODO Notify an error to user 
			insertedBegin = null; 
		}
		return insertedBegin;
	}
	
	protected BeginInstruction getBegin(ExpansionContext expansionContext)
	{
		BeginInstruction insertedBegin;		
		{
			insertedBegin = expansionContext.getBegin(label);
			if (insertedBegin == null)
			{
				// TODO Notify an error to user : no begin/end with this label
			}
		}

		return insertedBegin;
	}
	
	protected void setInsertedBlock(BeginInstruction insertedBegin)
	{
		assert(insertedBegin != null);
		Collection<Content> structureContent = structureOf(insertedBegin);
		insertedBlock = new Element("dummy", getXmlgen());
		insertedBlock.addContent(structureContent);
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
	private String blockId = null;
	private Element insertedBlock = null;
}
