/*
 * 
 */
package org.xmlgen.notifications;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextualNotification.
 */
public class ContextualNotification extends Notification
{

	/** The location. */
	private Artifact location;

	/**
	 * Instantiates a new contextual notification.
	 *
	 * @param notification
	 *           the notification
	 * @param location
	 *           the location
	 */
	public ContextualNotification(Notification notification, Artifact location)
	{
		super(notification);
		this.location = location;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Artifact getLocation()
	{
		return location;
	}

	@Override
	public String toString()
	{
		return getLocation().toString() + super.toString(); 
	}
}
