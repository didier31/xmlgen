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
	private float level = 0;

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
	public float getLevel()
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
		String levelString = Math.floor(level) == level ? Integer.toString((int) level).toString() : Float.toString(level);  
		return "frame <" 
            + (name != null ? name : "") 
            +  ">, level " 
            +  levelString
            + " "
            + keySet().toString();
	}

	public void setLevel(float level)
	{
		this.level = level;
	}
}
