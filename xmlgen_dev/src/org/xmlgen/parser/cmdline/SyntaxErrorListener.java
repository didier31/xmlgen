package org.xmlgen.parser.cmdline;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;

public class SyntaxErrorListener extends BaseErrorListener 
{
	@Override
	public void syntaxError(Recognizer<?,?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e)
	{
		Notification syntax_error = new Notification(Module.Parser,
				                                     Gravity.Error,
				                                     Subject.Command_Line, 
				                                     new Message(msg));
		Artifact artifact = new Artifact("");
		LocationImpl localisation = new LocationImpl(artifact, charPositionInLine, charPositionInLine, line);
		ContextualNotification contextualNotification = new ContextualNotification(syntax_error, localisation);
		Notifications.getInstance().add(contextualNotification);
	}
}
