package org.xmlgen.notifications;

public class ContextualNotification extends Notification 
{
private Artifact location;

public ContextualNotification(Notification notification, Artifact location) 
{
	super(notification);
	this.location = location;
}

public Artifact getLocation() 
{
	return location;
}

}
