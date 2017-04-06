package org.xmlgen.notifications;

public class Artefact 
{
	protected String name;

	public Artefact(String name) 
	{
		this.name = name;
	}

	public Artefact(Artefact artefact) 
	{
		this(artefact.getName());
	}
	
	public String getName()
	{
		return name;
	}
}