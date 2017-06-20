/*
 * 
 */
package org.xmlgen.template.dom.specialization;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.expansion.pi.parsing.InstructionParser;

// TODO: Auto-generated Javadoc
/**
 * The Class ContentInstruction.
 */
abstract public class ContentInstruction extends ExpansionInstruction
{

	/**
	 * Instantiates a new content instruction.
	 *
	 * @param pi
	 *           the pi
	 * @param queryText
	 *           the query text
	 */
	protected ContentInstruction(LocatedProcessingInstruction pi, String queryText)
	{
		super(pi);
		AstResult compiledQuery = InstructionParser.parseQuery(queryText, pi);
		if (compiledQuery.getErrors().isEmpty())
		{
			setCompiledQuery(compiledQuery);
		}
		else
		{
			setCompiledQuery(null);
		}
	}

	/**
	 * Eval.
	 *
	 * @return the object
	 */
	public Object eval()
	{
		return eval(getCompiledQuery());
	}

	/**
	 * Sets the compiled query.
	 *
	 * @param compiledQuery
	 *           the new compiled query
	 */
	protected void setCompiledQuery(AstResult compiledQuery)
	{
		this.compiledQuery = compiledQuery;
	}

	/**
	 * Gets the compiled query.
	 *
	 * @return the compiled query
	 */
	protected AstResult getCompiledQuery()
	{
		return compiledQuery;
	}

	/** The compiled query. */
	private AstResult compiledQuery;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1774007182013573180L;
}
