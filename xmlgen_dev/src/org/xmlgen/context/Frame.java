package org.xmlgen.context;

import java.util.HashMap;

public class Frame extends HashMap<String, Object> 
{
	private String name;	
	private int level;
	
	public Frame(String name, int level) {
		super();
		this.name = name;
		this.level = level;
	}

	public String getName()
	{
		return name;
	}
	
	public int getLevel()
	{
		return level;
	}
}
