/*
 * 
 */
package org.xmlgen.notifications;

// TODO: Auto-generated Javadoc
/**
 * The Class Notification.
 */
public class Notification
{

	/**
	 * The Enum Module.
	 */
	public enum Module
	{
		/** The Parameters check. */
		Parameters_check("Parameters check"),
		/** The Parser. */
		Parser("Parser"),
		/** The Expansion. */
		Expansion("Expansion"),
		/** The XML parser. */
		XML_Parser("XML Parser");

		/**
		 * Instantiates a new module.
		 *
		 * @param string
		 *           the string
		 */
		private Module(String string)
		{
			this.string = string;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		public String toString()
		{
			return string;
		}

		/** The string. */
		private String string;
	}

	/**
	 * The Enum Gravity.
	 */
	public enum Gravity
	{

		/** The Information. */
		Information("Info"),
		/** The Warning. */
		Warning("Warn"),
		/** The Error. */
		Error("Error"),
		/** The Fatal. */
		Fatal("Fatal");

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		public String toString()
		{
			return string;
		}

		/**
		 * Instantiates a new gravity.
		 *
		 * @param string
		 *           the string
		 */
		private Gravity(String string)
		{
			this.string = string;
		}

		/** The string. */
		public String string;
	};

	/**
	 * The Enum Subject.
	 */
	public enum Subject
	{

		/** The Template. */
		Template("Template"),
		/** The Schema. */
		Schema("Schema"),
		/** The Output. */
		Output("Output"),
		/** The Data source. */
		DataSource("Data source"),
		/** The Command line. */
		Command_Line("CMD"),
		/** The Configuration. */
		Configuration("Configuration"),
		/** Uset service */
		UserService("User service");

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		public String toString()
		{
			return string;
		}

		/**
		 * Instantiates a new subject.
		 *
		 * @param string
		 *           the string
		 */
		private Subject(String string)
		{
			this.string = string;
		}

		/** The string. */
		private String string;
	};

	/**
	 * The Class Message.
	 */
	static public class Message
	{

		/** The Constant Read_Denied. */
		public static final Message Read_Denied = new Message("read denied");

		/** The Constant Not_Found. */
		public static final Message Not_Found = new Message("not found");

		/** The Constant Argument_Missing. */
		public static final Message Argument_Missing = new Message("argument identifier missing");
		
		/** The Constant Argument_Missing. */
		public static final Message Argument_Value_Missing = new Message("argument value missing");

		/** The Constant Write_Denied. */
		public static final Message Write_Denied = new Message("write denied");

		/** The Constant Duplicate_Reference. */
		public static final Message Duplicate_Reference = new Message("duplicate reference");

		/** The Constant IsDirectory. */
		public static final Message IsDirectory = new Message("is a directory");

		/** The Constant URL_Syntax_Error. */
		public static final Message URL_Syntax_Error = new Message("URL malformed");

		/** The Constant No_Resource_Factory. */
		public static final Message No_Resource_Factory = new Message("No resource factory registered");

		/** The Constant No_Schema_Language_Found */
		public static final Message No_Schema_Language_Found = new Message("No suitable schema language, found");

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return string;
		}

		/**
		 * Instantiates a new message.
		 *
		 * @param string
		 *           the string
		 */
		public Message(String string)
		{
			this.string = string;
		}

		/** The string. */
		private String string;
	};

	/**
	 * Instantiates a new notification.
	 *
	 * @param module
	 *           the module
	 * @param gravity
	 *           the gravity
	 * @param subject
	 *           the subject
	 * @param message
	 *           the message
	 */
	public Notification(Module module, Gravity gravity, Subject subject, Message message)
	{
		super();
		this.module = module;
		this.gravity = gravity;
		this.subject = subject;
		this.message = message;
	}

	/**
	 * Instantiates a new notification.
	 *
	 * @param notification
	 *           the notification
	 */
	public Notification(Notification notification)
	{
		this(notification.getModule(), notification.getGravity(), notification.getSubject(), notification.getMessage());
	}

	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public Module getModule()
	{
		return module;
	}

	/**
	 * Gets the gravity.
	 *
	 * @return the gravity
	 */
	public Gravity getGravity()
	{
		return gravity;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public Message getMessage()
	{
		return message;
	}

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	public Subject getSubject()
	{
		return subject;
	}

	/** The module. */
	private Module module;

	/** The gravity. */
	private Gravity gravity;

	/** The subject. */
	private Subject subject;

	/** The message. */
	private Message message;
}
