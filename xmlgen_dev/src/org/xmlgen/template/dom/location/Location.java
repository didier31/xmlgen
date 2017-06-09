/*
 * 
 */
package org.xmlgen.template.dom.location;

// TODO: Auto-generated Javadoc
/**
 * The Class Location.
 */
public class Location
{

	/** The Constant LOCATION. */
	public static final String LOCATION = "locationKey";

	/**
	 * Instantiates a new location.
	 *
	 * @param systemId
	 *           the system id
	 * @param startLine
	 *           the start line
	 * @param startColumn
	 *           the start column
	 * @param endLine
	 *           the end line
	 * @param endColumn
	 *           the end column
	 */
	public Location(String systemId, int startLine, int startColumn, int endLine, int endColumn)
	{
		super();
		this.systemId = systemId;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}

	/**
	 * Gets the system id.
	 *
	 * @return the system id
	 */
	public String getSystemId()
	{
		return systemId;
	}

	/**
	 * Gets the start line.
	 *
	 * @return the start line
	 */
	public int getStartLine()
	{
		return startLine;
	}

	/**
	 * Gets the start column.
	 *
	 * @return the start column
	 */
	public int getStartColumn()
	{
		return startColumn;
	}

	/**
	 * Gets the end line.
	 *
	 * @return the end line
	 */
	public int getEndLine()
	{
		return endLine;
	}

	/**
	 * Gets the end column.
	 *
	 * @return the end column
	 */
	public int getEndColumn()
	{
		return endColumn;
	}

	/** The system id. */
	private final String systemId;

	/** The start line. */
	private final int startLine;

	/** The start column. */
	private final int startColumn;

	/** The end line. */
	private final int endLine;

	/** The end column. */
	private final int endColumn;
}