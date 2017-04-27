package org.xmlgen.expansion;

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.template.dom.location.Location;
import org.xmlgen.template.dom.specialization.AttributeContentInstruction;
import org.xmlgen.template.dom.specialization.CapturesInstruction;
import org.xmlgen.template.dom.specialization.ElementContentInstruction;
import org.xmlgen.template.dom.specialization.EndInstruction;
import org.xmlgen.template.dom.specialization.ExpansionInstruction;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;

public class Expander 
{	
	public Document expand(Document document)
	{
		Node[] node = expandDeeper(document, null);		
		Document expandedDocument = (Document) node[0];
		// TODO: Implement DOCTYPE copy.
		return expandedDocument;
	}
	
	private Node goNode = null;
	
	protected Node goNode()
	{
		Node goToS = goNode;
		goNode = null;
		return goToS;
	}
	
	protected void setGoNode(Node goNode)
	{
		this.goNode = goNode; 
	}

	protected Node[] expandDeeper(Node root, Node parent)
	{
		return expandDeeper(root, parent, true);
	}
	
	protected Node[] expandDeeperOnly(Node root, Node parent)
	{
		return expandDeeper(root, parent, false);
	}
	
	protected Node[] expandDeeper(Node root, Node parent, boolean deeperAndLonger)
	{
		assert(root != null);
		
		Node[] allSibling;
		Node rootClone;
		
		if (root instanceof ElementContentInstruction)
		{
			ElementContentInstruction elementContentInstruction = (ElementContentInstruction) root;
			
			allSibling = doContentInstruction(elementContentInstruction);
		}
		else if (root instanceof AttributeContentInstruction)
		{
			AttributeContentInstruction attributeContentInstruction = (AttributeContentInstruction) root;
			
			allSibling = doAttributeContentInstruction(attributeContentInstruction, parent);
		}
		else if (root instanceof CapturesInstruction)
		{
			CapturesInstruction capturesInstruction = (CapturesInstruction) root;
			Node[] expansionResult = doLoop(capturesInstruction, parent);
			
			skipLoopBody(capturesInstruction, parent);
			
			allSibling = expansionResult;
		}
		else if (root instanceof EndInstruction)
		{
			EndInstruction endInstruction = (EndInstruction) root;
			endLoop(endInstruction);
			
			allSibling = new Node[0];
		}
		else if (root instanceof ProcessingInstruction && ExpansionInstruction.isExpandPI((ProcessingInstruction) root))
		{
			ProcessingInstruction pi = (ProcessingInstruction) root;
			ExpansionInstruction ei = ExpansionInstruction.create(pi);
			
			allSibling = expandDeeper(ei, parent);
		}
		else
		{
			rootClone = root.cloneNode(false);
			allSibling = new Node[] {rootClone};
		}

			Node firstChild = root.getFirstChild();
			if (firstChild != null)
			{				
				Node[] subTree = expandDeeper(firstChild, root);
				assert(allSibling.length > 0);
				add(allSibling[0], subTree);						
			}
			
			if (deeperAndLonger)
			{
				Node sibling = goNode();
				if (sibling == null)
				{
					sibling = root.getNextSibling();
				}
				if (sibling != null)
				{
					Node[] subForest = expandDeeper(sibling, parent);
					allSibling = ArrayUtils.addAll(allSibling, subForest);
				}			
			}
			return allSibling;
	}

	/**
	 * @param attributeContentInstruction
	 * @return
	 */
	private Node[] doAttributeContentInstruction(AttributeContentInstruction attributeContentInstruction, Node parent)
	{
		Node attribute = parent.getAttributes().getNamedItem(attributeContentInstruction.getAttributeId());

		if (attribute != null)
		{
			Object computedValue = attributeContentInstruction.eval();
			Node attributeClone = attribute.cloneNode(true);
			if (computedValue != null)
			{
				attributeClone.setNodeValue(computedValue.toString());
			}
			
			return new Node[] { attributeClone };
		}
		else
		{
			return new Node[0];
		}
	}

	/**
	 * @param elementContentInstruction
	 * @return
	 */
	private Node[] doContentInstruction(ElementContentInstruction elementContentInstruction)
	{
		Object computedValue = elementContentInstruction.eval();
		if (computedValue != null)
		{
			Text text = elementContentInstruction.getOwnerDocument().createTextNode(computedValue.toString());
			return new Node[] {text};
		}
		else
		{	
			return new Node[0];
		}
	}

	private void add(Node toRoot, Node[] children)
	{
		for (Node child : children)
		{
			Document document = toRoot.getOwnerDocument();
			if (document == null)
			{
				assert toRoot instanceof Document;
				document = (Document) toRoot;
				if (child instanceof Attr)
				{
					document.getAttributes().setNamedItem(child);
				}
				else
				{
					document.adoptNode(child);
					document.appendChild(child);
				}
			}
			else
			{
				if (child instanceof Attr)
				{
					toRoot.getAttributes().setNamedItem(child);
				}
				else
				{
					document.adoptNode(child);
					toRoot.appendChild(child);
				}
			}
		}
	}
	
	protected void skipLoopBody(CapturesInstruction ci, Node parent)
	{
		Node sibling = ci;
		while (sibling != null && !(sibling instanceof EndInstruction))
		{			
			sibling = sibling.getNextSibling();
		}
		if (sibling == null)
		{
			CoreDocumentImpl owner = (CoreDocumentImpl) ci.getOwnerDocument();
			EndInstruction ei = new EndInstruction("", owner, ExpansionInstruction.piMarker, "");
			parent.appendChild(ei);
			setGoNode(ei);
		}
		else
		{
			setGoNode((EndInstruction) sibling);
		}
	}
	
	protected Node[] doLoop(CapturesInstruction capturesInstruction, Node parent)
	{		
		String label = capturesInstruction.getLabel();
		Frame newFrame = new Frame(label, 0);
		Context.getInstance().getFrameStack().push(newFrame);
		Node[] expandedNodes = null;
		while (capturesInstruction.iterate())
		{			
			Node node = capturesInstruction.getNextSibling();		
			
			while (!(node == null || node instanceof EndInstruction))
			{
				Node[] localExpandedNodes = expandDeeperOnly(node, parent);
				expandedNodes = ArrayUtils.addAll(expandedNodes, localExpandedNodes);
				
				node = node.getNextSibling();
			}
		}
		return expandedNodes;
	}

	/**
	 * @param endInstruction
	 */
	protected void endLoop(EndInstruction endInstruction)
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		String frameName = currentFrame.getName();
		
		if (frameName != null && !frameName.equals(endInstruction.getLabel()))
		{
			Message message = new Message("Expecting " + frameName + ", not " + endInstruction.getLabel());
			Notification blockNamesNotCorresponding = new Notification(Module.Expansion, Gravity.Warning, Subject.Template, message);
			Location location = (Location) endInstruction.getUserData(Location.LOCATION);
			Artefact artefact = new Artefact(location.getSystemId());
			LocationImpl locationImpl = new LocationImpl(artefact, -1, location.getStartColumn(), location.getStartLine());
			ContextualNotification contextual = new ContextualNotification(blockNamesNotCorresponding, locationImpl);
			Notifications.getInstance().add(contextual);
		}
	} 
}
