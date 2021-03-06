package org.xmlgen.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.GenericXMLResourceFactoryImpl;
import org.eclipse.papyrus.designer.languages.java.profile.PapyrusJava.PapyrusJavaPackage;
import org.eclipse.uml2.uml.UMLPlugin;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resource.XMI2UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlgen.Xmlgen;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;

import org.xmlgen.notifications.Notifications;
import org.xmlgen.parser.cmdline.CmdlineParser;
import org.xmlgen.template.dom.specialization.content.Template;
import org.xmlgen.template.dom.specialization.factory.TemplateDomFactory;

/**
 * Context is usually initialized by the command line parser : - xml template
 * URI - data sources URIs and reference names - xml schema URI - output
 * pathname - trace on/off - user-defined java services
 * 
 * It provides the parameters given by the user : - xml template - xml schema -
 * data sources (reference name, value) - trace on/off
 * 
 * Are registered to acceleo queries : - used-defined java services
 * 
 * Can checked : - xml schema (existence and readability and correctness) -
 * xmltemplate (existence and readability and correctness against xml-schema). -
 * data sources (existence and readability and correctness)
 * 
 * It provides frame stack of data sources for the expansion processing.
 * 
 * @author Didier Garcin
 * 
 */
public class Context
{
	public Context(Xmlgen xmlgen)
	{
		this();
		notifications = xmlgen.getNotifications();
		this.xmlgen = xmlgen;
	}

	protected Context()
	{
		resourceSet.setURIResourceMap(new HashMap<URI, Resource>());
	}

	/**
	 * Gets the user services class loader.
	 * 
	 * @param userServicesClassloader
	 */
	public ClassLoader getUserServicesClassloader()
	{
		return this.userServicesClassloader;
	}

	/**
	 * Gets the xml template.
	 *
	 * @return the xml template
	 */
	public String getXmlTemplate()
	{
		return xmlTemplateFilename;
	}

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	public File getOutput()
	{
		return output;
	}

	/**
	 * Gets the schema.
	 *
	 * @return the schema
	 */
	public String getSchema()
	{
		return schemaFilename;
	}

	/**
	 * Sets the user services class loader.
	 * 
	 * @param userServicesClassLoader
	 */
	public void setUserServicesClassLoader(ClassLoader userServicesClassLoader)
	{
		this.userServicesClassloader = userServicesClassLoader;
	}

	/**
	 * Sets the xml template.
	 *
	 * @param xmlTemplate
	 *           the new xml template
	 */
	public void setXmlTemplate(String xmlTemplate)
	{
		this.xmlTemplateFilename = xmlTemplate;
	}

	/**
	 * Sets the output.
	 *
	 * @param output
	 *           the new output
	 */
	public void setOutput(String output)
	{
		if (output != null)
		{
		this.output = new File(output);
		}
		else
		{
			this.output = null;
		}
	}

	/**
	 * Sets the schema.
	 *
	 * @param schema
	 *           the new schema
	 */
	public void setSchema(String schema)
	{
		this.schemaFilename = schema;
	}

	/**
	 * Sets the trace.
	 */
	public void setTrace()
	{
		trace = true;
	}

	/**
	 * Checks if is trace.
	 *
	 * @return true, if is trace
	 */
	public boolean isTrace()
	{
		return trace;
	}

	/**
	 * 
	 * Get the ResourceSet, used for datasources.
	 * 
	 * (allows user to registers additional EMF packages.)
	 * 
	 * @return the ResourceSet.
	 */
	public ResourceSet getResourceSet()
	{
		return resourceSet;
	}

	/**
	 * Gets the xml template document.
	 *
	 * @return the xml template document
	 */
	public Template getXmlTemplateDocument()
	{
		return xmlTemplateDocument;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String string = "template=" + getXmlTemplate() + '\n' + "schema=" + getSchema() + '\n' + "output=" + getOutput()
				+ '\n';

		string += xmlgen.getFrameStack().toString();

		return string;
	}

	/**
	 * Check context : - Schema - Template and against schema - Output
	 */
	public void check(boolean dataSourcesAsWell)
	{
		checkSchemaAndTemplate();
		if (dataSourcesAsWell)
		{
			checkAndCaptureDataSources();
		}
		checkOutput();
	}

	/**
	 * Check schema and template and template against schema.
	 * 
	 * Store template to be accessible through getXmlTemplateDocument()
	 */
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

