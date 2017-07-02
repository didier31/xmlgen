package org.xmlgen.context;

import java.util.HashMap;

/**
 * The class Frame
 */
public class Frame extends HashMap<String, Object>
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 652656178360546296L;

	/** The name. */
	private String name;

	/** The level. */
	private int level = 0;

	/**
	 * Instantiates a new frame.
	 *
	 * @param name
	 *           the name
	 */
	public Frame(String name)
	{
		super();
		this.name = name;
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

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}
	
	@Override
	public Object put(String key, Object value)
	{
		return super.put(key, value);
	}
	
	@Override
	public String toString()
	{
		return "frame <" 
            + (name != null ? name : "") 
            +  ">, level " + level + " "
            + keySet().toString();
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
}
