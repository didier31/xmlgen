package org.xmlgen.context;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXParseException;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;

public class TransXMLErrorListener implements ErrorListener
{
	public TransXMLErrorListener(Notifications notifications, Subject subject)
	{
		this.notifications = notifications;
		this.subject = subject;
	}
	
	@Override
	public void warning(TransformerException exception) throws TransformerException
	{
		doNotification(exception, Gravity.Warning);
	}

	@Override
	public void error(TransformerException exception) throws TransformerException
	{
		doNotification(exception, Gravity.Error);
	}

	@Override
	public void fatalError(TransformerException exception) throws TransformerException
	{
		doNotification(exception, Gravity.Fatal);
	}
	
	protected void doNotification(TransformerException exception, Gravity gravity)
	{
		Message message = new Message(exception.getMessage());
		Notification notification = new Notification(Module.Parameters_check, gravity, subject, message);
		SourceLocator locator = exception.getLocator();
		if (locator != null)
		{
			Artefact artefact = new Artefact(locator.getPublicId());
			LocationImpl location = new LocationImpl(artefact, -1, locator.getColumnNumber(), locator.getLineNumber());
			notification = new ContextualNotification(notification, location);
		}
		else if (exception.getException() instanceof SAXParseException)
		{
			SAXParseException e = (SAXParseException) exception.getException();
			Artefact artefact = new Artefact(e.getSystemId());
			LocationImpl location = new LocationImpl(artefact, -1, e.getColumnNumber(), e.getLineNumber());
			notification = new ContextualNotification(notification, location);
		}
		notifications.add(notification);
	}
	
	private Notifications notifications;
	private Subject subject;
}