	/**
	 * Validate template against schema.
	 */
	protected void validateTemplate()
	{
		assert (xmlTemplateDocument != null && schema != null);
		Validator validator = schema.newValidator();
		java.net.URI templateURI = toNetURI(getXmlTemplate());
		String systemId = templateURI.toString();
		InputSource inputSource = new InputSource(systemId);
		ErrorHandler templateErrorHandler = new XMLErrorsReporter(notifications, Module.Parameters_check,
				Subject.Template);
		validator.setErrorHandler(templateErrorHandler);
		SAXSource source = new SAXSource(inputSource);
		try
		{
			validator.validate(source);
		}
		catch (SAXException | IOException e)
		{
			Message message = new Message(e.getMessage());
			Notification notification = new Notification(Module.Parameters_check, Gravity.Error, Subject.Template,
					message);
			notifications.add(notification);
		}
	}

	/**
	 * Read template and store it to be accessible through
	 * getXmlTemplateDocument().
	 */
	protected void readTemplate()
	{
		xmlTemplateDocument = readTemplate(getXmlTemplate());
	}
	
	/**
	 * Read template and store it to be accessible through
	 * getXmlTemplateDocument().
	 */
	public Template readTemplate(String templateFilename)
	{
		assert (templateFilename != null);

		SAXBuilder jdomBuilder = new SAXBuilder();
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		jdomBuilder.setJDOMFactory(new TemplateDomFactory(getXmlgen()));
		Template xmlTemplateDocument = null;
		try
		{
			xmlTemplateDocument = (Template) jdomBuilder.build(templateFilename);
		}
		catch (JDOMException e)
		{
			Message message = new Message(e.getLocalizedMessage());
			Notification notification = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Template,
					message);
			notifications.add(notification);
		}
		catch (IOException e)
		{
		}
		//TODO : Check xmlTemplateDocument is not null => Notify user otherwise.
		return xmlTemplateDocument;
	}	

	protected Xmlgen getXmlgen()
	{
		// TODO Auto-generated method stub
		return xmlgen;
	}

	/**
	 * Check schema and store it to be accessible through getSchema() if success.
	 */
	protected void checkSchema()
	{
		assert (getSchema() != null);
		SchemaFactory factory;

		java.net.URI schemaURI = toNetURI(getSchema());

		Source schemaFile = new StreamSource(schemaURI.toASCIIString());
		Schema schema = null;

		System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI,
				"com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory");

		final String RNC_NS_URI = "http://relaxng.org/compact.html";
		System.setProperty(SchemaFactory.class.getName() + ":" + RNC_NS_URI,
				"com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");

		String schemaLanguages[] =
		{ XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLConstants.RELAXNG_NS_URI, RNC_NS_URI };

		boolean fail = true;

		Notifications localNotifications = new Notifications();

		HashMap<Gravity, Integer> counts = localNotifications.getCounts();

		ErrorHandler schemaErrorHandler = new XMLErrorsReporter(localNotifications, Module.Parameters_check,
				Subject.Schema);

		for (int i = 0; i < schemaLanguages.length && fail; i++)
		{
			factory = SchemaFactory.newInstance(schemaLanguages[i]);
			factory.setErrorHandler(schemaErrorHandler);
			try
			{
				schema = factory.newSchema(schemaFile);
				if (counts.get(Gravity.Error) > 0 || counts.get(Gravity.Fatal) > 0)
				{
					schema = null;
					localNotifications.resetCounts();
				}
				else
				{
					fail = false;
				}
			}
			catch (SAXException e)
			{
				schema = null;
			}
		}

		if (fail)
		{
			notifications.add(no_language_for_schema);
		}
		this.schema = schema;
	}

	/**
	 * Check output : - output argument exists - its parent directory exists and
	 * is writable
	 * 
	 * All errors are added to global notifications.
	 * 
	 */
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
			if (outputDir != null)
			{
				if (!outputDir.isDirectory())
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
			else
			{
				// TODO:
			}
		}
	}

	/**
	 * Register EMF packages 
	 */
	protected void registerEMFpackages()
	{
		/**
		 * UML inits
		 */
		UMLResourcesUtil.initLocalRegistries(resourceSet);
		
		String umlResourcePath = UMLResourcesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try 
		{
			umlResourcePath = URLDecoder.decode(umlResourcePath, "UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		URI umlResourcePluginURI = URI.createURI("jar:file:" + umlResourcePath + "!/");
		
		resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
				umlResourcePluginURI.appendSegment("libraries").appendSegment(""));
		
		resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
				umlResourcePluginURI.appendSegment("metamodels").appendSegment(""));
		
		resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI),
				umlResourcePluginURI.appendSegment("libraries").appendSegment("UMLPrimitiveTypes.library.uml"));
		
		resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.UML_METAMODEL_URI),
				umlResourcePluginURI.appendSegment("metamodels").appendSegment("UML.metamodel.uml"));

		resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.UML2_PROFILE_URI),
				umlResourcePluginURI.appendSegment("profiles").appendSegment("UML2.profile.uml"));		
		
		Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
		extensionToFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		extensionToFactoryMap.put(UMLResource.PROFILE_FILE_EXTENSION, UMLResource.Factory.INSTANCE );
		extensionToFactoryMap.put("uml", XMI2UMLResource.Factory.INSTANCE);
		
		/**
		 * Sysml inits
		 */		
		
