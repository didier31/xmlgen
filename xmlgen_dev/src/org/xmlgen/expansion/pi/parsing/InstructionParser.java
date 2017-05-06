package org.xmlgen.expansion.pi.parsing;

import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.acceleo.query.ast.Error;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.Query;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.acceleo.query.runtime.impl.QueryBuilderEngine;
import org.jdom2.ProcessingInstruction;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PILexer;
import org.xmlgen.parser.pi.PIParser;
import org.xmlgen.parser.pi.PIParser.ContentContext;
import org.xmlgen.parser.pi.PIParser.InputPIContext;

public class InstructionParser 
{
	 static public ParserRuleContext parse(ProcessingInstruction pi)
	{
		InputPIContext inputPI = doParse(pi);
		if (inputPI.content() != null)
		{
			ContentContext content = inputPI.content();
			if (content.attributeContent() != null)
			{
				return content.attributeContent();
			}
			else if (content.elementContent() != null)
			{
				return content.elementContent();
			}
			else
			{
				assert (false);
				return null;
			}
		}
		else if (inputPI.captures() != null)
		{
			return inputPI.captures();
		}
		else if (inputPI.end() != null)
		{
			return inputPI.end();
		}
		else
		{			
			return null;
		}
	}
	 
	public static AstResult parseQuery(String query, LocatedProcessingInstruction pi)
	{
	    QueryBuilderEngine builder = new QueryBuilderEngine(queryEnvironment);
	    AstResult astResult = builder.build(query);
	    notifyErrors(astResult, pi);
	    return astResult;
	}

	static private IQueryEnvironment queryEnvironment = Query.newEnvironmentWithDefaultServices(null);
	
	static public IQueryEnvironment getQueryEnv()
	{
		return queryEnvironment;
	}
		
	protected static InputPIContext doParse(ProcessingInstruction pi)
	{	
		PILexer lexer = new PILexer(CharStreams.fromString(pi.getData()));
		PIParser parser = new PIParser(new CommonTokenStream(lexer));
		parser.addErrorListener(new SyntaxErrorListener());
		InputPIContext inputPI = parser.inputPI();
		return inputPI;
	}
	
	protected static void notifyErrors(AstResult compiledQuery, LocatedProcessingInstruction pi)
	{
		List<Error> errors = compiledQuery.getErrors();
		for (Error error : errors)
		{
			Message message = new Message(error.toString());
			Notification notification = new Notification(Module.Parser,
					                                       Gravity.Fatal,
					                                       Subject.Template,
					                                       message);
			Artifact artefact = new Artifact("xml template");
			LocationImpl location = new LocationImpl(artefact, -1, pi.getColumn(), pi.getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications.getInstance().add(contextual);
		}
	}
}
