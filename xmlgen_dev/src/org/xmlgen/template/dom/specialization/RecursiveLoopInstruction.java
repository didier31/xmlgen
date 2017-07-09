package org.xmlgen.template.dom.specialization;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Parent;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.parser.pi.PIParser.BeginContext;

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
	
	protected RecursiveLoopInstruction(LocatedProcessingInstruction pi, BeginContext beginContext)
	{
		super(pi, beginContext);		
	}

	@Override
	public void terminate(Content lastInstruction)
	{
		super.terminate();		
	}

	private CapturesInstruction motherLoop = null;
	private static final long serialVersionUID = -7660528287075606697L;


}
