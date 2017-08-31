package org.xmlgen.template.dom.specialization.instructions;

import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Context;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.parser.pi.PIParser.ExpressionContext;
import org.xmlgen.parser.pi.PIParser.TemplateImportContext;

@SuppressWarnings("serial")
public class ImportInstruction extends ExpansionInstruction
{

	protected ImportInstruction(String data, TemplateImportContext templateImportContext, int line, int column,
			Xmlgen xmlgen)
	{
		super(data, line, column, xmlgen);
		ExpressionContext filenameExpression = templateImportContext.expression();
		if (filenameExpression != null)
		{
			filenameParsedExpression = InstructionParser.parseQuery(getText(data, filenameExpression), line, column);
		}
		else
		{
			filenameParsedExpression = null;
			// TODO : notify an error about the type
		}
	}

	@Override
	public Vector<Cloneable> expandMySelf(TemplateIterator it)
	{
		Xmlgen xmlgen = getXmlgen();
		ExpansionContext expansionContext = xmlgen.getExpansionContext();
		boolean isExecuting = expansionContext.isExecuting();
		if (isExecuting && filenameParsedExpression != null)
		{
			Object parameterValue = eval(filenameParsedExpression);
			if (parameterValue instanceof String)
			{
				String xmlfilename = (String) parameterValue;
				Context context = getXmlgen().getContext();
				context.readTemplate(xmlfilename);
			}
		}
		return new Vector<Cloneable>(0);
	}

	private AstResult filenameParsedExpression;
}
