package org.xmlgen.notifications;

import java.util.HashMap;
import java.util.LinkedList;

import org.xmlgen.notifications.Notification.Gravity;

public class Notifications extends LinkedList<Notification> 
{

static public Notifications getInstance()
{
	return instance;
}

public void clear()
{
	super.clear();
	resetCounts();
	
}

public boolean add(Notification n)
{
	counts.put(n.getGravity(), counts.get(n.getGravity()) + 1);
	return super.add(n);
}

public HashMap<Notification.Gravity, Integer> getCounts()
{
	return counts;
}

protected void resetCounts()
{
	for (Gravity g : Gravity.values())
	{
	counts.put(g, 0);
	}
}

protected Notifications()
{
	resetCounts();
}

static Notifications instance = new Notifications();

HashMap<Notification.Gravity, Integer> counts = new HashMap<Notification.Gravity, Integer>();

private static final long serialVersionUID = -5330193007718869533L;

}
