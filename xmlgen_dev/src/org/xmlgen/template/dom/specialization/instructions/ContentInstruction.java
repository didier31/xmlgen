/*
 * 
 */
package org.xmlgen.template.dom.specialization.instructions;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
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
	protected ContentInstruction(String queryText, int line, int column)
	{
		super(queryText, line, column);
		AstResult compiledQuery = InstructionParser.parseQuery(queryText, line, column);
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
