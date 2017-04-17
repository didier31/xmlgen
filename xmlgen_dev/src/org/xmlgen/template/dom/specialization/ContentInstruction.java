package org.xmlgen.template.dom.specialization;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.w3c.dom.ProcessingInstruction;

abstract public class ContentInstruction extends ExpansionInstruction 
{	
	protected ContentInstruction(ProcessingInstruction pi, String queryText) 
   {
      super(pi);
		AstResult compiledQuery = parseQuery(queryText);		
		if (compiledQuery.getErrors().isEmpty())
		{
			setCompiledQuery(compiledQuery);
		}
		else
		{
			setCompiledQuery(null);
		}
   }   
	
	public Object eval()
	{
		 return eval(getCompiledQuery());
	}
   
	protected void setCompiledQuery(AstResult compiledQuery)
	{
		this.compiledQuery = compiledQuery;
	}

	protected AstResult getCompiledQuery()
	{
		return compiledQuery;
	}
	
   private AstResult compiledQuery;
   
	private static final long serialVersionUID = -1774007182013573180L;   
}
