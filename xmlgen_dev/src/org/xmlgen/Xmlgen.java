package org.xmlgen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.xmlgen.context.Context;
import org.xmlgen.context.FrameStack;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.cmdline.SmartCmdlineParser;
import org.xmlgen.template.dom.specialization.content.Template;
import org.xmlgen.template.dom.specialization.instructions.BeginInstruction;

public class Xmlgen
{
	public Xmlgen(Notifications notifications, FrameStack frameStack)
	{
		this.notifications = notifications;
		this.frameStack = frameStack;
		context = new Context(this);
		expansionContext = new ExpansionContext();
	}

	public Notifications getNotifications()
	{
		return notifications;
	}
	
	public Context getContext()
	{
		return context;
	}
	
	public FrameStack getFrameStack()
	{
		return frameStack;
	}
	
	public ExpansionContext getExpansionContext()
	{
		return expansionContext;
	}
	
	public static void main(String[] args)
	{
		Notifications notifications = new Notifications();
		Xmlgen xmlgen = new Xmlgen(notifications, new FrameStack(""));
		xmlgen.perform(args, null);
		System.err.print(xmlgen.toString(xmlgen.getNotifications()));
	}

	public void perform(String[] vargs, ClassLoader userServicesClassloader)
	{				
		notifications.clear();	

		SmartCmdlineParser parser = new SmartCmdlineParser(vargs, this);
		parser.parse();
		
		perfom(context, userServicesClassloader);
	}

	public void perfom(Context context, ClassLoader userServicesClassloader)
	{
		context.setUserServicesClassLoader(userServicesClassloader);
		context.check(true);

		Notifications notifications = getNotifications();
		HashMap<Gravity, Integer> counts = notifications.getCounts();
		
		if (counts.get(Gravity.Error) == 0 && counts.get(Gravity.Fatal) == 0)
		{
			doExpansion();
		}
	}

	public void doExpansion()
	{
		Document document;
		Template template = context.getXmlTemplateDocument();
		
		document = template.expand(this);
		
		XMLOutputter xml = new XMLOutputter();
		
		FileOutputStream xmlOutput = null;
		
		try
		{
			xmlOutput = new FileOutputStream(context.getOutput());
			if (document != null)
			{
				xml.output(document, xmlOutput);
			}
		}
		catch (FileNotFoundException e)
		{
			getNotifications().add(cant_write_output);
		}
		catch (IOException e)
		{
			// TODO Notify an error to user.
		}
	}

	public String toString(Notifications notifications)
	{
		String string = "";
			
		for (Notification notification : notifications)
		{
			String specific = "";
			if (notification instanceof ContextualNotification)
			{
				ContextualNotification specNotification = (ContextualNotification) notification;
				Artifact artifact = specNotification.getLocation();
				specific = artifact.getName();
				if (artifact instanceof LocationImpl)
				{					
					LocationImpl location = (LocationImpl) artifact;
					specific += ":o" + location.getCharacterOffset() + ":l" + location.getLineNumber() + ":c" + location.getColumnNumber();
				}
			}
			
			string += fixedLengthString(notification.getGravity().toString(), 6) 
			  + "|" + fixedLengthString(notification.getModule().toString(), 9) 
			  + "|" + fixedLengthString(notification.getSubject().toString(), 11)
			  + "|" + fixedLengthString(specific, 12)
			  + "|" + fixedLengthString(notification.getMessage().toString(), 40)
			  + "\n"; 		
		}

	HashMap<Gravity, Integer> counts = notifications.getCounts();	
		
 	for (Gravity g : Gravity.values())
 	{
 		string += g.toString() + ":" + counts.get(g) + "    ";
 	}
	
 	string += "\n";
 	
	return string;
	}
	
	public String fixedLengthString(String string, int length) 
	{
	    return String.format("%1$"+length+ "s", string);
	}
	
	public void setBlock(String id, BeginInstruction beginInstruction)
	{
		 declaredBlocks.put(id, beginInstruction);
	}
	
	public boolean containsBlock(String id)
	{
		return declaredBlocks.containsKey(id);
	}
	
	public BeginInstruction getBlock(String id)
	{
		BeginInstruction beginInstruction = declaredBlocks.get(id);
		return beginInstruction;
	}
			
	private Notifications notifications;
	private FrameStack frameStack;
	private Context context;
	private ExpansionContext expansionContext;
	private HashMap<String, BeginInstruction> declaredBlocks = new HashMap<String, BeginInstruction>();
	
	final static Message message = new Message("output can't be written on mass storage.");
	final static Notification cant_write_output = new Notification(Module.Parser, Gravity.Error, Subject.Output,
			                                                         message);
}
