package org.xmlgen.template.dom;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Factories
{

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
static private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

}
