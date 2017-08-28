package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.jdom2.Content;
import org.jdom2.located.Located;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.parser.pi.PIParser.EffectiveParameterContext;
import org.xmlgen.parser.pi.PIParser.TemplateCallContext;
import org.xmlgen.template.dom.specialization.content.Element;

@SuppressWarnings("serial")
public class InsertTemplateInstruction extends InsertInstruction
{
	public InsertTemplateInstruction(String pi, TemplateCallContext templateCall, int line, int column, Xmlgen xmlgen)
	{
		super(pi, line, column, xmlgen);
		setId(templateCall.Ident().getText());
		initEffectiveParameters(pi, line, column, templateCall);
	}

	protected void setId(String id)
	{
		this.id = id;
	}

	protected String getId()
	{
		return id;
	}

	@Override
	protected Element getBody()
	{
		if (body == null)
		{
			TemplateDef templateDef = getTemplateDef();
			if (templateDef != null)
			{
				Collection<Content> structureContent = structureOf(templateDef);
				templateDef = (TemplateDef) structureContent.iterator().next();
				templateDef.setExecutable();
				body = new Element(getId(), getXmlgen());
				body.addContent(structureContent);
			}
			else
			{
				body = null;
				// TODO Notify an error to user : no template with this id
			}
		}
		return body;
	}

	protected TemplateDef getTemplateDef()
	{
		TemplateDef templateDef;
		String templateId = getId();
		Xmlgen xmlgen = getXmlgen();
		boolean constainsKey = xmlgen.containsTemplate(templateId);
		if (constainsKey)
		{
			templateDef = xmlgen.getTemplateDef(templateId);
		}
		else
		{
			// TODO Notify an error to user
			templateDef = null;
		}
		return templateDef;
	}

	protected void initEffectiveParameters(String pi, int line, int column, TemplateCallContext templateCall)
	{
		List<EffectiveParameterContext> parameters = templateCall.effectiveParameter();
		effectiveParameters = new EffectiveParameter[parameters.size()];
		int i = 0;
		for (EffectiveParameterContext effectiveParameter : parameters)
		{
			String id = effectiveParameter.Ident() != null ? effectiveParameter.Ident().getText() : null;
			String expressionStr = getText(pi, effectiveParameter.expression());
			AstResult parsedExpression = InstructionParser.parseQuery(expressionStr, line, column);
			effectiveParameters[i] = new EffectiveParameter(id, parsedExpression);
			i++;
		}
	}

	@Override
	protected Vector<Cloneable> expand(Element body, TemplateIterator recursiveIt)
	{
		TemplateDef templateDef = getTemplateDef();
		Vector<Cloneable> expanded;

		boolean isSuccess = templateDef.identifyPositionalParams(effectiveParameters);

		if (isSuccess)
		{
			// Evaluate effective parameters
			for (EffectiveParameter effectiveParameter : effectiveParameters)
			{
				effectiveParameter.setValue(eval(effectiveParameter.getExpression()));
			}

			isSuccess = templateDef.checkTypeCompatibility(effectiveParameters);
			if (isSuccess)
			{
				FrameStack frameStack = getXmlgen().getFrameStack();
				Frame parametersFrame = new Frame("parameters of " + getId());
				frameStack.push(parametersFrame);
				// Passing parameters
				for (EffectiveParameter effectiveParameter : effectiveParameters)
				{
					String paramId = effectiveParameter.getId();
					Object paramValue = effectiveParameter.getValue();
					addToCurrentFrame(paramId, paramValue);
				}
				expanded = body.expandMySelf(recursiveIt, false);
				popFrame((Located) recursiveIt.current());
			}
			else
			{
				expanded = new Vector<Cloneable>(0);
			}
		}
		else
		{
			expanded = new Vector<Cloneable>(0);
		}
		return expanded;
	}

	private String id;
	private EffectiveParameter[] effectiveParameters = new EffectiveParameter[0];

	public class EffectiveParameter
	{
		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public Object getValue()
		{
			return value;
		}

		public AstResult getExpression()
		{
			return expression;
		}

		private EffectiveParameter(String id, AstResult expression)
		{
			this.id = id;
			this.expression = expression;
		}

		private void setValue(Object value)
		{
			this.value = value;
		}

		private String id;
		private AstResult expression;
		private Object value = null;
	}
}
