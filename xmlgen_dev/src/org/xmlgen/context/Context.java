package org.xmlgen.context;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;

import org.xmlgen.notifications.Notifications;
import org.xmlgen.template.dom.Factories;
import org.xmlgen.template.dom.location.LocationAnnotator;

public class Context 
{

static public void clear() 
{
	instance = new Context();
}	
	
public static Context getInstance()
{
	return instance;
}

public String getXmlTemplate() 
{
	return xmlTemplateFilename;
}
public File getOutput() 
{
	return output;
}
public String getSchema() 
{
	return schemaFilename;
}
public FrameStack getFrameStack() 
{
	return frameStack;
}
public void setXmlTemplate(String xmlTemplate) 
{
	this.xmlTemplateFilename = xmlTemplate;
}
public void setOutput(String output) 
{
	this.output = new File(output);
}

public void setSchema(String schema) 
{
	this.schemaFilename = schema;
}

public Document getXmlTemplateDocument()
{
	return xmlTemplateDocument;
}

public String toString()
{
	String string = "template=" + getXmlTemplate() + '\n'
	              + "schema=" + getSchema() + '\n'
	              + "output=" + getOutput() + '\n';
	
	string += getFrameStack().toString();
	
	return string;
}

/**
 * Check context
 */
public void check()
{
	Notifications notifications = Notifications.getInstance();
	
	checkDataSources(notifications);
	checkSchemaAndTemplate(notifications);
	checkOutput(notifications);	
}

protected void checkSchemaAndTemplate(Notifications notifications)
{
	if (getSchema() != null)
	{
		checkSchema(notifications);
	}
	
	if (getXmlTemplate() == null)
	{
		notifications.add(templateMissing);
		return;
	}
	
	readTemplate(notifications);
	if (schema != null && xmlTemplateDocument != null)
	{
		validateTemplate(notifications);
	}
}

protected void validateTemplate(Notifications notifications)
{
	assert(xmlTemplateDocument != null && schema != null);
	Validator validator = schema.newValidator();
	validator.setErrorHandler(new XMLErrorsReporter(notifications));
   try 
   {
       validator.validate(new DOMSource(xmlTemplateDocument));
   } 
   catch (SAXException | IOException e) 
   {
		Message message = new Message(e.getMessage());
		Notification notification = new Notification(Module.Parameters_check, Gravity.Error, Subject.Template, message);
		notifications.add(notification);
   }
}

protected void readTemplate(Notifications notifications)
{	
	assert(getXmlTemplate() != null);
	
	XMLReader xmlReader;
	try
	{
		xmlReader = Factories.newXMLReader();
	} 
	catch (ParserConfigurationException | SAXException e)
	{		
		xmlReader = null;
		
		Message message = new Message(e.getMessage());
		Notification notification = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, message);
		notifications.add(notification);
	}
	
	if (xmlReader == null)
	{
		return;
	}
	
	xmlTemplateDocument = Factories.newDocument(); 
	DOMResult domResult = new DOMResult(xmlTemplateDocument);
	
	 /*
    * Create our filter to wrap the SAX parser, that captures the 
    * locations of elements and annotates their nodes as they are
    * inserted into the DOM.
    */
   LocationAnnotator locationAnnotator
           = new LocationAnnotator(xmlReader, xmlTemplateDocument);

   /*
    * Create the SAXSource to use the annotator.
    */
	java.net.URI templateURI = toNetURI(getXmlTemplate());	
   String systemId = templateURI.toString();
   InputSource inputSource = new InputSource(systemId);
   SAXSource saxSource = new SAXSource(locationAnnotator, inputSource);

   Transformer nullTransformer;
	try
	{
		nullTransformer = Factories.getTransformerFactory().newTransformer();
	} 
	catch (TransformerConfigurationException e)
	{
		nullTransformer = null;
		
		Message message = new Message(e.getMessage());
		Notification notification = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, message);
		Artefact artefact = new Artefact(e.getLocator().getPublicId());
		LocationImpl location = new LocationImpl(artefact, -1, e.getLocator().getColumnNumber(), e.getLocator().getLineNumber());
		ContextualNotification contextualNotification = new ContextualNotification(notification, location);
		notifications.add(contextualNotification);
	}
	/*
    * Finally read the XML into the DOM.
    */
	if (nullTransformer == null)
	{
		return;
	}
	nullTransformer.setErrorListener(new TransXMLErrorListener(notifications, Subject.Template));
	try
	{
		nullTransformer.transform(saxSource, domResult);
	} 
	catch (TransformerException e)
	{
		return;
	}
}

