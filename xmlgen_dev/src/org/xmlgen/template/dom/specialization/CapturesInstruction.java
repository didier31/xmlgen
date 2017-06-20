/*
 * 
 */
package org.xmlgen.template.dom.specialization;

import java.util.Iterator;
import java.util.Vector;

import org.antlr.v4.runtime.Token;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.notifications.Artifact;
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

// TODO: Auto-generated Javadoc
/**
 * The Class CapturesInstruction.
 */
public class CapturesInstruction extends ExpansionInstruction
{

	/**
	 * Instantiates a new captures instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param capturesInstruction
	 *           the captures instruction
	 */
	public CapturesInstruction(LocatedProcessingInstruction pi, CapturesContext capturesInstruction)
	{
		super(pi);
		LabelContext labelContext = capturesInstruction.label();
		label = (labelContext != null) ? capturesInstruction.label().getText() : "";
		int capturesCount = capturesInstruction.capture().size();
		datasourcesIDs = new Vector<String>(capturesCount);
		iterators = new Vector<Iterator<Object>>(capturesCount);
		captureQueries = new Vector<AstResult>(capturesCount);

		for (CaptureContext capture : capturesInstruction.capture())
		{
			String queryToParse = capture.expression().getText();
			AstResult parsedQuery = InstructionParser.parseQuery(queryToParse, pi);
			String id = capture.dataID().getText();
			if (!datasourcesIDs.contains(id))
			{
				datasourcesIDs.add(id);
				captureQueries.add(parsedQuery);
				iterators.add(null);
			}
			else
			{
				notifyDuplicateId(capture, id);
			}
		}
	}

	/**
	 * Initialize loop variables iterators 
	 * 
	 * loop variables are temporarily set with with their whole content (not available for the loop body) 
	 * in the order of their declaration
	 * in order to allow references to the previous ones
	 * in definitions of the following ones in the instruction.
	 * 
	 */

	public void initialize()
	{
		int i = 0;
		for (AstResult captureQuery : captureQueries)
		{
			Object result = null;
			if (captureQuery.getErrors().isEmpty())
			{
				result = eval(captureQuery);
			}
			Iterator<Object> iterator = createEMFIterator(result);
			assert (iterator != null);
			iterators.set(i, iterator);
			addToCurrentFrame(datasourcesIDs.get(i), result);
			i++;
		}
	}
	
	/**
	 * Iterate.
	 *
	 * @return if something has been effectively iterated.
	 */
	public boolean iterate()
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();

		int i = 0;
		boolean notAtTheEnd = false;
		for (String id : datasourcesIDs)
		{
			Iterator<Object> iterator = iterators.get(i);
			if (iterator.hasNext())
			{
				Object object = iterators.get(i).next();
				currentFrame.put(id, object);
				notAtTheEnd = true;
				traceForUser(id, object);
			}
			i++;
		}
		return notAtTheEnd;
	}

	/**
	 * Notify duplicate id.
	 *
	 * @param capture
	 *           the capture
	 * @param id
	 *           the id
	 */
	private void notifyDuplicateId(CaptureContext capture, String id)
	{
		Token startToken = capture.dataID().getStart();
		// TODO : Resolve -1 problem for column parameter.
		LocationImpl location = new LocationImpl(new Artifact(id), startToken.getStartIndex(), -1, startToken.getLine());
		ContextualNotification contextNotification = new ContextualNotification(duplicateDataSourceReference, location);
		notifications.add(contextNotification);
	}

	/**
	 * Creates the EMF iterator.
	 *
	 * @param result
	 *           the result
	 * @return the iterator
	 */
	@SuppressWarnings("unchecked")
	private Iterator<Object> createEMFIterator(Object result)
	{
		Iterator<Object> iterator;
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
		return iterator;
	}

	/**
	 * Adds the to current frame.
	 *
	 * @param id
	 *           the id
	 * @param value
	 *           the value
	 */
	protected void addToCurrentFrame(String id, Object value)
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		assert (!currentFrame.containsKey(id));
		currentFrame.put(id, value);
	}

	/**
	 * Trace for user.
	 *
	 * @param id
	 *           the id
	 * @param object
	 *           the object
	 */
	protected void traceForUser(String id, Object object)
	{
		if (Context.getInstance().isTrace())
		{
			String referenceValue = object != null ? object.toString() : "null";
			Message message = new Message(id + " = " + referenceValue);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getColumn(),
					getLine());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			notifications.add(contextual);
		}
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/** The duplicate data source reference. */
	Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, Gravity.Error,
			Subject.DataSource, Message.Duplicate_Reference);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5198212129556107335L;

	/** The datasources Ids. */
	protected Vector<String> datasourcesIDs;

	/** The iterators. */
	protected Vector<Iterator<Object>> iterators;

	/** parsed capture queries */
	private Vector<AstResult> captureQueries;

	/** The label. */
	private String label;

	/** The notifications. */
	static private Notifications notifications = Notifications.getInstance();
}
