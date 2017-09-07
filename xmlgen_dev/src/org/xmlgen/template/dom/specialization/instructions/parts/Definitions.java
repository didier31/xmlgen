package org.xmlgen.template.dom.specialization.instructions.parts;

import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.parser.pi.PIParser.DefinitionContext;
import org.xmlgen.parser.pi.PIParser.DefinitionsContext;
import org.xmlgen.parser.pi.PIParser.ExpressionContext;
import org.xmlgen.template.dom.specialization.instructions.ExpansionInstruction;

public class Definitions
{
	public Definitions(DefinitionsContext definitionsContext, int line, int column, ExpansionInstruction motherInstruction)
	{
		this.motherInstruction = motherInstruction;
		initDefinitions(definitionsContext, line, column);		
	}

	public void setDefinitions()
	{
		// Initializes references in the just new created frame in stack
		int i = 0;
		for (AstResult definitionQuery : definitionsQueries)
		{
			Object result = null;
			if (definitionQuery.getErrors().isEmpty())
			{
				result = motherInstruction.eval(definitionQuery);
			}
			motherInstruction.addToCurrentFrame(datasourcesIDs.get(i), result);
			i++;
		}
	}
	
	/**
	 * @param definitionsContext
	 * @param line
	 * @param column
	 */
	protected void initDefinitions(DefinitionsContext definitionsContext, int line, int column)
	{
		final int definitionsCount = definitionsContext == null ? 0 : definitionsContext.definition().size();
		datasourcesIDs = new Vector<String>(definitionsCount);
		definitionsQueries = new Vector<AstResult>(definitionsCount);

		if (definitionsContext != null)
		{
			for (DefinitionContext definition : definitionsContext.definition())
			{
				ExpressionContext expression = definition.expression();
				String queryToParse = ExpansionInstruction.getText(motherInstruction.getData(), expression);
				AstResult parsedQuery = InstructionParser.parseQuery(queryToParse, line, column);
				String id = definition.dataID().getText();
				datasourcesIDs.add(id);
				definitionsQueries.add(parsedQuery);
			}
		}
	}
	
	private Vector<AstResult> definitionsQueries;
   private Vector<String> datasourcesIDs;
   
   private ExpansionInstruction motherInstruction;
}
