/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.Iterator;
import java.util.Vector;

import org.antlr.v4.runtime.Token;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.expansion.ExpansionContext;
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
import org.xmlgen.parser.pi.PIParser.TaggedContext;

// TODO: Auto-generated Javadoc
/**
 * The Class CapturesInstruction.
 */
public class CapturesInstruction extends IterativeInstruction
{
	/**
	 * Instantiates a new captures instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param capturesInstruction
	 *           the captures instruction
	 */
	protected CapturesInstruction(String data, CapturesContext capturesContext, int line, int column)
	{
		super(data, (TaggedContext) capturesContext.getParent(), line, column);
		initFields(capturesContext, line, column);
	}

	private void initFields(CapturesContext capturesContext, int line, int column)
	{
		int capturesCount = capturesContext.capture().size();
		datasourcesIDs = new Vector<String>(capturesCount);
		iterators = new Vector<Iterator<Object>>(capturesCount);
		captureQueries = new Vector<AstResult>(capturesCount);

		for (CaptureContext capture : capturesContext.capture())
		{
			String queryToParse = capture.expression().getText();
			AstResult parsedQuery = InstructionParser.parseQuery(queryToParse, line, column);
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
	 * Creates a new context and Initialize loop variables iterators in it
	 * 
	 * loop variables are temporarily set with with their whole content (not
	 * available for the loop body) in the order of their declaration in order to
	 * allow references to the previous ones in definitions of the following ones
	 * in the instruction.
	 * 
	 */
	@Override
	protected void doInitialisation(ExpansionContext expansionContext)
	{
		// Initializes references in the just new created frame in stack
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
	@Override
	protected boolean iterateImpl(ExpansionContext expansionContext)
	{
		boolean iterating = doIterate();
		return iterating;
	}
	
	protected boolean doIterate()
	{
		Frame currentFrame = Context.getInstance().getFrameStack().peek();
		
		int i = 0;
		boolean iterating = false;
		
		for (String id : datasourcesIDs)
		{
			Iterator<Object> iterator = iterators.get(i);
			if (iterator.hasNext())
			{
				Object object = iterators.get(i).next();
				currentFrame.put(id, object);
				iterating = true;
				traceIteration(id, object);
			}
			i++;
		}
		return iterating;
	}

	@Override
	public boolean isFinished()
	{
		boolean isFinished = true;
		for (int i = 0; i < iterators.size() && isFinished; i++)
		{
			Iterator<Object> iterator = iterators.get(i);
			isFinished = !iterator.hasNext();
		}
		return isFinished;
	}

	/**
	 * Notify duplicate id.
	 *
	 * @param capture
	 *           the capture
	 * @param id
	 *           the id
	 */
	protected void notifyDuplicateId(CaptureContext capture, String id)
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
	 * Trace for user.
	 *
	 * @param id
	 *           the id
	 * @param object
	 *           the object
	 */
	protected void traceIteration(String id, Object object)
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

	/** The duplicate data source reference. */
	private Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, Gravity.Error,
			Subject.DataSource, Message.Duplicate_Reference);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5198212129556107335L;

	/** The datasources Ids. */
	protected Vector<String> datasourcesIDs;

	/** The iterators. */
	protected Vector<Iterator<Object>> iterators;

	/** parsed capture queries */
	private Vector<AstResult> captureQueries;



	/** The notifications. */
	static private Notifications notifications = Notifications.getInstance();
}
