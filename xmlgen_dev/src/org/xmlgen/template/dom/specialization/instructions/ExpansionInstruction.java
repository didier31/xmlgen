/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.acceleo.query.ast.Error;
import org.eclipse.acceleo.query.runtime.EvaluationResult;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.acceleo.query.runtime.IQueryEvaluationEngine;
import org.eclipse.acceleo.query.runtime.QueryEvaluation;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.uml2.uml.UMLPackage;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.FrameStack;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.AttributeContentContext;
import org.xmlgen.parser.pi.PIParser.BeginContext;
import org.xmlgen.parser.pi.PIParser.CapturesContext;
import org.xmlgen.parser.pi.PIParser.ElementContentContext;
import org.xmlgen.parser.pi.PIParser.EndContext;
import org.xmlgen.parser.pi.PIParser.ExpandContext;
import org.xmlgen.parser.pi.PIParser.InsertContext;
import org.xmlgen.parser.pi.PIParser.UserServiceContext;
import org.xmlgen.template.dom.specialization.content.ProcessingInstruction;

// TODO: Auto-generated Javadoc
/**
 * The Class ExpansionInstruction.
 */
abstract public class ExpansionInstruction extends ProcessingInstruction
{

	/** The Constant piMarker. */
	public final static String piMarker = "xmlgen";

	/**
	 * Creates the.
	 *
	 * @param pi
	 *           the pi
	 * @return the expansion instruction
	 */
	public static ExpansionInstruction create(String pi, int line, int column, Xmlgen xmlgen)
	{
		ParserRuleContext instruction = InstructionParser.parse(pi, line, column, xmlgen);
		ExpansionInstruction domInstruction = null;
		if (instruction instanceof CapturesContext)
		{
			CapturesContext capturesInstruction = (CapturesContext) instruction;
			domInstruction = new CapturesInstruction(pi, capturesInstruction, line, column, xmlgen);
		}
		else if (instruction instanceof BeginContext)
		{
			BeginContext beginContext = (BeginContext) instruction;
			return new BeginInstruction(pi, beginContext, line, column, xmlgen);
		}
		else if (instruction instanceof EndContext)
		{
			EndContext endContext = (EndContext) instruction;
			domInstruction = new EndInstruction(pi, endContext, line, column, xmlgen);
		}
		else if (instruction instanceof AttributeContentContext)
		{
			AttributeContentContext attributeContentInstruction = (AttributeContentContext) instruction;
			domInstruction = new AttributeContentInstruction(pi, attributeContentInstruction, line, column, xmlgen);
		}
		else if (instruction instanceof ElementContentContext)
		{
			ElementContentContext elementContentInstruction = (ElementContentContext) instruction;
			domInstruction = new ElementContentInstruction(pi, elementContentInstruction, line, column, xmlgen);
		}
		else if (instruction instanceof InsertContext)
		{
			InsertContext insertInstruction = (InsertContext) instruction; 
			domInstruction = new InsertInstruction(pi, insertInstruction, line, column, xmlgen);
		}
		else if (instruction instanceof UserServiceContext)
		{
			UserServiceContext userServiceContext = (UserServiceContext) instruction;
			domInstruction = new LoadInstruction(pi, userServiceContext, line, column, xmlgen);
		}
		else if (instruction instanceof ExpandContext)
		{
			domInstruction = new ExpandInstruction(pi, line, column, xmlgen);
		}
		else
		{
			domInstruction = null;
			assert(false);
		}
		return domInstruction;
	}

	/**
	 * Instantiates a new expansion instruction.
	 *
	 * @param pi
	 *           the pi
	 */
	protected ExpansionInstruction(String data, int line, int column, Xmlgen xmlgen)
	{
		super(piMarker, data, xmlgen);
		setLine(line);
		setColumn(column);
	}

	/**
	 * Instantiates a new expansion instruction.
	 *
	 * @param target
	 *           the target
	 * @param data
	 *           the data
	 */
	protected ExpansionInstruction(String target, String data, Xmlgen xmlgen)
	{
		super(target, data, xmlgen);
	}

	static
	{
		InstructionParser.getQueryEnv().registerEPackage(UMLPackage.eINSTANCE);
	}

	/**
	 * Notify errors.
	 *
	 * @param compiledQuery
	 *           the compiled query
	 */
	protected void notifyErrors(AstResult compiledQuery)
	{
		List<Error> errors = compiledQuery.getErrors();
		for (Error error : errors)
		{
			Message message = new Message(error.toString());
			Notification notification = new Notification(Module.Parser, Gravity.Fatal, Subject.Template, message);
			Artifact artifact = new Artifact("Xml template");
			LocationImpl location = new LocationImpl(artifact, -1, getColumn(), getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			getXmlgen().getNotifications().add(contextual);
		}
	}

	/**
	 * Eval.
	 *
	 * @param parsedQuery
	 *           the parsed query
	 * @return the object
	 */
	protected Object eval(AstResult parsedQuery)
	{
		if (parsedQuery != null && parsedQuery.getErrors().isEmpty())
		{
			IQueryEvaluationEngine engine = QueryEvaluation.newEngine(InstructionParser.getQueryEnv());
			FrameStack frameStack = getXmlgen().getFrameStack();
			EvaluationResult evaluationResult = engine.eval(parsedQuery, frameStack);
			Object result = evaluationResult.getResult();

			if (evaluationResult.getDiagnostic() != null)
			{
				Diagnostic diagnostic = evaluationResult.getDiagnostic();
				int severity = diagnostic.getSeverity();
				if (severity != Diagnostic.OK)
				{
					notifyErrors(diagnostic);
					if (severity == Diagnostic.WARNING || severity != Diagnostic.INFO)
					{
						return result;
					}
					else
					{
						return null;
					}
				}
				else
				{
					return result;
				}
			}
			else
			{
				return result;
			}
		}
		else
		{
			return null;
		}
	}
	
	static protected String getText(String pi, ParserRuleContext parserRuleContext)
	{
		String text;
		if (parserRuleContext != null)
		{
			text = pi.substring(parserRuleContext.start.getStartIndex(), parserRuleContext.stop.getStopIndex() + 1);
		}
		else
		{
			text = "";
		}
		return text;
	}

	/**
	 * Notify errors.
	 *
	 * @param diagnostic
	 *           the diagnostic
	 */
	protected void notifyErrors(Diagnostic diagnostic)
	{
		assert (diagnostic != null);
		String messageString = diagnostic.getMessage();
		if (messageString != null)
		{
			int severity = diagnostic.getSeverity();
			assert (severity != Diagnostic.OK);
			Message message = new Message(messageString);
			Gravity gravity;
			switch (severity)
			{
			case Diagnostic.CANCEL:
				gravity = Gravity.Fatal;
				break;

			case Diagnostic.ERROR:
				gravity = Gravity.Error;
				break;

			case Diagnostic.WARNING:
				gravity = Gravity.Warning;
				break;

			case Diagnostic.INFO:
				gravity = Gravity.Information;
				break;

			default:
				assert (false);
				gravity = Gravity.Fatal;
			}
			Notification notification = new Notification(Module.Parser, gravity, Subject.Template, message);
			LocationImpl location = new LocationImpl(null, -1, getColumn(), getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			getXmlgen().getNotifications().add(contextual);
		}
		for (Diagnostic subDiagnostic : diagnostic.getChildren())
		{
			notifyErrors(subDiagnostic);
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8385870921899134393L;

}
