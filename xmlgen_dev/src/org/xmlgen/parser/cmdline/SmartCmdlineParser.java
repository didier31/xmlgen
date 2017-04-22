/**
 * 
 */
package org.xmlgen.parser.cmdline;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
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
		CmdlineLexer lexer = new CmdlineLexer(CharStreams.fromString(args));
		lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
		lexer.addErrorListener(new SyntaxErrorListener());
		return new CommonTokenStream(lexer);
	}
	
	public SmartCmdlineParser(String[] vargs)
	{
		super(get(vargs));
		removeErrorListener(ConsoleErrorListener.INSTANCE);
		addErrorListener(new SyntaxErrorListener());
	}

	public ParseTree parse() 
	{
		return cmdline();
	}

}
