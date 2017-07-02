/*
 * 
 */
package org.xmlgen.notifications;

import javax.xml.stream.Location;

// TODO: Auto-generated Javadoc
/**
 * The Class LocationImpl.
 */
public class LocationImpl extends Artifact implements Location
{

	/** The line number. */
	private int characterOffset, columnNumber, lineNumber;

	/**
	 * Instantiates a new location impl.
	 *
	 * @param artefact
	 *           the artefact
	 * @param characterOffset
	 *           the character offset
	 * @param columnNumber
	 *           the column number
	 * @param lineNumber
	 *           the line number
	 */
	public LocationImpl(Artifact artefact, int characterOffset, int columnNumber, int lineNumber)
	{
		super(artefact);
		this.characterOffset = characterOffset;
		this.columnNumber = columnNumber;
		this.lineNumber = lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.Location#getCharacterOffset()
	 */
	@Override
	public int getCharacterOffset()
	{
		return characterOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.Location#getColumnNumber()
	 */
	@Override
	public int getColumnNumber()
	{
		return columnNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.Location#getLineNumber()
	 */
	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.Location#getPublicId()
	 */
	@Override
	public String getPublicId()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.Location#getSystemId()
	 */
	@Override
	public String getSystemId()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + ":" + getLineNumber() + ":" + getColumnNumber() + " ";
	}
}
