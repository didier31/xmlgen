package org.xmlgen.context;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Holds the variables and constants for a given structure's instance.  
 * 
 * @author Didier Garcin
 * 
 */
@SuppressWarnings("serial")
public class Frame extends HashMap<String, Object>
{
	/** The name. */
	private String name;
	String level;

	/**
	 * 
	 */
	public void setLevel(String level)
	{
		this.level = level;
	}
	
	/**
	 * Instantiates a new frame.
	 *
	 * @param name : the name of the frame.
	 */
	public Frame(String name)
	{
		super();
		this.name = name;
	}

	/**
	 * Gets the name of the frame.
	 *
	 * @return its name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * Returns an overview of its state.
	 * 
	 * @return the string representation.  
	 * 
	 */
	@Override
	public String toString()
	{
		return "frame <" 
            + (name != null ? name : "") 
            +  ">, level " 
            +  level
            + " "
            + keySet().toString();
	}
	
	static private NumberFormat decimalFormat = NumberFormat.getInstance(Locale.US);
	
	static
	{
		decimalFormat.setMinimumFractionDigits(0);
		decimalFormat.setMaximumFractionDigits(1);
		decimalFormat.setMinimumIntegerDigits(0);
	}
}
