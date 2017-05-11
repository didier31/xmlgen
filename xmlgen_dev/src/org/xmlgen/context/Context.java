package org.xmlgen.context;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
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
import org.eclipse.emf.ecore.xmi.impl.GenericXMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.impl.NotationPackageImpl;
import org.eclipse.papyrus.infra.viewpoints.style.impl.StylePackageImpl;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;

import org.xmlgen.notifications.Notifications;
import org.xmlgen.template.dom.specialization.CapturesInstruction;

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

public void setTrace()
{
	trace = true;
}
public boolean isTrace()
{
	return trace;
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
	checkSchemaAndTemplate();
	checkDataSources();
	checkOutput();	
}

protected void checkSchemaAndTemplate()
{
	if (getSchema() != null)
	{
		checkSchema();
	}
	
	if (getXmlTemplate() == null)
	{
		notifications.add(templateMissing);
		return;
	}
	
	readTemplate();
	if (schema != null && xmlTemplateDocument != null && xmlTemplateDocument.getRootElement() != null)
	{
		validateTemplate();
	}
}

protected void validateTemplate()
{
	assert(xmlTemplateDocument != null && schema != null);
	Validator validator = schema.newValidator();
	java.net.URI templateURI = toNetURI(getXmlTemplate());	
   String systemId = templateURI.toString();
   InputSource inputSource = new InputSource(systemId);
	validator.setErrorHandler(templateErrorHandler );
	SAXSource source = new SAXSource(inputSource);
   try 
   {
       validator.validate(source);
   } 
   catch (SAXException | IOException e) 
   {
		Message message = new Message(e.getMessage());
		Notification notification = new Notification(Module.Parameters_check, Gravity.Error, Subject.Template, message);
		notifications.add(notification);
   }
}

protected void readTemplate()
{	
	assert(getXmlTemplate() != null);

	SAXBuilder jdomBuilder = new SAXBuilder();
	jdomBuilder.setJDOMFactory(new LocatedJDOMFactory());
   try
	{
		xmlTemplateDocument = jdomBuilder.build(getXmlTemplate());
	} catch (JDOMException e)
   {
		Message message = new Message(e.getLocalizedMessage());
		Notification notification = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, message);
		notifications.add(notification);
   }  
   catch (IOException e)
	{
	}
}

protected void checkSchema() 
{	
	assert(getSchema() != null);
   SchemaFactory factory;

   java.net.URI schemaURI = toNetURI(getSchema());
   
   Source schemaFile = new StreamSource(schemaURI.toASCIIString());
   Schema schema = null;

   System.setProperty(
         SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI, 
         "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory");
   
   final String RNC_NS_URI = "http://relaxng.org/compact.html";
	System.setProperty(
         SchemaFactory.class.getName() + ":" +  RNC_NS_URI,
         "com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
	
   String schemaLanguages[] = {  XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLConstants.RELAXNG_NS_URI, RNC_NS_URI };
   
   boolean fail = true;
   
   HashMap<Gravity, Integer> counts = notifications.getCounts();
   
   int errorsCount = counts.get(Gravity.Error);
   int fatalCount = counts.get(Gravity.Fatal);
   
   for (int i = 0; i < schemaLanguages.length && fail; i++)
   {
   	factory = SchemaFactory.newInstance(schemaLanguages[i]);
   	factory.setErrorHandler(schemaErrorHandler);
   	try
   	{
   		schema = factory.newSchema(schemaFile);
   		fail = false;
   	} 
   	catch (SAXException e)
   	{
		schema = null;
		}
   }
   
   if (errorsCount < counts.get(Gravity.Error) || fatalCount < counts.get(Gravity.Fatal))
   {
   	schema = null;
   }
   	
   
   this.schema = schema;
}

protected void checkOutput() 
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

protected void registerEMFpackages()
{
	UMLResourcesUtil.initLocalRegistries(resourceSet);
	resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("http://www.eclipse.org/gmf/runtime/1.0.2/notation", 
			                                                                    NotationFactory.eINSTANCE);
	resourceSet.getPackageRegistry().put("http://www.eclipse.org/gmf/runtime/1.0.2/notation", NotationPackageImpl.eINSTANCE);
	resourceSet.getPackageRegistry().put("http://www.eclipse.org/papyrus/infra/viewpoints/policy/style", StylePackageImpl.eINSTANCE);
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("notation", new XMIResourceFactoryImpl());
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new GenericXMLResourceFactoryImpl());
}

protected void checkDataSources()
{
	FrameStack frameStack = getFrameStack();
	
	Set<String> dataSourcesIds = frameStack.keySet();
	
	if (dataSourcesIds.isEmpty())
	{		
		notifications.add(dataSourceMissing);
	}
				
	Vector<String> validDatasourceIds = new Vector<String>(dataSourcesIds.size());
	Vector<Iterator<Object>> validIterators = new Vector<Iterator<Object>>(dataSourcesIds.size());
	
	registerEMFpackages();
	
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
			resourceSet.getURIConverter().createInputStream(resource.getURI());
			resource.load(null);
		} 
		catch (IOException e)
		{}
		
		EList<Diagnostic> errors = resource.getErrors();
		
		for (Diagnostic error : errors)
		{
			LocationImpl location = new LocationImpl(new Artifact(error.getLocation()), -1, error.getColumn(), error.getLine());	
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
				frameStack.peek().remove(dataSourceId);
				Artifact artifact = new Artifact(dataSourceId);
				Notification notification = new ContextualNotification(dataSourceNotFound, artifact);
				notifications.add(notification);
			}
			else
			{
				Vector<Object> contentRoot = new Vector<Object>(1);
				contentRoot.add(contents.get(0));
				
				validDatasourceIds.add(dataSourceId);
				validIterators.add(contentRoot.iterator());
			}
		}
	}
	
	frameStack.clear();
	
	// Add a captures instruction for data sources
	Document xmlTemplateDoc = getXmlTemplateDocument();
	if (xmlTemplateDoc != null && xmlTemplateDoc.getRootElement() != null)
	{
		CapturesInstruction dataSourcesCapturesInstruction = new CapturesInstruction(validDatasourceIds,
				                                                                       validIterators,
				                                                                       xmlTemplateDoc.getRootElement().getDocument());
		xmlTemplateDoc.getRootElement().addContent(0, dataSourcesCapturesInstruction);
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

private boolean trace;

private Notifications notifications = Notifications.getInstance();
private ErrorHandler templateErrorHandler = new XMLErrorsReporter(notifications, Module.Parameters_check, Subject.Template);
private ErrorHandler schemaErrorHandler = new XMLErrorsReporter(notifications, Module.Parameters_check, Subject.Schema);

final private Notification templateMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template, Message.Argument_Missing);

final private Notification outputMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Argument_Missing);
final private Notification outputNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Not_Found);
final private Notification outputIsDirectory = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.IsDirectory);
final private Notification outputNotWritable =  new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output, Message.Write_Denied);

final private Notification dataSourceMissing = new Notification(Module.Parameters_check, Gravity.Warning, Subject.DataSource, Message.Argument_Missing);
final private Notification dataSourceNotFound = new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource, Message.Not_Found);
final private Notification dataSourceNoResourceFactoryRegistered = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Configuration, Message.No_Resource_Factory);
}
