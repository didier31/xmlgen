package org.xmlgen.template.dom.specialization.instructions;

import java.util.HashMap;
import java.util.Vector;

import org.xmlgen.Xmlgen;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
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

@SuppressWarnings("serial")
public class ExpandInstruction extends ExpansionInstruction
{
	protected ExpandInstruction(String data, int line, int column, Xmlgen xmlgen)
	{
		super(data, line, column, xmlgen);
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
		if (expansionContext.isExecuting())
		{		
			trace("<Expand>");
			/**
			 * Replace template, schema and output parameters by variable value of same name
			 */
			FrameStack frameStack = getXmlgen().getFrameStack();
			
		   Notifications notifications = new Notifications();
		   notifications.setNotified(getXmlgen().getNotifications().getNotified());
		   FrameStack newFrameStack = new FrameStack("");
		   Xmlgen xmlgen = new Xmlgen(notifications, newFrameStack);
		   
		   Context newContext = xmlgen.getContext(); 
		
		   Object paramValue;
		   if (frameStack.containsKey(template))
		   {
		   	paramValue = frameStack.get(template);
		   	if (paramValue instanceof String)
		   	{
		   		String templatePath = (String) paramValue;
		   		newContext.setXmlTemplate(templatePath);
		   	}
		   }
		   
		   if (frameStack.containsKey(output))
		   {
		   	paramValue = frameStack.get(output);
		   	if (paramValue instanceof String)
		   	{
		   		String outputPath = (String) paramValue;
		   		newContext.setOutput(outputPath);
		   	}
		   }
		   
		   
		   if (frameStack.containsKey(schema))
		   {
		   	paramValue = frameStack.get(schema);
		   	if (paramValue instanceof String)
		   	{
		   		String schemaPath = (String) paramValue;
		   		newContext.setSchema(schemaPath);
		   	}
		   }
		   
		   paramValue = frameStack.get(trace);			
		   if (paramValue instanceof String)
		   {
		   	String value = (String) paramValue; 
		   	Boolean isTraceOn = value.equalsIgnoreCase("yes"); 
		   	if (isTraceOn)
		   	{
		   		newContext.setTrace();
		   	}
		   }		   
		   
		   /**
		    * Check but there is no additionnal data source resources
		    * So, pass false as parameter.
		    */
		   newContext.check(false);
		   
		   /**
		    * Perform expansion
		    */
			notifications = xmlgen.getNotifications();
			HashMap<Gravity, Integer> counts = notifications.getCounts();
			
			if (counts.get(Gravity.Error) == 0 && counts.get(Gravity.Fatal) == 0)
			{
				/**
				 * Creates a copy of the frame context.
				 * It isolates upper stack from corruption
				 */
				Frame newFrame0 = newFrameStack.peek();
				newFrame0.putAll(frameStack);
			   xmlgen.doExpansion();
			}		   	
			getXmlgen().getNotifications().addAll(notifications);
			trace("End <Expand>");
		}
		return new Vector<Cloneable>(0);
	}
	
	private void trace(String msg)
	{
		if (getXmlgen().getContext().isTrace())
		{
			Message message = new Message(msg);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			LocationImpl location = new LocationImpl(new Artifact(""), -1, getLine(), getColumn());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications notifications = getXmlgen().getNotifications(); 
			notifications.add(contextual);
		}
	}

	final String template = "template";
	final String schema = "schema";
	final String output = "output";
	final String trace = "trace"; 
}
