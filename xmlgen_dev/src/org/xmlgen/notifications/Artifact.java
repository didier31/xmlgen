package org.xmlgen.notifications;

public class Artifact 
{
	protected String name;

	public Artifact(String name) 
	{
		this.name = name;
	}

	public Artifact(Artifact artifact) 
	{
		this(artifact != null ? artifact.getName() : null);
	}
	
	public String getName()
	{
		return name;
	}
}