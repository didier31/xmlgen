package org.xmlgen.notifications;

import java.util.LinkedList;

public class Notifications extends LinkedList<Notification> 
{

static public Notifications getInstance()
{
	return instance;
}

protected Notifications()
{
}

static Notifications instance = new Notifications();

private static final long serialVersionUID = -5330193007718869533L;

}
