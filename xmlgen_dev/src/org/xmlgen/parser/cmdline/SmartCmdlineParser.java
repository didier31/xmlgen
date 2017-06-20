/**
 * 
 */
package org.xmlgen.parser.cmdline;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;

// TODO: Auto-generated Javadoc
/**
 * The Class SmartCmdlineParser.
 *
 * @author didier
 */
public class SmartCmdlineParser extends CmdlineParser
{

	/**
	 * Gets the.
	 *
	 * @param vargs
	 *           the vargs
	 * @return the common token stream
	 */
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

	/**
	 * Instantiates a new smart cmdline parser.
	 *
	 * @param vargs
	 *           the vargs
	 */
	public SmartCmdlineParser(String[] vargs)
	{
		super(get(vargs));
		removeErrorListener(ConsoleErrorListener.INSTANCE);
		addErrorListener(new SyntaxErrorListener());
	}

	/**
	 * Parses the.
	 *
	 * @return the parses the tree
	 */
	public ParseTree parse()
	{
		return cmdline();
	}

}
