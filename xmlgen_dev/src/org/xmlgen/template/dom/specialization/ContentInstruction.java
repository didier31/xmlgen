package org.xmlgen.template.dom.specialization;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.jdom2.located.LocatedProcessingInstruction;
import org.xmlgen.expansion.pi.parsing.InstructionParser;

abstract public class ContentInstruction extends ExpansionInstruction 
{	
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
