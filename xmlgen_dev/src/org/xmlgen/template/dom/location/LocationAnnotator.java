/*
 * 
 */
package org.xmlgen.template.dom.location;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class LocationAnnotator.
 */
public class LocationAnnotator extends XMLFilterImpl
{

	/**
	 * Instantiates a new location annotator.
	 *
	 * @param xmlReader
	 *           the xml reader
	 * @param dom
	 *           the dom
	 */
	public LocationAnnotator(XMLReader xmlReader, Document dom)
	{
		super(xmlReader);

		// Add listener to DOM, so we know which node was added.
		EventListener domListener = new EventListener()
		{
			@Override
			public void handleEvent(Event e)
			{
				EventTarget target = ((MutationEvent) e).getTarget();
				lastAddedElement = (Element) target;
			}
		};

		((EventTarget) dom).addEventListener("DOMNodeInserted", domListener, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.XMLFilterImpl#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator locator)
	{
		super.setDocumentLocator(locator);
		this.locator = locator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.XMLFilterImpl#processingInstruction(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void processingInstruction(String target, String data) throws SAXException
	{
		super.processingInstruction(target, data);
		Locator startLocator = locatorStack.peek();

		Location location = new Location(startLocator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber(),
				-1, -1);

		lastAddedElement.getFirstChild().setUserData(Location.LOCATION, location, dataHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		super.startElement(uri, localName, qName, atts);

		// Keep snapshot of start location,
		// for later when end of element is found.
		locatorStack.push(new LocatorImpl(locator));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		// Mutation event fired by the adding of element end,
		// and so lastAddedElement will be set.
		super.endElement(uri, localName, qName);

		if (locatorStack.size() > 0)
		{
			Locator startLocator = locatorStack.pop();

			Location location = new Location(startLocator.getSystemId(), startLocator.getLineNumber(),
					startLocator.getColumnNumber(), locator.getLineNumber(), locator.getColumnNumber());

			lastAddedElement.setUserData(Location.LOCATION, location, dataHandler);
		}
	}

	/**
	 * The Class LocationDataHandler.
	 */
	// Ensure location data copied to any new DOM node.
	private class LocationDataHandler implements UserDataHandler
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.w3c.dom.UserDataHandler#handle(short, java.lang.String,
		 * java.lang.Object, org.w3c.dom.Node, org.w3c.dom.Node)
		 */
		@Override
		public void handle(short operation, String key, Object data, Node src, Node dst)
		{

			if (src != null && dst != null)
			{
				Location locationData = (Location) src.getUserData(Location.LOCATION);

				if (locationData != null)
				{
					dst.setUserData(Location.LOCATION, locationData, dataHandler);
				}
			}
		}
	}

	/** The locator. */
	private Locator locator;

	/** The last added element. */
	private Element lastAddedElement;

	/** The locator stack. */
	private Stack<Locator> locatorStack = new Stack<Locator>();

	/** The data handler. */
	private UserDataHandler dataHandler = new LocationDataHandler();
}
