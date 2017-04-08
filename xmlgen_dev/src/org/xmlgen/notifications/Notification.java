package org.xmlgen.notifications;

import org.xmlgen.notifications.Notification.Message;

public class Notification 
{
	public enum Module  
	    { 
		Parameters_check("Parameters check"), Parser("Parser"), Expansion("Expansion");
		
		private Module(String string)
		{
			this.string = string;
		}
		
		public String toString()
		{
			return string;
		}
		
		private String string;
		}

	public enum Gravity 
	{
		Information("Info"), Warning("Warn"), Error("Error"), Fatal("Fatal");
		
		public String toString()
		{
			return string;
		}
		
		private Gravity(String string)
		{
			this.string = string;
		}
		
		public String string;
	};
	
	public enum Subject 
	{ 
		Template("Template"), Schema("Schema"), Output("Output"), DataSource("Data source"), Command_Line("CMD");
		
		public String toString()
		{
			return string;
		}
		
		private Subject(String string)
		{
			this.string = string;
		}
		
		private String string;
	};
	
	static public class Message 
	{ 
		public static final Message Read_Denied = new Message("read denied");
		public static final Message Not_Found = new Message("not found");
		public static final Message Argument_Missing = new Message("argument missing");
		public static final Message Write_Denied = new Message("write denied");
		public static final Message Duplicate_Reference = new Message("duplicate reference");
		public static final Message IsDirectory = new Message("is a directory");;
		
		public String toString()
		{
			return string;
		}		
		
		public Message(String string)
		{
			this.string = string;
		}		
		
		private String string;
	};

    public Notification(Module module, Gravity gravity, Subject subject, Message message) 
	{
		super();
		this.module = module;
		this.gravity = gravity;
		this.subject = subject;
		this.message = message;
	}
	
	public Notification(Notification notification) 
	{
		this(notification.getModule(), 
		     notification.getGravity(), 
		     notification.getSubject(), 
		     notification.getMessage());
	}
	
	public Module getModule() 
	{
		return module;
	}

	public Gravity getGravity() 
	{
		return gravity;
	}
	
	public Message getMessage() 
	{
		return message;
	}
	
	public Subject getSubject()
	{
		return subject;
	}
	
	private Module module;
	private Gravity gravity;
	private Subject subject;
	private Message message;
}
