/**
 * 
 */
package org.xmlgen.parser.cmdline;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author didier
 *
 */
public class SmartCmdlineParser extends CmdlineParser 
{
	static protected CommonTokenStream get(String[] vargs)
	{
		String args = "";
		for (String arg : vargs)
		{
			args += ' ' + arg;
		}
		StringReader sr = new StringReader(args);
		ANTLRInputStream ucs = null;
		try {
			ucs = new ANTLRInputStream(sr);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		CmdlineLexer lexer = new CmdlineLexer(ucs);
		return new CommonTokenStream(lexer);
	}
	
	public SmartCmdlineParser(String[] vargs)
	{
		super(get(vargs));
	}

	public ParseTree parse() 
	{
		return cmdline();
	}

}
