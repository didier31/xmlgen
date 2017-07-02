/*
 * 
 */
package org.xmlgen.notifications;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.xmlgen.notifications.Notification.Gravity;

// TODO: Auto-generated Javadoc
/**
 * The Class Notifications.
 */
public class Notifications extends LinkedList<Notification> implements Notifier
{

	/**
	 * Gets the single instance of Notifications.
	 *
	 * @return single instance of Notifications
	 */
	static public Notifications getInstance()
	{
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#clear()
	 */
	public void clear()
	{
		super.clear();
		resetCounts();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#add(java.lang.Object)
	 */
	public boolean add(Notification n)
	{
		counts.put(n.getGravity(), counts.get(n.getGravity()) + 1);
		boolean success = super.add(n);
		notify(n);
		return success;
	}

	/**
	 * Gets the counts.
	 *
	 * @return the counts
	 */
	public HashMap<Notification.Gravity, Integer> getCounts()
	{
		return counts;
	}

	/**
	 * Reset counts.
	 */
	public void resetCounts()
	{
		for (Gravity g : Gravity.values())
		{
			counts.put(g, 0);
		}
	}

	/**
	 * Instantiates a new notifications.
	 */
	public Notifications()
	{
		resetCounts();
	}

	@Override
	public void notify(Notification notification)
	{
		for (Notified notified : notifieds)
		{
			notified.notification(notification);
		}
	}

	@Override
	public void add(Notified notififed)
	{
		notifieds.add(notififed);
	}

	@Override
	public void clearNotifiedQueue()
	{
		notifieds.clear();
	}
	
	/** The instance. */
	static Notifications instance = new Notifications();

	/** The counts. */
	HashMap<Notification.Gravity, Integer> counts = new HashMap<Notification.Gravity, Integer>();

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5330193007718869533L;


	private Vector<Notified> notifieds = new Vector<Notified>(1);
}
