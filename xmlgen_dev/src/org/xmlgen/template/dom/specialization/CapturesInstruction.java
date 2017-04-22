package org.xmlgen.template.dom.specialization;

import java.util.Iterator;
import java.util.Vector;

import org.antlr.v4.runtime.Token;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.ProcessingInstruction;
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
import org.xmlgen.parser.pi.PIParser.CaptureContext;
import org.xmlgen.parser.pi.PIParser.CapturesContext;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;

public class CapturesInstruction extends ExpansionInstruction 
{
	@SuppressWarnings("unchecked")
	public CapturesInstruction(ProcessingInstruction pi, CapturesContext capturesInstruction)
	{
		super(pi);
		datasourcesIDs = new Vector<String>(capturesInstruction.capture().size());
		iterators = new Vector<Iterator<EObject>>(capturesInstruction.capture().size());
		
		for (CaptureContext  capture : capturesInstruction.capture())
		{
			AstResult parsedQuery = parseQuery(capture.expression().getText());
			Iterator<EObject> iterator = null;
			if (parsedQuery.getErrors().isEmpty())
			{
				String id = capture.dataID().getText();
				if (!datasourcesIDs.contains(id))
				{
					EObject result = eval(parsedQuery);
					if (result instanceof Iterator)
					{
						iterator = (Iterator<EObject>) result;										
					}
					else
					{
						Vector<EObject> results = new Vector<EObject>(1);
						results.add(result);
						iterator = results.iterator();
					}					
				assert(iterator != null);
				datasourcesIDs.add(id);
				iterators.add(iterator);
				addToCurrentFrame(id);
				}
				else
				{
					Notifications notifications = Notifications.getInstance();
					Token startToken = capture.dataID().getStart();
				   // TODO : Resolve -1 problem for column parameter.
               LocationImpl location = new LocationImpl(new Artefact(id), startToken.getStartIndex(), -1, startToken.getLine());	  
               ContextualNotification contextNotification = new ContextualNotification(duplicateDataSourceReference, location);
               notifications.add(contextNotification); 
				}
			}
		}
	}
	
	public CapturesInstruction(Vector<String> dataSourceIDs, Vector<Iterator<EObject>> dataSourcesIterators, CoreDocumentImpl ownerDoc)
	{
		super(ownerDoc, piMarker, "");
		assert(dataSourceIDs.size() == dataSourcesIterators.size());
		this.datasourcesIDs = dataSourceIDs;
		this.iterators = dataSourcesIterators;
	}
	
	protected void addToCurrentFrame(String id)
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		assert(!currentFrame.containsKey(id));
		currentFrame.put(id, new String("dummy"));
	}
	
	public void iterate()
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		
		int i = 0;
		for (String id : datasourcesIDs)
		{
			Iterator<EObject> iterator = iterators.get(i); 
			if (iterator.hasNext())
			{
				Object object = iterators.get(i).next();
				currentFrame.put(id, object);
			}
			i++;
		}
	}
	
	Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, 
			                                                       Gravity.Error,
			                                                       Subject.DataSource,
			                                                       Message.Duplicate_Reference);

	private static final long serialVersionUID = 5198212129556107335L;
	
	Vector<String> datasourcesIDs;
	Vector<Iterator<EObject>> iterators;
}
