package org.xmlgen.template.dom.specialization;

import java.util.Iterator;
import java.util.Vector;

import org.antlr.v4.runtime.Token;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.jdom2.Document;
import org.jdom2.ProcessingInstruction;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
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
import org.xmlgen.parser.pi.PIParser.LabelContext;

public class CapturesInstruction extends ExpansionInstruction 
{
	@SuppressWarnings("unchecked")
	public CapturesInstruction(ProcessingInstruction pi, CapturesContext capturesInstruction)
	{
		super(pi);
		LabelContext labelContext = capturesInstruction.label();
		label = (labelContext != null) ? capturesInstruction.label().getText() : "";
		datasourcesIDs = new Vector<String>(capturesInstruction.capture().size());
		iterators = new Vector<Iterator<Object>>(capturesInstruction.capture().size());		
		
		for (CaptureContext  capture : capturesInstruction.capture())
		{
			AstResult parsedQuery = InstructionParser.parseQuery(capture.expression().getText());
			Iterator<Object> iterator = null;
			if (parsedQuery.getErrors().isEmpty())
			{
				String id = capture.dataID().getText();
				if (!datasourcesIDs.contains(id))
				{
					Object result = eval(parsedQuery);
					if (result instanceof Iterable)
					{
						iterator = ((Iterable<Object>) result).iterator();										
					}
					else
					{
						Vector<Object> results = new Vector<Object>(1);
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
	
	public CapturesInstruction(Vector<String> dataSourceIDs, Vector<Iterator<Object>> dataSourcesIterators, Document ownerDoc)
	{
		super(piMarker, "");
		assert(dataSourceIDs.size() == dataSourcesIterators.size());
		this.datasourcesIDs = dataSourceIDs;
		this.iterators = dataSourcesIterators;
	}
	
	protected void addToCurrentFrame(String id)
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		assert(!currentFrame.containsKey(id));
		currentFrame.put(id, new String("####"));
	}
	
	/**
	 * 
	 * @return if iterate can be run again
	 */
	public boolean iterate()
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		
		int i = 0;
		boolean notAtTheEnd = true;
		for (String id : datasourcesIDs)
		{
			Iterator<Object> iterator = iterators.get(i); 
			if (iterator.hasNext())
			{
				Object object = iterators.get(i).next();
				currentFrame.put(id, object);
			}
			else
			{
				notAtTheEnd = false;
			}
			i++;
		}
	return notAtTheEnd;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, 
			                                                       Gravity.Error,
			                                                       Subject.DataSource,
			                                                       Message.Duplicate_Reference);

	private static final long serialVersionUID = 5198212129556107335L;
	
	private Vector<String> datasourcesIDs;
	private Vector<Iterator<Object>> iterators;
	private String label;
	
}
