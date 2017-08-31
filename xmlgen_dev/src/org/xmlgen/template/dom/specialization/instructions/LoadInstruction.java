package org.xmlgen.template.dom.specialization.instructions;

import java.util.Set;
import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.IService;
import org.eclipse.acceleo.query.runtime.ServiceUtils;
import org.xmlgen.Xmlgen;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.parser.pi.PIParser.UserServiceContext;

@SuppressWarnings("serial")
public class LoadInstruction extends ExpansionInstruction
{
	protected LoadInstruction(String pi, UserServiceContext userServiceInstruction, int line, int column, Xmlgen xmlgen)
	{
		super(pi, line, column, xmlgen);
		qualifiedClassname = getText(pi, userServiceInstruction.dottedIdent());
	}
	
	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
		if (expansionContext.isExecuting() && notLoaded())
		{
			load();
		}
		return new Vector<Cloneable>(0);
	}
	
	public String getClassname()
	{
		return qualifiedClassname;
	}

	public void load()
	{
		registerUserService(getClassname());
		notLoaded = false;
	}

	protected boolean notLoaded()
	{
		return notLoaded;
	}

	/**
	 * 
	 * Makes a java class accessible for queries.
	 * 
	 * @param userServiceName
	 *           the name of the java class.
	 */
	public void registerUserService(String userServiceName)
	{
		ClassLoader classLoader = getXmlgen().getContext().getUserServicesClassloader();
		if (classLoader == null)
		{
			classLoader = getClass().getClassLoader();
		}

		try
		{
			Class<?> _class = classLoader.loadClass(userServiceName);
			registerUserService(_class);
		}
		catch (ClassNotFoundException e)
		{
			Message message = new Message(e.getMessage());
			Notification notification = new Notification(Notification.Module.Parameters_check, Notification.Gravity.Error,
					                                       Notification.Subject.UserService, message);
			getXmlgen().getNotifications().add(notification);
		}
	}

	/**
	 * 
	 * Makes a java class accessible for queries.
	 * 
	 * @param serviceClass
	 */
	public void registerUserService(Class<?> serviceClass)
	{
		IQueryEnvironment env = InstructionParser.getQueryEnv();
		Set<IService> userServices = ServiceUtils.getServices(env, serviceClass);
		ServiceUtils.registerServices(env, userServices);
	}
	
	private boolean notLoaded = true;
	private String qualifiedClassname;
}