protected void checkSchema(Notifications notifications) 
{	
	assert(getSchema() != null);
   SchemaFactory factory;

   java.net.URI schemaURI = toNetURI(getSchema());
   
   Source schemaFile = new StreamSource(schemaURI.toASCIIString());
   Schema schema = null;
     
   String schemaLanguages[] = {  XMLConstants.W3C_XML_SCHEMA_NS_URI }; //, XMLConstants.RELAXNG_NS_URI };
	/* TODO : REMOVE : Message messages[] = new Message[schemaLanguages.length]; */
   
   boolean fail = true;
   
   for (int i = 0; i < schemaLanguages.length && fail; i++)
   {
   	factory = SchemaFactory.newInstance(schemaLanguages[i]);
   	try
   	{
   		schema = factory.newSchema(schemaFile);
   		fail = false;
   	} 
   	catch (SAXException e)
   	{
		schema = null;
		return;
		}
   }
   
   this.schema = schema;
}

protected void checkOutput(Notifications notifications) 
{
	File output = getOutput();

	if (output == null)
	{
		notifications.add(outputMissing);
	}
	else
	{
		File outputDir = output.getParentFile();
		if (outputDir != null && !outputDir.isDirectory())
		{
			notifications.add(outputNotFound);
		}
		else
		{
			if (output.isDirectory())
			{
				notifications.add(outputIsDirectory);
			}
			else
			{
				if (!outputDir.canWrite())
				{
					notifications.add(outputNotWritable);
				}
			}
		}	
	}
}

protected void checkDataSources(Notifications notifications)
{
	FrameStack frameStack = getFrameStack();
	
	Set<String> dataSourcesIds = frameStack.keySet(); 
	
	if (dataSourcesIds.isEmpty())
	{		
		notifications.add(dataSourceMissing);
	}
				
	Vector<String> validDatasourceIds = new Vector<String>(dataSourcesIds.size());
	Vector<Iterator<EObject>> validIterators = new Vector<Iterator<EObject>>(dataSourcesIds.size());
	
	// TODO : Provisoire
	/* Registry registry = resourceSet.getResourceFactoryRegistry();
	registry.getContentTypeToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE); */
	
	UMLResourcesUtil.initLocalRegistries(resourceSet);
	resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	
	/*resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
	   .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
	   .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE); */
	
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());
	
	for (String dataSourceId : dataSourcesIds)
	{
		Object object = frameStack.get(dataSourceId);
		assert(object instanceof String);
		
		String dataSourceFilename = (String) object;
		
		URI dataSourceURI = toURI(dataSourceFilename);
		 	
		Resource resource = resourceSet.createResource(dataSourceURI);
		
		if (resource == null)
		{
			notifications.add(dataSourceNoResourceFactoryRegistered);
			return;
		}			
		
		try
		{
			resource.load(null);
		} 
		catch (IOException e)
		{}
		
		EList<Diagnostic> errors = resource.getErrors();
		
		for (Diagnostic error : errors)
		{
			LocationImpl location = new LocationImpl(new Artefact(error.getLocation()), -1, error.getColumn(), error.getLine());	
			Notification notification = new Notification(Module.Parameters_check,
					                                       Gravity.Error,
					                                       Subject.DataSource, 
					                                       new Message(error.getMessage()));
			notification = new ContextualNotification(notification, location);
         notifications.add(notification);
		}
		
		if (errors.isEmpty())
		{
			List<EObject> contents = resource.getContents();
			if (contents.isEmpty())
			{
				Artefact artefact = new Artefact(dataSourceId);
				Notification notification = new ContextualNotification(dataSourceNotFound, artefact);
				notifications.add(notification);
			}
			else
			{
				validDatasourceIds.add(dataSourceId);
				validIterators.add(contents.iterator());
			}
		}
	// TODO : Add a couple (captures, end) instructions respectively at beginning and end of the DOM document.
	}
}

protected URI toURI(String filename)
{	
	return URI.createURI(toNetURI(filename).toString());
}

protected java.net.URI toNetURI(String filename)
{
	java.net.URI fileURI = null;
	try
	{
		fileURI = new java.net.URI(filename);
	} 
	catch (URISyntaxException e)
	{
		File file = new File(filename);
		fileURI = file.toURI();
	}
	return fileURI;
}

protected Context()
{}

ResourceSet resourceSet = new ResourceSetImpl();

static private Context instance = new Context();

private String xmlTemplateFilename = null;
private Document xmlTemplateDocument = null;

private File output = null;
private String schemaFilename = null;
private Schema schema = null;
private FrameStack frameStack = new FrameStack("");

final private Notification templateMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Argument_Missing);
final private Notification templateNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Not_Found);
final private Notification templateNotReadable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Read_Denied);

final private Notification schemaNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Schema, Message.Not_Found);
final private Notification schemaNotReadable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Schema, Message.Read_Denied);

final private Notification outputMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Argument_Missing);
final private Notification outputNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Not_Found);
final private Notification outputIsDirectory = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.IsDirectory);
final private Notification outputNotWritable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Write_Denied);

final private Notification dataSourceMissing = new Notification(Module.Parameters_check, Gravity.Warning, Subject.DataSource, Message.Argument_Missing);
final private Notification dataSourceNotFound = new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource, Message.Not_Found);
final private Notification dataSourceNotReadable =  new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource, Message.Read_Denied);
final private Notification dataSourceNoResourceFactoryRegistered = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Configuration, Message.No_Resource_Factory);
}
