package org.xmlgen.template.dom;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;

public class Factories
{
static public Document newDocument()
{
	return getDocumentBuilder().newDocument();
}

static public XMLReader newXMLReader() throws ParserConfigurationException, SAXException
{
	SAXParser saxParser = newSAXParser();
   XMLReader xmlReader = saxParser.getXMLReader();
   return xmlReader;
}

static public SAXParser newSAXParser() throws ParserConfigurationException, SAXException
{
	return getSAXParserFactory().newSAXParser();
}

static public DocumentBuilder getDocumentBuilder()
{
	return docBuilder;
}

static protected SAXParserFactory getSAXParserFactory()
{
	return saxParserFactory;
}

static public TransformerFactory getTransformerFactory()
{
	return transformerFactory;
}

static protected DocumentBuilderFactory getDocumentBuilderFactory()
{
	 return documentBuilderFactory;
}

static private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
static private TransformerFactory transformerFactory = TransformerFactory.newInstance();
static private DocumentBuilder docBuilder = null;
static private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

static
{
	try
	{
		docBuilder = getDocumentBuilderFactory().newDocumentBuilder();
	} 
	catch (ParserConfigurationException e)
	{
		Notification notification = new Notification(Module.XML_Parser, 
				                                       Gravity.Fatal,
				                                       Subject.Configuration,
				                                       new Message(e.getMessage()));
		Notifications.getInstance().add(notification);
	}
saxParserFactory.setNamespaceAware(true);
saxParserFactory.setValidating(true);
}
}
