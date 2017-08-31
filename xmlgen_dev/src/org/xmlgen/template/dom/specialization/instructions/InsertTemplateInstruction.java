package org.xmlgen.template.dom.specialization.instructions;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.jdom2.Content;
import org.jdom2.located.Located;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Context;
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.pi.parsing.InstructionParser;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
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
				String id = getId();

				Message message = new Message("Unknown " + " template reference '" + id + "'");
				Notification notification = new Notification(Module.Expansion, Gravity.Warning, Subject.Template, message);
				Xmlgen xmlgen = getXmlgen();
				Context context = xmlgen.getContext();
				Artifact artefact = new Artifact(context.getXmlTemplate());
				LocationImpl location = new LocationImpl(artefact, -1, getLine(), getColumn());
				ContextualNotification contextualNotification = new ContextualNotification(notification, location);
				Notifications notifications = getXmlgen().getNotifications();
				notifications.add(contextualNotification);
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
			Message message = new Message("Unknown template's identificator '" + templateId + "'");
			Notification notification = new Notification(Module.Expansion, Gravity.Error, Subject.Template, message);
			Context context = xmlgen.getContext();
			Artifact artefact = new Artifact(context.getXmlTemplate());
			LocationImpl location = new LocationImpl(artefact, -1, getLine(), getColumn());
			ContextualNotification contextualNotification = new ContextualNotification(notification, location);
			Notifications notifications = getXmlgen().getNotifications();
			notifications.add(contextualNotification);

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

		evaluateEffectiveParameters();
		boolean isSuccess = templateDef.identifyPositionalParams(effectiveParameters);

		if (isSuccess)
		{
			// Push parameters frame
			FrameStack contextualFrameStack = getXmlgen().getFrameStack();
			Frame parametersFrame;
			final String nameOfParametersFrame = "parameters of " + getId();
			if (templateDef.isPure())
			{
				FrameStack pureFrameStack = new FrameStack(nameOfParametersFrame);
				parametersFrame = pureFrameStack.peek();
				getXmlgen().setFrameStack(pureFrameStack);
			}
			else
			{
				parametersFrame = new Frame(nameOfParametersFrame);
			}
			contextualFrameStack.push(parametersFrame);
			// Passing parameters
			copyEffectiveParametersOnStack();
			// Expand the template
			expanded = body.expandMySelf(recursiveIt, false);
			// if any, make jump exports over the parameters frame.
			propagateExports(body);
			getXmlgen().setFrameStack(contextualFrameStack);
			// Pop parameters frame
			popFrame((Located) recursiveIt.current());
		}
		else
		{
			expanded = new Vector<Cloneable>(0);
		}
		return expanded;
	}

	/**
	 * 
	 */
	protected void evaluateEffectiveParameters()
	{
		// Evaluate effective parameters
		for (EffectiveParameter effectiveParameter : effectiveParameters)
		{
			effectiveParameter.setValue(eval(effectiveParameter.getExpression()));
		}
	}

	/**
	 * @param body
	 */
	protected void propagateExports(Element body)
	{
		int lastContentIdx = body.getContentSize() - 1;
		Content lastContent = body.getContent(lastContentIdx);
		if (lastContent instanceof EndInstruction)
		{
			EndInstruction endInstruction = (EndInstruction) lastContent;
			endInstruction.exports();
		}
	}

	/**
	 * 
	 */
	protected void copyEffectiveParametersOnStack()
	{
		for (EffectiveParameter effectiveParameter : effectiveParameters)
		{
			String paramId = effectiveParameter.getId();
			Object paramValue = effectiveParameter.getValue();
			addToCurrentFrame(paramId, paramValue);
		}
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
