package org.xmlgen.template.dom.specialization.instructions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.acceleo.query.ast.Call;
import org.eclipse.acceleo.query.ast.Conditional;
import org.eclipse.acceleo.query.ast.Expression;
import org.eclipse.acceleo.query.ast.Let;
import org.eclipse.acceleo.query.ast.Literal;
import org.eclipse.acceleo.query.ast.TypeLiteral;
import org.eclipse.acceleo.query.ast.VarRef;
import org.eclipse.acceleo.query.ast.impl.ErrorTypeLiteralImpl;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.emf.ecore.EClassifier;
import org.xmlgen.Xmlgen;
import org.xmlgen.context.Context;
import org.xmlgen.dom.template.TemplateIterator;
import org.xmlgen.expansion.ExpansionContext;
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
import org.xmlgen.parser.pi.PIParser.ParameterContext;
import org.xmlgen.parser.pi.PIParser.TemplateDefContext;
import org.xmlgen.template.dom.specialization.instructions.InsertTemplateInstruction.EffectiveParameter;

@SuppressWarnings({ "serial", "restriction" })
public class TemplateDef extends StructuralInstruction
{
	public void setExecutable()
	{
		notExecutable = false;
	}
	
	public boolean isPure()
	{
		return isPure;
	}
	
	public boolean identifyPositionalParams(EffectiveParameter[] effectiveParameters)
	{
		boolean isSuccess = effectiveParameters.length == formalParameters.length;
		
		List<EffectiveParameter> effectiveParams = new ArrayList<EffectiveParameter>(Arrays.asList(effectiveParameters));
		List<FormalParameter> formalParams = new ArrayList<FormalParameter>(Arrays.asList(formalParameters));

		isSuccess = discardNamedEffectiveParameters(isSuccess, effectiveParams, formalParams);		
		/*
		 * Identify positional parameters
		 */
		formalParams.removeIf(Objects::isNull);
		int idx = 0;
		while (idx < effectiveParams.size() && idx < formalParameters.length)
		{
			EffectiveParameter effectiveParameter = effectiveParams.get(idx); 
			FormalParameter formalParameter = formalParams.get(idx);
			boolean parametersAreCompatible = checkIsKindOf(formalParameter, effectiveParameter);
			if (parametersAreCompatible)
			{
				String id = formalParameter.getId();
				effectiveParameter.setId(id);
			}
			else				
			{
				isSuccess = false;
			}
			idx++;
		}	
		
		return isSuccess;
	}

	private boolean discardNamedEffectiveParameters(boolean isSuccess, List<EffectiveParameter> effectiveParams,
			List<FormalParameter> formalParams)
	{
		/*
		 * Discard all named parameters 
		 */
		int effectiveIdx = 0;
		while (effectiveIdx < effectiveParams.size())
		{
			EffectiveParameter effectiveParam = effectiveParams.get(effectiveIdx);
			String id = effectiveParam.getId();
			if (id != null)
			{
				boolean thisParamIdExists = formalParametersByName.containsKey(id); 
				if (thisParamIdExists)
				{
					FormalParameter formalParameter = formalParametersByName.get(id);
					boolean parametersAreCompatible = checkIsKindOf(formalParameter, effectiveParam);
					if (!parametersAreCompatible)
					{				
						isSuccess = false;
					}
					/*
					   Remove it from the effective/formal params lists.
					   At first, symbolically for formal parameters list.
					   To be able to use, initial idx stored with formal parameter.
					*/
					int idx = formalParameter.getIndex();
					formalParams.set(idx, null);
				}
				else
				{				
					notify(Gravity.Error, "Unknown parameter '" + id +  "'");					
					isSuccess = false;
				}
				/*
				 * Remove it from the effective params lists.
				 */
				effectiveParams.remove(effectiveIdx);
			}
			else
			{
				effectiveIdx++;
			}
		}
		return isSuccess;
	}

	public String getId()
	{
		return id;
	}
	
	public boolean checkTypeCompatibility(EffectiveParameter[] effectiveParameters)
	{
		assert(effectiveParameters.length == formalParameters.length);
		int i = 0;
		boolean isSuccess = true;
		while (i < effectiveParameters.length)
		{
			EffectiveParameter effectiveParameter = effectiveParameters[i];
			FormalParameter formalParameter = formalParameters[i];
			boolean isCorrect = checkIsKindOf(formalParameter, effectiveParameter);
			if (!isCorrect)
			{
				isSuccess = false;
			}
			i++;
		}
		return isSuccess;
	}
	
	protected boolean checkIsKindOf(FormalParameter formal, EffectiveParameter effective)
	{
		Object effectiveValue = effective.getValue();
		Object formalType = formal.getType();
		Class<?> formalClass;
		if (formalType instanceof EClassifier)
		{
			EClassifier formalEClass = (EClassifier) formalType;
			formalClass = formalEClass.getInstanceClass();
		}
		else if (formalType instanceof Class<?>)
		{
			formalClass = (Class<?>) formalType;
		}
		else
		{
			formalClass = null;
			assert(false);
		}
		
		boolean isKindOf = effectiveValue == null;
		Class<? extends Object> effectiveClass = null;
		if (!isKindOf)
		{
			effectiveClass = effectiveValue.getClass();	
			isKindOf = formalClass.isAssignableFrom(effectiveClass);
		}
		
		if (!isKindOf)
		{
			// Notify user that formal and effective parameters at formalParameter.Id() are not compatible.
			notify(Gravity.Error, "Formal parameter named '" + formal.getId() + "' is constrained to " + formal.getTypeAsString()
                             + ", found " + effectiveClass.getSimpleName() + " as effective parameter");
		}		
		return isKindOf; 
	}
	
