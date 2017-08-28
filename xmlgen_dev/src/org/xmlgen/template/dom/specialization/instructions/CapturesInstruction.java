/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.antlr.v4.runtime.Token;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.xmlgen.Xmlgen;
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
	protected CapturesInstruction(String data, CapturesContext capturesContext, int line, int column, Xmlgen xmlgen)
	{
		super(data, getLabel(capturesContext.Label()) , line, column, xmlgen);
		initFields(capturesContext);
	}

	private void initFields(CapturesContext capturesContext)
	{
		int capturesCount = capturesContext.capture().size();
		datasourcesIDs = new Vector<String>(capturesCount);

		captureQueries = new Vector<AstResult>(capturesCount);

		for (CaptureContext capture : capturesContext.capture())
		{
			String queryToParse = getText(getData(), capture.expression());
			AstResult parsedQuery = InstructionParser.parseQuery(queryToParse, getLine(), getColumn());
			String id = capture.dataID().getText();
			if (!datasourcesIDs.contains(id))
			{
				datasourcesIDs.add(id);
				captureQueries.add(parsedQuery);
			}
			else
			{
				notifyDuplicateId(capture, id);
			}
		}
	}

	private void setIterators(Vector<Iterator<Object>> iterators)
	{
		currrentState().setIterators(iterators);
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
	protected void initialize()
	{
		super.initialize();
		State currentState = currentState();
		if (!currentState.isInitialized())
		{
			ExpansionContext expansionContext = getXmlgen().getExpansionContext();
			if (expansionContext.isExecuting())
			{
				int size = datasourcesIDs.size();
				Vector<Iterator<Object>> iterators = new Vector<Iterator<Object>>(size);
				iterators.setSize(size);
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
				setIterators(iterators);
			}
			currentState.setInitialized();
		}
	}

	/**
	 * Iterate.
	 *
	 * @return if finished (no more iteration at all).
	 */
	@Override
	protected boolean iterateImpl(ExpansionContext expansionContext)
	{
		Frame currentFrame = getXmlgen().getFrameStack().peek();

		Vector<Iterator<Object>> iterators = iterators();
		int i = 0;
		boolean iterating = true;

		for (String id : datasourcesIDs)
		{
			Iterator<Object> iterator = iterators.get(i);
			if (iterator.hasNext())
			{
				Object object = iterators.get(i).next();
				currentFrame.put(id, object);
				traceIteration(id, object);
				setExecuted();
			}
			else
			{
				iterating = false;
				break;
			}
			i++;
		}
		return !iterating;
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
		LocationImpl location = new LocationImpl(new Artifact(id), startToken.getStartIndex(), startToken.getLine(), startToken.getCharPositionInLine());
		ContextualNotification contextNotification = new ContextualNotification(duplicateDataSourceReference, location);
		Notifications notifications = getXmlgen().getNotifications();
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
		if (result == null)
		{
			Vector<Object> results = new Vector<Object>(0);
			iterator = results.iterator();
		}
		else if (result instanceof Iterable)
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

	Vector<Iterator<Object>> iterators()
	{
		State currentState = currrentState();
		Vector<Iterator<Object>> iterators = currentState.getIterators();
		return iterators;
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
		if (getXmlgen().getContext().isTrace())
		{
			String referenceValue = object != null ? object.toString() : "null";
			Message message = new Message("iteration: " + id + " = " + referenceValue);
			Notification notification = new Notification(Module.Expansion, Gravity.Information, Subject.DataSource,
					message);
			LocationImpl location = new LocationImpl(new Artifact(getLabel() != null ? getLabel() : ""), -1, getLine(), getColumn());
			ContextualNotification contextual = new ContextualNotification(notification, location);
			Notifications notifications = getXmlgen().getNotifications();
			notifications.add(contextual);
		}
	}

	protected State currrentState()
	{
		return states.peek();
	}

	/** The duplicate data source reference. */
	private Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, Gravity.Error,
			Subject.DataSource, Message.Duplicate_Reference);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5198212129556107335L;

	/** The datasources Ids. */
	private Vector<String> datasourcesIDs;

	private Stack<State> states = new Stack<State>();

	protected class State extends IterativeInstruction.State
	{
		public State()
		{
			super();
		}

		public Vector<Iterator<Object>> getIterators()
		{
			return iterators;
		}

		public void setIterators(Vector<Iterator<Object>> iterators)
		{
			this.iterators = iterators;
		}

		private boolean isInitialized()
		{
			return isInitialized;
		}

		private void setInitialized()
		{
			isInitialized = true;
		}

		/** The iterators. */
		private Vector<Iterator<Object>> iterators;
		private boolean isInitialized = false;
	}

	/** parsed capture queries */
	private Vector<AstResult> captureQueries;

	@Override
	protected void createState(ExpansionContext expansionContext)
	{
		State currentState = new State();
		states.push(currentState);
	}

	@Override
	protected void deleteState()
	{
		states.pop();
	}

	@Override
	protected State currentState()
	{
		return states.peek();
	}

	@Override
	protected boolean thereIsNoState()
	{
		return states.isEmpty();
	}
}
