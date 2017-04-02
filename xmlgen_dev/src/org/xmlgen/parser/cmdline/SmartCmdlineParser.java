/**
 * 
 */
package org.xmlgen.parser.cmdline;

import java.io.StringReader;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;

/**
 * @author didier
 *
 */
public class SmartCmdlineParser extends CmdlineParser 
{
	static protected CommonTokenStream get(String[] vargs)
	{
		StringReader sr = new StringReader("");
		UnbufferedCharStream ucs = new UnbufferedCharStream(sr);
		CmdlineLexer lexer = new CmdlineLexer(ucs);
		return new CommonTokenStream(lexer);
	}
	
	public SmartCmdlineParser(String[] vargs) 
	{
		super(get(vargs));
	}

}
