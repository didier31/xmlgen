/*
 * 
 */
package org.xmlgen.expansion.pi.parsing;

import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.acceleo.query.ast.Error;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.Query;
import org.eclipse.acceleo.query.runtime.QueryParsing;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
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
import org.xmlgen.parser.pi.PIParser.BeginContext;
import org.xmlgen.parser.pi.PIParser.CapturesContext;
import org.xmlgen.parser.pi.PIParser.ContentContext;
import org.xmlgen.parser.pi.PIParser.EndContext;
import org.xmlgen.parser.pi.PIParser.InputPIContext;
import org.xmlgen.parser.pi.PIParser.InsertContext;
import org.xmlgen.parser.pi.PIParser.TaggedContext;

// TODO: Auto-generated Javadoc
/**
 * The Class InstructionParser.
 */
public class InstructionParser
{

	/**
	 * Parses the.
	 *
	 * @param pi
	 *           the pi
	 * @return the parser rule context
	 */
	static public ParserRuleContext parse(String pi)
	{
		InputPIContext inputPI = doParse(pi);
		ContentContext content = inputPI.content();
		if (content != null)
		{
			return content(content);
		}
		else
		{
			TaggedContext tagged = inputPI.tagged();
			if (tagged != null)
			{
				return tagged(tagged);
			}
			else
			{
				InsertContext insert = inputPI.insert();
				if (insert != null)
				{
					return insert;
				}
				else
				{
					assert (false);
					return null;
				}
			}			
		}
	}
	
	static protected ParserRuleContext content(ContentContext content)
	{
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
	
	static protected ParserRuleContext tagged(TaggedContext tagged)
	{
		CapturesContext captures = tagged.captures();
		if (captures != null)
		{
			return captures;
		}
		else
		{
			EndContext end = tagged.end();
			if (end != null)
			{
				return end;
			}
			else
			{
				BeginContext begin = tagged.begin();
				if (begin != null)
				{
					return begin;
				}
				else
				{
					assert (false);
					return null;
				}
			}
		}		
	}

	/**
	 * Parses the query.
	 *
	 * @param query
	 *           the query
	 * @param pi
	 *           the pi
	 * @return the ast result
	 */
	public static AstResult parseQuery(String query, int line, int column)
	{
		IQueryBuilderEngine builder = QueryParsing.newBuilder(queryEnvironment);
		AstResult astResult = builder.build(query);
		notifyErrors(astResult, line, column);
		return astResult;
	}

	/** The query environment. */
	static private IQueryEnvironment queryEnvironment = Query.newEnvironmentWithDefaultServices(null);

	/**
	 * Gets the query env.
	 *
	 * @return the query env
	 */
	static public IQueryEnvironment getQueryEnv()
	{
		return queryEnvironment;
	}

	/**
	 * Do parse.
	 *
	 * @param pi
	 *           the pi
	 * @return the input PI context
	 */
	protected static InputPIContext doParse(String pi)
	{
		PILexer lexer = new PILexer(CharStreams.fromString(pi));
		PIParser parser = new PIParser(new CommonTokenStream(lexer));
		parser.addErrorListener(new SyntaxErrorListener());
		InputPIContext inputPI = parser.inputPI();
		return inputPI;
	}

	/**
	 * Notify errors.
	 *
	 * @param compiledQuery
	 *           the compiled query
	 * @param pi
	 *           the pi
	 */
	protected static void notifyErrors(AstResult compiledQuery, int line, int column)
	{
		List<Error> errors = compiledQuery.getErrors();
		for (Error error : errors)
		{
			Message message = new Message(error.toString());
			Notification notification = new Notification(Module.Parser, Gravity.Fatal, Subject.Template, message);
			Artifact artefact = new Artifact("");
			LocationImpl location = new LocationImpl(artefact, -1, column, line);
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications.getInstance().add(contextual);
		}
	}
}