	protected TemplateDef(String data, TemplateDefContext templateDef, int line, int column, Xmlgen xmlgen)
	{
		super(data, templateDef.Ident().getText(), line, column, xmlgen);
		isPure = templateDef.Pure() != null;
		id = templateDef.Ident().getText();
		List<ParameterContext> parameters = templateDef.parameter();
		formalParameters = new FormalParameter[parameters.size()];
		formalParametersByName = new HashMap<String, FormalParameter>(20);
		if (parameters != null)
		{
			int i = 0;
			for (ParameterContext parameter : parameters)
			{
				String id = parameter.Ident().getText();
				String clazzStr = getText(data, parameter.typeLiteral());
				AstResult result = InstructionParser.parseQuery(clazzStr, line, column);
				Expression expression = result.getAst();
				boolean isTypeReference = check_IsTypeReference(expression, i + 1, line, column);
				TypeLiteral typeReference;
				if (isTypeReference)
				{
					typeReference = (TypeLiteral) expression;
				}
				else
				{
					typeReference = new ErrorTypeLiteral();
					notify(Gravity.Error, "Unknown type of parameter '" + id + "' for template '" + getId() + "'");
				}
				FormalParameter formalParameter = new FormalParameter(i, id, typeReference, clazzStr);
				formalParameters[i] = formalParameter;
				if (formalParametersByName.containsKey(id))
				{
					notify(Gravity.Error, "Redefinition of parameter '" + id +  "' in template '" + getId() + "'.");
				}
				else
				{
					formalParametersByName.put(id, formalParameter);
				}
				i++;
			}
		}
		getXmlgen().addTemplate(this);
	}

	protected void notify(Gravity gravity, String messageStr)
	{
		Message message = new Message(messageStr);
		Notification notification = new Notification(Module.Expansion, gravity, Subject.Template, message);
		Xmlgen xmlgen = getXmlgen();
		Context context = xmlgen.getContext();
		Artifact artefact = new Artifact(context.getXmlTemplate());
		LocationImpl location = new LocationImpl(artefact, -1, getLine(), getColumn());
		ContextualNotification contextualNotification = new ContextualNotification(notification, location);
		Notifications notifications = getXmlgen().getNotifications();
		notifications.add(contextualNotification);
	}
	
	private boolean check_IsTypeReference(Expression expression, int position, int line, int column)
	{
		String errorMessage;
		if (expression instanceof TypeLiteral)
		{
			return true;
		}
		else if (expression instanceof Call)
		{
			errorMessage = "a call instruction";
		}
		else if (expression instanceof Conditional)
		{
			errorMessage = "a conditional instruction";
		}
		else if (expression instanceof Let)
		{
			errorMessage = "a let instruction";
		}
		else if (expression instanceof Literal)
		{
			errorMessage = "a literal";
		}
		else if (expression instanceof VarRef)
		{
			errorMessage = "a variable reference";
		}
		else
		{
			errorMessage = "an unreconized expression";
		}
		errorMessage += " found at position " + position + ", expecting a type for template definition '" + getId() + "'.";
		notify(Gravity.Error, errorMessage);
		
		return false;
	}		

	protected class FormalParameter
	{		
		protected FormalParameter(int index, String id, TypeLiteral type, String typeAsStr)
		{
			this.index = index;
			this.id = id;
			this.type = type;
			this.typeAsString = typeAsStr;
		}

		protected int getIndex()
		{
			return index;
		}
		
		protected String getId()
		{
			return id;
		}

		protected Object getType()
		{
			return type.getValue();
		}
		
		protected String getTypeAsString()
		{
			return typeAsString;
		}

		private int index;
		private String id;
		private TypeLiteral type;
		private String typeAsString;
	}
	
	class ErrorTypeLiteral extends ErrorTypeLiteralImpl
	{
	}

	@Override
	protected void createState(ExpansionContext expansionContext)
	{
		State currentState = new State();
		states.push(currentState);		
	}

	@Override
	protected State currentState()
	{
		return states.peek();
	}

	@Override
	protected void deleteState()
	{
		states.pop();
	}
	
	@Override
	protected boolean thereIsNoState()
	{
		return states.isEmpty();
	}

	@Override
	protected Vector<Cloneable> doExpandMySelf(TemplateIterator it)
	{
		if (notExecutable)
		{
			disableExecution();
		}
		return new Vector<Cloneable>(0);
	}
	
	private boolean notExecutable = true;
	private Stack<State> states = new Stack<State>();
	private String id;
	private boolean isPure;
	private FormalParameter[] formalParameters;
	private HashMap<String, FormalParameter> formalParametersByName;
}
