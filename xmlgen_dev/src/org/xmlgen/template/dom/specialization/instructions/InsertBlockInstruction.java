package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jdom2.Content;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Context;
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
import org.xmlgen.template.dom.specialization.content.Element;

@SuppressWarnings("serial")
public class InsertBlockInstruction extends InsertInstruction
{
	public InsertBlockInstruction(String pi, TerminalNode labelContext, int line, int column, Xmlgen xmlgen)
	{
		super(pi, line, column, xmlgen);
		setLabel(labelContext.getText());
	}

	@Override
	protected Element getBody()
	{
		if (body == null)
		{
			BeginInstruction insertedBegin = getBegin();
			if (insertedBegin != null)
			{
				Collection<Content> structureContent = structureOf(insertedBegin);
				String elementName = getLabel().replaceAll("[\\[\\]]", "");
				body = new Element(elementName, getXmlgen());
				body.addContent(structureContent);
			}
			else
			{
				body = null;
			}
		}
		return body;
	}

	protected BeginInstruction getBegin()
	{
		ExpansionContext expansionContext = getXmlgen().getExpansionContext();
		BeginInstruction insertedBegin = expansionContext.getBegin(getLabel());
		if (insertedBegin == null)
		{
			String label = getLabel();

			Message message = new Message("Unknown " + " label '" + label + "' of begin instruction");
			Notification notification = new Notification(Module.Expansion, Gravity.Warning, Subject.Template, message);
			Xmlgen xmlgen = getXmlgen();
			Context context = xmlgen.getContext();
			Artifact artefact = new Artifact(context.getXmlTemplate());
			LocationImpl location = new LocationImpl(artefact, -1, getLine(), getColumn());
			ContextualNotification contextualNotification = new ContextualNotification(notification, location);
			Notifications notifications = getXmlgen().getNotifications();
			notifications.add(contextualNotification);
		}
		return insertedBegin;
	}

	protected void setLabel(String label)
	{
		this.label = label;
	}

	protected String getLabel()
	{
		return label;
	}

	private String label;
}
