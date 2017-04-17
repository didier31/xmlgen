package org.xmlgen.context;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;

public class XMLErrorsReporter implements ErrorHandler
{
	public XMLErrorsReporter(Notifications notifications)
	{
		this.notifications = notifications;
	}
	
	@Override
	public void warning(SAXParseException exception) throws SAXException
	{
		doNotification(exception, Gravity.Warning);
	}

	@Override
	public void error(SAXParseException exception) throws SAXException
	{
		doNotification(exception, Gravity.Error);
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException
	{
		doNotification(exception, Gravity.Fatal);
	}
	
	protected void doNotification(SAXParseException exception, Gravity gravity)
	{
		Message message = new Message(exception.getMessage());
		Notification notification = new Notification(Module.Parameters_check, gravity, Subject.Template, message);
		Artefact artefact = new Artefact(exception.getPublicId());
		LocationImpl location = new LocationImpl(artefact, -1, exception.getColumnNumber(), exception.getLineNumber());
		ContextualNotification contextualNotification = new ContextualNotification(notification, location);
		notifications.add(contextualNotification);
	}
	
   private Notifications notifications;
}
