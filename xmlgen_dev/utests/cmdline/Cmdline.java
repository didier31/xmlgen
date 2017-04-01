package cmdline;

import static org.junit.Assert.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class Cmdline {

	@Test
	public void test() {
		/* // create a CharStream that reads from standard input
		ANTLRInputStream input = new ANTLRInputStream(System.in);
		// create a lexer that feeds off of input CharStream
		//CmdlineLexer lexer = new CmdlineLexer(input);
		// create a buffer of tokens pulled from the lexer 
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// create a parser that feeds off the tokens buffer 
		CmdlineParser parser = new CmdlineParser(tokens);
		ParseTree tree = parser.init(); // begin parsing 
		
		System.out.println(tree.toStringTree(parser)); // print LISP-style tree */
	}

}
