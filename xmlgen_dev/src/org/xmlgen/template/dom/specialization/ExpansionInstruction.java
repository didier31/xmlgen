/*
 * 
 */
package org.xmlgen.template.dom.specialization;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.acceleo.query.ast.Error;
import org.eclipse.acceleo.query.runtime.EvaluationResult;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.acceleo.query.runtime.impl.QueryEvaluationEngine;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.uml2.uml.UMLPackage;
import org.xmlgen.context.Context;
import org.xmlgen.context.FrameStack;
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
import org.xmlgen.parser.pi.PIParser.AttributeContentContext;
import org.xmlgen.parser.pi.PIParser.CapturesContext;
import org.xmlgen.parser.pi.PIParser.ElementContentContext;
import org.xmlgen.parser.pi.PIParser.EndContext;
import org.xmlgen.parser.pi.PIParser.InsertContext;
import org.jdom2.Element;
import org.jdom2.ProcessingInstruction;
import org.jdom2.located.LocatedProcessingInstruction;

// TODO: Auto-generated Javadoc
/**
 * The Class ExpansionInstruction.
 */
abstract public class ExpansionInstruction extends LocatedProcessingInstruction
{

	/** The Constant piMarker. */
	public final static String piMarker = "xmlgen";

	/**
	 * Test is ProcessingInstruction is an expand PI.
	 *
	 * @param pi
	 *           the pi
	 * @return true, if is expand PI
	 */
	static public boolean isExpandPI(ProcessingInstruction pi)
	{
		return piMarker.compareToIgnoreCase(pi.getTarget()) == 0;
	}

	/**
	 * Creates the.
	 *
	 * @param pi
	 *           the pi
	 * @return the expansion instruction
	 */
	public static ExpansionInstruction create(LocatedProcessingInstruction pi)
	{
		assert isExpandPI(pi);
		ParserRuleContext instruction = InstructionParser.parse(pi);
		ExpansionInstruction domInstruction = null;
		if (instruction instanceof CapturesContext)
		{
			CapturesContext capturesInstruction = (CapturesContext) instruction;
			domInstruction = new CapturesInstruction(pi, capturesInstruction);
		}
		else if (instruction instanceof AttributeContentContext)
		{
			AttributeContentContext attributeContentInstruction = (AttributeContentContext) instruction;
			domInstruction = new AttributeContentInstruction(pi, attributeContentInstruction);
		}
		else if (instruction instanceof ElementContentContext)
		{
			ElementContentContext elementContentInstruction = (ElementContentContext) instruction;
			domInstruction = new ElementContentInstruction(pi, elementContentInstruction);
		}
		else if (instruction instanceof EndContext)
		{
			EndContext endInstruction = (EndContext) instruction;
			domInstruction = new EndInstruction(pi, endInstruction);
		}
		else if (instruction instanceof InsertContext)
		{
			domInstruction = new RecursiveLoopInstruction(pi);
		}
		else
		{
			domInstruction = null;
		}
		return domInstruction;
	}

	/**
	 * Instantiates a new expansion instruction.
	 *
	 * @param pi
	 *           the pi
	 */
	protected ExpansionInstruction(LocatedProcessingInstruction pi)
	{
		super(pi.getTarget(), pi.getData());
		setColumn(pi.getColumn());
		setLine(pi.getLine());
		Element parent = pi.getParentElement();
		int i = parent.indexOf(pi);
		parent.removeContent(i);
		parent.addContent(i, this);
	}

	/**
	 * Instantiates a new expansion instruction.
	 *
	 * @param target
	 *           the target
	 * @param data
	 *           the data
	 */
	protected ExpansionInstruction(String target, String data)
	{
		super(target, data);
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
			Notifications.getInstance().add(contextual);
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
			QueryEvaluationEngine engine = new QueryEvaluationEngine(InstructionParser.getQueryEnv());
			FrameStack frameStack = Context.getInstance().getFrameStack();
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
			Notifications.getInstance().add(contextual);
		}
		for (Diagnostic subDiagnostic : diagnostic.getChildren())
		{
			notifyErrors(subDiagnostic);
		}
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8385870921899134393L;

}
