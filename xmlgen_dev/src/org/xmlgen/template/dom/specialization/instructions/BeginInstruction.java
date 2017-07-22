package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.parser.pi.PIParser.BeginContext;
import org.xmlgen.parser.pi.PIParser.DefinitionContext;
import org.xmlgen.parser.pi.PIParser.DefinitionsContext;
import org.xmlgen.parser.pi.PIParser.ExpressionContext;
import org.xmlgen.parser.pi.PIParser.GuardContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

@SuppressWarnings("serial")
public class BeginInstruction extends StructuralInstruction
{

	private Vector<AstResult> definitionsQueries;
	private Vector<String> datasourcesIDs;
	private AstResult guard;

	protected BeginInstruction(String pi, BeginContext beginContext, int line, int column)
	{
		super(pi, (TaggedContext) beginContext.getParent(), line, column);
		initFields(beginContext, line, column);
	}

	private void initFields(BeginContext beginContext, int line, int column)
	{
		GuardContext guardContext = beginContext.guard();
		String guardStr = guardContext != null ? guardContext.expression().getText() : "true"; 
		
		guard = InstructionParser.parseQuery(guardStr, line, column);
		
		
		DefinitionsContext definitions = beginContext.definitions();
		final int definitionsCount = definitions == null ? 0 : definitions.definition().size();
		datasourcesIDs = new Vector<String>(definitionsCount);
		definitionsQueries = new Vector<AstResult>(definitionsCount);

		if (definitions != null)
		{
			for (DefinitionContext definition : definitions.definition())
			{
				ExpressionContext expression = definition.expression();
				String queryToParse = this.getData().substring(expression.start.getStartIndex(), expression.stop.getStopIndex()+1);
				AstResult parsedQuery = InstructionParser.parseQuery(queryToParse, line, column);
				String id = definition.dataID().getText();
				datasourcesIDs.add(id);
				definitionsQueries.add(parsedQuery);
			}
		}
	}

	protected void setDefinitions()
	{
		// Initializes references in the just new created frame in stack
		int i = 0;
		for (AstResult definitionQuery : definitionsQueries)
		{
			Object result = null;
			if (definitionQuery.getErrors().isEmpty())
			{
				result = eval(definitionQuery);
			}
			addToCurrentFrame(datasourcesIDs.get(i), result);
			i++;
		}
	}

	protected void pushDatasourcesContext(Frame newFrame)
	{
		FrameStack frameStack = Context.getInstance().getFrameStack();
		frameStack.pushR(newFrame);
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		if (expansionContext.isExecuting())
		{
			Object guardResult = eval(guard);
			boolean executionGranted = (guardResult != null || (guardResult instanceof Boolean && (Boolean) guardResult));
			if (executionGranted)
			{
				initialize(expansionContext);
				setDefinitions();
			}
			else
			{
				disableExecution();
			}
		}
		return new Vector<Cloneable>(0);
	}

	@Override
	public void end(ExpansionContext expansionContext)
	{
		expansionContext.getContext().getStructuresStack().pop();
		enableExecution();
	}
}