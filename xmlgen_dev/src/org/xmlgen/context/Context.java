package org.xmlgen.context;

import java.io.File;
import java.util.Set;

import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;

public class Context 
{

static public void clear() 
{
	instance = new Context();
}	
	
public static Context getInstance()
{
	return instance;
}

public File getXmlTemplate() 
{
	return xmlTemplate;
}
public File getOutput() 
{
	return output;
}
public File getSchema() 
{
	return schema;
}
public FrameStack getFrameStack() 
{
	return frameStack;
}
public void setXmlTemplate(String xmlTemplate) 
{
	this.xmlTemplate = new File(xmlTemplate);
}
public void setOutput(String output) 
{
	this.output = new File(output);
}

public void setSchema(String schema) 
{
	this.schema = new File(schema);
}

public String toString()
{
	String string = "template=" + getXmlTemplate() + '\n'
	              + "schema=" + getSchema() + '\n'
	              + "output=" + getOutput() + '\n';
	
	string += getFrameStack().toString();
	
	return string;
}

/**
 * Check context
 */
public void check()
{
	Notifications notifications = Notifications.getInstance();
	
	checkDataSources(notifications);
	checkTemplate(notifications);	
	checkSchema(notifications);
	checkOutput(notifications);	
}

protected void checkTemplate(Notifications notifications)
{
	File template = getXmlTemplate(); 
	if (template == null)
	{
		notifications.add(templateMissing);
	}
	else
	{
		if (!template.isFile())
		{
			notifications.add(templateNotFound);
		}
		else
		{
			if (!template.canRead())
			{
				notifications.add(templateNotReadable);
			}
		}
	}	
}

protected void checkSchema(Notifications notifications) 
{	
	File schema = getSchema();
	if (schema != null)
	{
		if (!schema.isFile())
		{
			notifications.add(schemaNotFound);
		}
		else
		{
			if (!schema.canRead())
			{
				notifications.add(schemaNotReadable);
			}
		}
	}
}

protected void checkOutput(Notifications notifications) 
{
	File output = getOutput();

	if (output == null)
	{
		notifications.add(outputMissing);
	}
	else
	{
		File outputDir = output.getParentFile();
		if (outputDir != null && !outputDir.isDirectory())
		{
			notifications.add(outputNotFound);
		}
		else
		{
			if (output.isDirectory())
			{
				notifications.add(outputIsDirectory);
			}
			else
			{
				if (!outputDir.canWrite())
				{
					notifications.add(outputNotWritable);
				}
			}
		}	
	}
}

protected void checkDataSources(Notifications notifications)
{
	FrameStack frameStack = getFrameStack();
	
	Set<String> dataSourcesStr = frameStack.keySet(); 
	
	if (dataSourcesStr.isEmpty())
	{		
		notifications.add(dataSourceMissing);
	}
			
	for (String dataSourceStr : dataSourcesStr)
	{
		Object object = frameStack.get(dataSourceStr);
		if (object instanceof File)
		{
			File dataSource = (File) object;
			
			if (!dataSource.isFile())
			{
				ContextualNotification _dataSourceNotFound = new ContextualNotification(dataSourceNotFound, 
						                                                                new Artefact(dataSource.getName()));
				notifications.add(_dataSourceNotFound);
			}
			else
			{
				if (!dataSource.canRead())
				{
					ContextualNotification _dataSourceNotReadable = new ContextualNotification(dataSourceNotReadable, 
							                                                                   new Artefact(dataSource.getName()));
					notifications.add(_dataSourceNotReadable);					
				}
			}			
		}
	}
}

protected Context()
{}

static private Context instance = new Context();

private File xmlTemplate = null, output = null, schema = null;
private FrameStack frameStack = new FrameStack("");

final private Notification templateMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Argument_Missing);
final private Notification templateNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Not_Found);
final private Notification templateNotReadable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Read_Denied);

final private Notification schemaNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Schema, Message.Not_Found);
final private Notification schemaNotReadable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Schema, Message.Read_Denied);

final private Notification outputMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Argument_Missing);
final private Notification outputNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Not_Found);
final private Notification outputIsDirectory = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.IsDirectory);
final private Notification outputNotWritable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Write_Denied);

final private Notification dataSourceMissing = new Notification(Module.Parameters_check, Gravity.Warning, Subject.DataSource, Message.Argument_Missing);
final private Notification dataSourceNotFound = new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource, Message.Not_Found);
final private Notification dataSourceNotReadable =  new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource, Message.Read_Denied);

}
