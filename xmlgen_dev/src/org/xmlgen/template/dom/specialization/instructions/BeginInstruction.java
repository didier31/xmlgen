package org.xmlgen.template.dom.specialization.instructions;

import java.util.Stack;
import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.BeginContext;
import org.xmlgen.parser.pi.PIParser.DefinitionContext;
import org.xmlgen.parser.pi.PIParser.DefinitionsContext;
import org.xmlgen.parser.pi.PIParser.ExpressionContext;
import org.xmlgen.parser.pi.PIParser.GuardContext;

@SuppressWarnings("serial")
public class BeginInstruction extends StructuralInstruction
{

	private Vector<AstResult> definitionsQueries;
	private Vector<String> datasourcesIDs;
	private AstResult guard;

	protected BeginInstruction(String pi, BeginContext beginContext, int line, int column, Xmlgen xmlgen)
	{
		super(pi, getLabel(beginContext.Label()), line, column, xmlgen);
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
			if (guardStr.equals(""))
			{
				guardStr = "false";
			}
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

	@Override
	protected void pushDatasourcesContext(Frame newFrame)
	{
		FrameStack frameStack = getXmlgen().getFrameStack();
		frameStack.push(newFrame);
	}

	@Override
	public Vector<Cloneable> doExpandMySelf(TemplateIterator it)
	{
		Object guardResult = eval(guard);
		boolean executionGranted = (guardResult != null && (guardResult instanceof Boolean && (Boolean) guardResult));
		if (executionGranted)
		{
			setDefinitions();
			setExecuted();
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
		traceEntry(executionGranted);
		return new Vector<Cloneable>(0);
	}

	protected void traceEntry(boolean executionGranted)
	{
		if (getXmlgen().getContext().isTrace())
		{
			final String guardPassed = "guard " + (executionGranted ? "passed" : "not passed");
			Message message = new Message(" begin: " + guardPassed);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource, message);
			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getLine(), getColumn());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications notifications = getXmlgen().getNotifications();
			notifications.add(contextual);
		}
	}

	@Override
	protected void createState(ExpansionContext expansionContext)
	{
		State currentState = new State();
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
	
	@Override
	protected boolean thereIsNoState()
	{
		return states.isEmpty();
	}
	
	private Stack<State> states = new Stack<State>();
}
