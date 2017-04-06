package cmdline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.xmlgen.context.Context;
import org.xmlgen.parser.cmdline.SmartCmdlineParser;

public class TestCmdline {
	
	public void run(String[] vargs) throws FileNotFoundException
	{
		SmartCmdlineParser parser = new SmartCmdlineParser(vargs);
		// begin parsing 		
		String methodName = new Exception().getStackTrace()[1].getMethodName();

		File dir = new File("utests"+ File.separator 
                          + "cmdline" + File.separator
                          + "output" + File.separator
                          + methodName);
			
	    boolean success = dir.mkdir();
		
		PrintStream err = new PrintStream(new File(dir, "stderr"));		
		System.setErr(err);
		
		ParseTree tree = parser.parse();
		
		Context context = Context.getInstance();
		
		PrintStream output = new PrintStream(new File(dir, "output"));
		output.println(tree.toStringTree(parser));
		output.println("context = " + context.toString());
		output.close();
	}
	
	@Test
	public void nominal_1_1() throws FileNotFoundException
	{
		String[] vargs = {"data_source='0/TT'", "data_source1='file1'", "_data_source2", "=", "'dir\\file2'", "", "--output", "'TOTOT'", "--template", "'c:\\repo\\'"};
		run(vargs);
	}
	
	@Test
	public void nominal_1_2() throws FileNotFoundException
	{
		String[] vargs = {"data_source='0/TT'", "data_source1='file1'", "_data_source2", "=", "'dir\\file2'", "", "--output", "'TOTOT'"};
		run(vargs);
	}
}
