package org.xmlgen.template.dom.specialization;

import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.parser.pi.PIParser.AttributeContentContext;
import org.xmlgen.template.dom.location.Location;

public class AttributeContentInstruction extends ContentInstruction
{	
	protected AttributeContentInstruction(ProcessingInstruction pi, AttributeContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
		attributeId = parsedPI.attributeID().Ident().getText();
	}
	
	public String getAttributeId()
	{
		return attributeId;
	}

	public Node getAttributeNode()
	{
		return attrNode;
	}
	
	private static final long serialVersionUID = -1488609623791079537L;
	private String attributeId;
	private Node attrNode;
	
	private Notification noAttributeWithThisName = null;
}
