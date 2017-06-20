/*
 * 
 */
package org.xmlgen.context;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLErrorsReporter.
 */
public class XMLErrorsReporter implements ErrorHandler
{

	/**
	 * Instantiates a new XML errors reporter.
	 *
	 * @param notifications
	 *           the notifications
	 * @param module
	 *           the module
	 * @param subject
	 *           the subject
	 */
	public XMLErrorsReporter(Notifications notifications, Module module, Subject subject)
	{
		this.notifications = notifications;
		this.module = module;
		this.subject = subject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@Override
	public void warning(SAXParseException exception) throws SAXException
	{
		doNotification(exception, Gravity.Warning);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@Override
	public void error(SAXParseException exception) throws SAXException
	{
		doNotification(exception, Gravity.Error);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException exception) throws SAXException
	{
		doNotification(exception, Gravity.Fatal);
	}

	/**
	 * Do notification.
	 *
	 * @param exception
	 *           the exception
	 * @param gravity
	 *           the gravity
	 */
	protected void doNotification(SAXParseException exception, Gravity gravity)
	{
		Message message = new Message(exception.getMessage());
		Notification notification = new Notification(module, gravity, subject, message);
		Artifact artifact = new Artifact(exception.getPublicId());
		LocationImpl location = new LocationImpl(artifact, -1, exception.getColumnNumber(), exception.getLineNumber());
		ContextualNotification contextualNotification = new ContextualNotification(notification, location);
		notifications.add(contextualNotification);
	}

	/** The notifications. */
	private Notifications notifications;

	/** The module. */
	private Module module;

	/** The subject. */
	private Subject subject;
}
