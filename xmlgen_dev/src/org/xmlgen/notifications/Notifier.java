package org.xmlgen.notifications;

import java.util.Vector;

public interface Notifier
{
	public void notify(Notification notification);
	public void add(Notified notififed);
	public Vector<Notified> getNotified();	
	public void setNotified(Vector<Notified> notifieds);
	public void clearNotifiedQueue();
}