/*		String sysmlResourcePath = SysmlResource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try 
		{
			sysmlResourcePath = URLDecoder.decode(sysmlResourcePath, "UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}	
		
		URI sysmlResourcePluginURI = URI.createURI("jar:file:" + sysmlResourcePath + "!/");
		
		resourceSet.getURIConverter().getURIMap().put(URI.createURI(SysmlResource.SYSML_PROFILE_URI),
				sysmlResourcePluginURI.appendSegment("profiles").appendSegment("SysML.profile.uml"));	*/	
		
		/**
		 * Papyrus profile init
		 */
				
		String papyrusJavaProfileResourcePath = PapyrusJavaPackage.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try 
		{
			papyrusJavaProfileResourcePath = URLDecoder.decode(papyrusJavaProfileResourcePath, "UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}		
		
     URI papyrusJavaProfileResourcePluginURI = URI.createURI("jar:file:" + papyrusJavaProfileResourcePath + "!/");
		
     URI papyrusJavaProfileURI = papyrusJavaProfileResourcePluginURI.appendSegment("profiles").appendSegment("PapyrusJava.profile.uml");  
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI("pathmap://PapyrusJava_PROFILES/PapyrusJava.profile.uml"), papyrusJavaProfileURI);

		resourceSet.getPackageRegistry().put(PapyrusJavaPackage.eNS_URI , PapyrusJavaPackage.eINSTANCE);	
		
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(PapyrusJavaPackage.eNS_URI,
				                                               URI.createURI("pathmap://PapyrusJava_PROFILES/PapyrusJava.profile.uml#_j9REUByGEduN1bTiWJ0lyw"));
	  
		/**
		 * Papyrus java library init
		*/ 	
		ClassLoader classLoader = getClass().getClassLoader();
		URL resourceURL = classLoader.getResource("models/JavaLibrary.uml");
		URI javaLibraryURI = URI.createURI(resourceURL.getFile());
		
	   Resource resource = resourceSet.createResource(javaLibraryURI);
	   InputStream resourceInputStream = classLoader.getResourceAsStream("models/JavaLibrary.uml"); 
		
		try
		{
			resource.load(resourceInputStream, null);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resourceSet.getURIConverter().getURIMap().put(URI.createURI("pathmap://PapyrusJava_LIBRARIES/JavaLibrary.uml"), javaLibraryURI);
		// Map the resource to able to resolve EMF proxies.
		resourceSet.getURIResourceMap().put(javaLibraryURI, resource);
		
		/**
		 * XML Resources
		 */
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml",
				new GenericXMLResourceFactoryImpl());			
	}

	/**
	 * check there is at least one data source in command line. If not, add a
	 * notification in the global notifications.
	 * 
	 * @param dataSourcesIds
	 *           reference names of the data sources.
	 * @return true if passed
	 */
	protected boolean checkDataSourceExistence(Set<String> dataSourcesIds)
	{
		/**
		 * Check there is at least a datasource else add a notification to the
		 * user.
		 */
		if (dataSourcesIds.isEmpty())
		{
			notifications.add(dataSourceMissing);
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Load a data source given its URI string
	 * 
	 * Notifications can be added to global notifications related to : - absence
	 * of suitable registered resource factory. - syntax and semantic errors in
	 * data source resource.
	 * 
	 * @param dataSourceFilename
	 *           the URI of the data source
	 * 
	 * @param dataSourceId
	 *           the reference name of the data source
	 * 
	 * @return loaded data source resource
	 */
	protected Resource loadDataSource(String dataSourceFilename, String dataSourceId)
	{
		URI dataSourceURI = toURI(dataSourceFilename);
		/**
		 * Create and load the data source EMF resource
		 */
		Resource resource = resourceSet.createResource(dataSourceURI);

		// Map the resource to able to resolve EMF proxies.
		resourceSet.getURIResourceMap().put(URI.createFileURI(dataSourceURI.lastSegment()), resource);

		if (resource == null)
		{
			Artifact artifact = new Artifact(dataSourceId);
			Notification notification = new ContextualNotification(dataSourceNoResourceFactoryRegistered, artifact);
			notifications.add(notification);
		}
		else
		{
			try
			{
				resource.load(null);
			}
			catch (IOException e)
			{
			}

			EList<Diagnostic> errors = resource.getErrors();

			for (Diagnostic error : errors)
			{
				LocationImpl location = new LocationImpl(new Artifact(error.getLocation()), -1, error.getLine(), error.getColumn());
				Notification notification = new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource,
						new Message(error.getMessage()));
				notification = new ContextualNotification(notification, location);
				notifications.add(notification);
			}
		}
		return resource;
	}

	/**
	 * Check data sources : - their existence. - syntax and semantic errors.
	 * 
	 * and capture data sources : put datasource contents on the frame stack. of
	 * the xml template including these data sources.
	 * 
	 */
	protected void checkAndCaptureDataSources()
	{
		registerEMFpackages();

		FrameStack frameStack = xmlgen.getFrameStack();
		Set<String> dataSourcesIds = frameStack.keySet();

		checkDataSourceExistence(dataSourcesIds);

		for (String dataSourceId : dataSourcesIds)
		{
			/**
			 * Get data source uri passed via the frame
			 */
			Object object = frameStack.get(dataSourceId);
			if (object instanceof CmdlineParser.Filename)
			{
				CmdlineParser.Filename dataSourceFilename = (CmdlineParser.Filename) object;

				Resource resource = loadDataSource(dataSourceFilename.toString(), dataSourceId);

				if (resource != null)
				{
					EList<Diagnostic> errors = resource.getErrors();

					if (errors.isEmpty())
					{
						List<EObject> contents = resource.getContents();
						if (contents.isEmpty())
						{
							Artifact artifact = new Artifact(dataSourceId);
							Notification notification = new ContextualNotification(dataSourceNotFound, artifact);
							notifications.add(notification);
						}
						else
						{
							frameStack.put(dataSourceId, contents);
						}
					}
				}
			}
			else
			{
				frameStack.put(dataSourceId, object);
			}
		}
		EcoreUtil.resolveAll(resourceSet);
	}

	/**
	 * To EMF URI.
	 *
	 * @param filename
	 *           the string of URI
	 * 
	 * @return the uri
	 */
	protected org.eclipse.emf.common.util.URI toURI(String filename)
	{
		return URI.createURI(toNetURI(filename).toString());
	}

	/**
	 * To java.net URI.
	 *
	 * @param filename
	 *           the string of URI
	 * 
	 * @return the java.net. URI
	 */
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

	/*
	 * Parent Xmlgen instance
	 */
	private Xmlgen xmlgen;
	
	/** The resource set. */
	private ResourceSetImpl resourceSet = new ResourceSetImpl();
	
	/** ClassLoader for loading the user services */
	private ClassLoader userServicesClassloader;

	/** The xml template filename. */
	private String xmlTemplateFilename = null;

	/** The xml template document. */
	private Template xmlTemplateDocument = null;

	/** The output. */
	private File output = null;

	/** The schema filename. */
	private String schemaFilename = null;

	/** The schema. */
	private Schema schema = null;

	/** The trace. */
	private boolean trace;

	/** The notifications. */
	private Notifications notifications;

	/* No suitable language for schema, found */
	final private Notification no_language_for_schema = new Notification(Module.Parameters_check, Gravity.Error,
			Subject.Schema, Message.No_Schema_Language_Found);

	/** The template missing. */
	final private Notification templateMissing = new Notification(Module.Parameters_check, Gravity.Fatal,
			Subject.Template, Message.Argument_Missing);

	/** The output missing. */
	final private Notification outputMissing = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output,
			Message.Argument_Missing);

	/** The output not found. */
	final private Notification outputNotFound = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.Output,
			Message.Not_Found);

	/** The output is directory. */
	final private Notification outputIsDirectory = new Notification(Module.Parameters_check, Gravity.Fatal,
			Subject.Output, Message.IsDirectory);

	/** The output not writable. */
	final private Notification outputNotWritable = new Notification(Module.Parameters_check, Gravity.Fatal,
			Subject.Output, Message.Write_Denied);

	/** The data source missing. */
	final private Notification dataSourceMissing = new Notification(Module.Parameters_check, Gravity.Warning,
			Subject.DataSource, Message.Argument_Missing);

	/** The data source not found. */
	final private Notification dataSourceNotFound = new Notification(Module.Parameters_check, Gravity.Error,
			Subject.DataSource, Message.Not_Found);

	/** The data source no resource factory registered. */
	final private Notification dataSourceNoResourceFactoryRegistered = new Notification(Module.Parameters_check,
			Gravity.Fatal, Subject.Configuration, Message.No_Resource_Factory);
}
