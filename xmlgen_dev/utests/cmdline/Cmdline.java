package cmdline;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.xmlgen.parser.cmdline.SmartCmdlineParser;

public class Cmdline {

	@Test
	public void test() {
		String[] vargs = {"--output", "c:\\User\\didier"};
		SmartCmdlineParser parser = new SmartCmdlineParser(null);
		ParseTree tree = parser.cmdline(); // begin parsing 
		
		//System.out.println(tree.toStringTree(parser)); // print LISP-style tree
	}

}
