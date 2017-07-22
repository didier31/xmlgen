/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.List;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.jdom2.Attribute;
import org.jdom2.located.LocatedElement;
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
	protected AttributeContentInstruction(String pi, AttributeContentContext parsedPI, int line, int column)
	{
		super(parsedPI.expression().getText(), line, column);
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

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it, ExpansionContext expansionContext)
	{
		if (expansionContext.isExecuting())
		{
			Attribute attribute = getAttribute();

			if (attribute != null)
			{
				Object computedValue = eval();
				Attribute attributeClone = attribute.clone();
				if (computedValue != null)
				{
					attributeClone.setValue(computedValue.toString());
				}

				Vector<Cloneable> attributes = new Vector<Cloneable>(1);
				attributes.add(attributeClone);
				return attributes;
			}
			else
			{
				return new Vector<Cloneable>(0);
			}
		}
		else
		{
			return new Vector<Cloneable>(0);
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
		if (attrNode == null)
		{
			setAttribute();
		}
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
