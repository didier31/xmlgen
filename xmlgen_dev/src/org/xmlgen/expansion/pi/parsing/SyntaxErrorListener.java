/*
 * 
 */
package org.xmlgen.expansion.pi.parsing;

import java.io.File;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.xmlgen.Xmlgen;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving syntaxError events. The class that is
 * interested in processing a syntaxError event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addSyntaxErrorListener<code> method. When the syntaxError
 * event occurs, that object's appropriate method is invoked.
 *
 * @see SyntaxErrorEvent
 */
public class SyntaxErrorListener extends BaseErrorListener
{
	public SyntaxErrorListener(int line, int column, Xmlgen xmlgen)
	{
		this.baseLine = line;
		this.baseColumn = column;
		this.xmlgen = xmlgen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.antlr.v4.runtime.BaseErrorListener#syntaxError(org.antlr.v4.runtime.
	 * Recognizer, java.lang.Object, int, int, java.lang.String,
	 * org.antlr.v4.runtime.RecognitionException)
	 */
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e)
	{
		Message message = new Message(msg);
		Notification syntax_error = new Notification(Module.Parser, Gravity.Error, Subject.Template, message);
		File template = new File(xmlgen.getContext().getXmlTemplate());
		Artifact artifact = new Artifact(template.getName());
		LocationImpl localisation = new LocationImpl(artifact, 0, baseLine, baseColumn);
		ContextualNotification contextualNotification = new ContextualNotification(syntax_error, localisation);
		xmlgen.getNotifications().add(contextualNotification);
	}
	
	private Xmlgen xmlgen;
	private int baseLine;
	private int baseColumn;
}
