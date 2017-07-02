package org.xmlgen.notifications;

// TODO: Auto-generated Javadoc
/**
 * The Class Artifact.
 */
public class Artifact
{

	/** The name. */
	protected String name;

	/**
	 * Instantiates a new artifact.
	 *
	 * @param name
	 *           the name
	 */
	public Artifact(String name)
	{
		this.name = name;
	}

	/**
	 * Instantiates a new artifact.
	 *
	 * @param artifact
	 *           the artifact
	 */
	public Artifact(Artifact artifact)
	{
		this(artifact != null ? artifact.getName() : null);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	@Override
	public String toString()
	{
		return name != null ? name : "" ;
	}
}