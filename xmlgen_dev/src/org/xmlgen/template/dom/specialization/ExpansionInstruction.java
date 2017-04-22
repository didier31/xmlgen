package org.xmlgen.template.dom.specialization;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.acceleo.query.ast.Error;
import org.eclipse.acceleo.query.runtime.EvaluationResult;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.Query;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.acceleo.query.runtime.impl.QueryBuilderEngine;
import org.eclipse.acceleo.query.runtime.impl.QueryEvaluationEngine;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.ProcessingInstruction;
import org.xmlgen.context.Context;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
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
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ProcessingInstructionImpl;

abstract public class ExpansionInstruction extends ProcessingInstructionImpl {

	final static String piMarker = "xmlgen";
	
	/**
	 * Test is ProcessingInstruction is an expand PI
	 * @param pi
	 * @return
	 */
	static public boolean isExpandPI(ProcessingInstruction pi)
	{
		return piMarker.compareToIgnoreCase(pi.getTarget()) == 0;
	}
	
	static ExpansionInstruction create(ProcessingInstruction pi)
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
		   domInstruction = new AttributeContentIntruction(pi, attributeContentInstruction);
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
		else
		{
			domInstruction = null;
		}
		return domInstruction;
	}	
	
	protected ExpansionInstruction(ProcessingInstruction pi) 
	{
		super((CoreDocumentImpl) pi.getOwnerDocument(), pi.getTarget(), pi.getData());
		pi.getParentNode().replaceChild(this, pi);
	}
	
	protected ExpansionInstruction(CoreDocumentImpl ownerDoc, String target, String data)
	{
		super(ownerDoc, target, data);
	}

	static IQueryEnvironment queryEnvironment = Query.newEnvironmentWithDefaultServices(null);	
	
	protected void notifyErrors(AstResult compiledQuery)
	{
		List<Error> errors = compiledQuery.getErrors();
		for (Error error : errors)
		{
			// TODO: Localize errors
			Message message = new Message(error.toString());
			Notification notification = new Notification(Module.Parser,
					                                     Gravity.Fatal,
					                                     Subject.Template,
					                                     message);
			Notifications.getInstance().add(notification);
		}
	}
	
	protected AstResult parseQuery(String query)
	{
	    QueryBuilderEngine builder = new QueryBuilderEngine(queryEnvironment);
	    AstResult compiledQuery = builder.build(query);
	    notifyErrors(compiledQuery);
	    return compiledQuery;
	}
	
	protected EObject eval(AstResult parsedQuery)
	{
		if (parsedQuery != null && parsedQuery.getErrors().isEmpty())
		{   
			QueryEvaluationEngine engine = new QueryEvaluationEngine(queryEnvironment);		
			EvaluationResult evaluationResult = engine.eval(parsedQuery, 
   		                                                Context.getInstance().getFrameStack());
		
			
			
			
			EObject result = (EObject) evaluationResult.getResult();
			return result;
		}
		else
		{
			return null;
		}
	}
	
	protected void notifyErrors(Diagnostic diagnostic)
	{
		// TODO : notify errors in harmony with sub-classes
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8385870921899134393L;
	
}
