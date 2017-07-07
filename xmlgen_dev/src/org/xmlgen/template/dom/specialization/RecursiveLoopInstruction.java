package org.xmlgen.template.dom.specialization;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Parent;
import org.jdom2.located.LocatedProcessingInstruction;

public class RecursiveLoopInstruction extends IterativeInstruction
{
	/**
	 * 
	 */
	public void setMotherInstruction(CapturesInstruction motherLoop)
	{
		this.motherLoop = motherLoop;
		// Duplicate parent for its sons
		Parent originalParent = motherLoop.getParent();
		Parent duplicatedParent = (Parent) originalParent.clone();
		setParent(duplicatedParent);
		int idx = originalParent.indexOf(motherLoop);
		List<Content> children = duplicatedParent.getContent();
		this.setParent(null);
		children.set(idx, this);
	}
	
	@Override
	public boolean iterate()
	{
		return motherLoop.iterate();
	}
	
	protected RecursiveLoopInstruction(LocatedProcessingInstruction pi)
	{
		super(pi);		
	}
	
	@Override
	protected String getLabel()
	{
		return "";
	}

	@Override
	public void terminate(Content lastInstruction)
	{
		super.terminate();		
	}

	private CapturesInstruction motherLoop = null;
	private static final long serialVersionUID = -7660528287075606697L;


}
