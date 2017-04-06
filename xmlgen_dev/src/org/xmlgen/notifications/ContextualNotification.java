package org.xmlgen.notifications;

import javax.xml.stream.Location;

public class ContextualNotification extends Notification 
{
private Artefact location;

public ContextualNotification(Notification notification, Artefact location) 
{
	super(notification);
	this.location = location;
}

public Artefact getLocation() 
{
	return location;
}

}
