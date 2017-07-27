package org.xmlgen.template.dom.specialization.instructions;

import java.util.Stack;
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
		String guardStr;
		if (guardContext != null)
		{
			ExpressionContext expressionContext = guardContext.expression();
			guardStr = getText(this.getData(), expressionContext);
		}
		else
		{
			guardStr = "true";
		}			
		
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
				String queryToParse = getText(this.getData(), expression);
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
	public Vector<Cloneable> doExpandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		Object guardResult = eval(guard);
		boolean executionGranted = (guardResult != null && (guardResult instanceof Boolean && (Boolean) guardResult));
		if (executionGranted)
		{
			setDefinitions();
		}
		else
		{
			disableExecution();
			if (!(guardResult instanceof Boolean))
			{
				// TODO: Notify user that guard has not the correct type 
			}
		}
		setFinished();
		return new Vector<Cloneable>(0);
	}

	@Override
	protected void createState(ExpansionContext expansionContext)
	{
		State currentState = new State(expansionContext);
		states.push(currentState);		
	}

	@Override
	protected State currentState()
	{
		return states.peek();
	}

	@Override
	protected void deleteState()
	{
		states.pop();
	}
	
	private Stack<State> states = new Stack<State>();
}
