/*
 * 
 */
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

// TODO: Auto-generated Javadoc
/**
 * The Class AttributeContentInstruction.
 */
public class AttributeContentInstruction extends ContentInstruction
{

	/**
	 * Instantiates a new attribute content instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param parsedPI
	 *           the parsed PI
	 */
	protected AttributeContentInstruction(LocatedProcessingInstruction pi, AttributeContentContext parsedPI)
	{
		super(pi, parsedPI.expression().getText());
		attributeId = parsedPI.attributeID().Ident().getText();
		TerminalNode prefixToken = parsedPI.attributeID().prefix() != null ? parsedPI.attributeID().prefix().Ident()
				: null;
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

	/**
	 * Sets the attribute.
	 */
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
				if (attribute.getName().equals(getAttributeId()) && attribute.getNamespacePrefix().equals(getPrefix()))
				{
					attrNode = attribute;
				}
			}
		}
		if (attrNode == null)
		{
			Message attributeNotFoundMessage = new Message("attribute, " + attributeId + " is not found");
			Notification attributeNotFound = new Notification(Module.Parser, Gravity.Error, Subject.Template,
					attributeNotFoundMessage);
			Artifact artifact = new Artifact(parent.getQualifiedName());
			LocationImpl location = new LocationImpl(artifact, -1, parent.getColumn(), parent.getLine());
			ContextualNotification contextualAttributeNotFound = new ContextualNotification(attributeNotFound, location);
			Notifications.getInstance().add(contextualAttributeNotFound);
		}
	}

	/**
	 * Gets the attribute id.
	 *
	 * @return the attribute id
	 */
	public String getAttributeId()
	{
		return attributeId;
	}

	/**
	 * Gets the attribute.
	 *
	 * @return the attribute
	 */
	public Attribute getAttribute()
	{
		return attrNode;
	}

	/**
	 * Gets the prefix.
	 *
	 * @return the prefix
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1488609623791079537L;

	/** The attribute id. */
	private String attributeId;

	/** The prefix. */
	private String prefix = null;

	/** The attr node. */
	private Attribute attrNode = null;
}
