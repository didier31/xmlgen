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
	private int level;

	/**
	 * Instantiates a new frame.
	 *
	 * @param name
	 *           the name
	 * @param level
	 *           the level
	 */
	public Frame(String name, int level)
	{
		super();
		this.name = name;
		this.level = level;
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
}
