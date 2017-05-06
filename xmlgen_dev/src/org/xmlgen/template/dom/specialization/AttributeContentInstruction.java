package org.xmlgen.template.dom.specialization;

import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.jdom2.Attribute;
import org.jdom2.located.LocatedElement;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.AttributeContentContext;

public class AttributeContentInstruction extends ContentInstruction
{	
	protected AttributeContentInstruction(LocatedProcessingInstruction pi, AttributeContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
		attributeId = parsedPI.attributeID().Ident().getText();
		TerminalNode prefixToken = parsedPI.attributeID().Prefix();
		if (prefixToken != null)
		{
			prefix = prefixToken.getText();
			if (prefix.lastIndexOf(':') == prefix.length() - 1)
			{
				prefix = prefix.substring(0, prefix.length() - 1);
			}
		}
		setAttribute();
	}
	
	protected void setAttribute()
	{
		LocatedElement parent = (LocatedElement) getParentElement();
		List<Attribute> attributesList = parent.getAttributes();
   	
		if (getPrefix() == null)
		{
			attrNode = getParentElement().getAttribute(getAttributeId());
		}
		else
		{			
			for (Attribute attribute : attributesList)
			{
				if (attribute.getName().equals(getAttributeId())
					 &&
					 attribute.getNamespacePrefix().equals(getPrefix())
					)
				{
					attrNode = attribute;
				}
   		}
		}
		if (attrNode == null)
		{
   		Message attributeNotFoundMessage = new Message("attribute, " + attributeId + " is not found");
   		Notification attributeNotFound = new Notification(Module.Parser, Gravity.Error, Subject.Template, attributeNotFoundMessage );
   		Artifact artifact = new Artifact(parent.getQualifiedName());
   		LocationImpl location = new LocationImpl(artifact, -1, parent.getColumn(), parent.getLine());
   		ContextualNotification contextualAttributeNotFound = new ContextualNotification(attributeNotFound, location);
   		Notifications.getInstance().add(contextualAttributeNotFound); 
		}
	}
	
	public String getAttributeId()
	{
		return attributeId;
	}

	public Attribute getAttribute()
	{
		return attrNode;
	}
	
	public String getPrefix()
	{
		return prefix;
	}

	private static final long serialVersionUID = -1488609623791079537L;
	private String attributeId;
	private String prefix = null;
	private Attribute attrNode = null;
}
