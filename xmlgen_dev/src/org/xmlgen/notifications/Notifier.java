package org.xmlgen.notifications;

public interface Notifier
{
	public void notify(Notification notification);
	public void add(Notified notififed);	
	public void clearNotifiedQueue();
}
