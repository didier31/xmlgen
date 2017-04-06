package org.xmlgen.notifications;

import javax.xml.stream.Location;

public class LocationImpl extends Artefact implements Location {

	private int characterOffset, columnNumber, lineNumber;
	
	public LocationImpl(Artefact artefact, int characterOffset, int columnNumber, int lineNumber) 
	{
		super(artefact);
		this.characterOffset = characterOffset;
		this.columnNumber = columnNumber;
		this.lineNumber = lineNumber;
	}

	
	@Override
	public int getCharacterOffset() 
	{
		return characterOffset;
	}


	@Override
	public int getColumnNumber() 
	{
		return columnNumber;
	}

	@Override
	public int getLineNumber() 
	{
		return lineNumber;
	}

	@Override
	public String getPublicId() {
		return null;
	}

	@Override
	public String getSystemId() {
		return null;
	}
}
