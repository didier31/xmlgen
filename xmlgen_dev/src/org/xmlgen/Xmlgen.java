package org.xmlgen;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xmlgen.context.Context;
import org.xmlgen.expansion.Expander;
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

public class Xmlgen
{

	public static void main(String[] args)
	{
		Xmlgen xmlgen = new Xmlgen();
		xmlgen.perform(args, null);
		System.err.print(xmlgen.toString(Notifications.getInstance()));
	}

	public void perform(String[] vargs, ClassLoader userServicesClassloader)
	{
		Context.clear();		
		Notifications.getInstance().clear();
		
		Context context = Context.getInstance();
		context.setUserServicesClassLoader(userServicesClassloader);

		SmartCmdlineParser parser = new SmartCmdlineParser(vargs);
		parser.parse();
		
		context.check();

		Notifications notifications = Notifications.getInstance();
		HashMap<Gravity, Integer> counts = notifications.getCounts();

		Expander expander = new Expander();

		Document document = null;
		
		if (counts.get(Gravity.Error) == 0 && counts.get(Gravity.Fatal) == 0)
		{
			document = expander.expand(Context.getInstance().getXmlTemplateDocument());

			XMLOutputter xml = new XMLOutputter();
			xml.setFormat(Format.getPrettyFormat());
			
			PrintStream xmlOutput = null;
			try
			{
				xmlOutput = new PrintStream(context.getOutput());
				if (document != null)
				{
					xmlOutput.println(xml.outputString(document));
				}
			}
			catch (FileNotFoundException e)
			{
				Notifications.getInstance().add(cant_write_output);
			}
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
		
	final static Message message = new Message("output can't be written on mass storage.");
	final static Notification cant_write_output = new Notification(Module.Parser, Gravity.Error, Subject.Output,
			message);
}
