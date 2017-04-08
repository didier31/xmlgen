package cmdline;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.xmlgen.context.Context;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.parser.cmdline.SmartCmdlineParser;

public class TestCmdline {
	
	public String toString(Notifications notifications)
	{
		String string = "";
		
		for (Notification notification : notifications)
		{
			String specific = "";
			if (notification instanceof ContextualNotification)
			{
				ContextualNotification specNotification = (ContextualNotification) notification;
				Artefact artefact = specNotification.getLocation();
				specific = artefact.getName();
				if (artefact instanceof LocationImpl)
				{					
					LocationImpl location = (LocationImpl) artefact;
					specific += ":o" + location.getCharacterOffset() + ":l" + location.getLineNumber() + ":c" + location.getColumnNumber();
				}
			}
			
			string += notification.getGravity() 
			  + "|" + notification.getModule() 
			  + "|" + notification.getSubject()
			  + "|" + specific
			  + "|" + notification.getMessage()
			  + "\n";
		}
	return string;
	}
	
	protected void prepareRun()
	{
		String methodName = new Exception().getStackTrace()[1].getMethodName();

		odir = new File("utests"+ File.separator 
                      + "cmdline" + File.separator
                      + "output" + File.separator
                      + methodName + File.separator);
			
		if (odir.isDirectory())
		{
			odir.delete();
		}
	    odir.mkdir();
	}
	
	protected void createFiles(String ... filesPaths) throws IOException
	{
		for (String filePath : filesPaths)
		{
			File file = new File(filePath.substring(1, filePath.length()-1));
			try 
			{
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	
	protected void deleteFiles(String ... filesPaths)
	{
		for (String filePath : filesPaths)
		{
			File file = new File(filePath.substring(1, filePath.length()-1));
			file.delete();
		}
	}
	
	protected void run(String[] vargs, String ...filesToCreatePaths) throws IOException
	{
		createFiles(filesToCreatePaths);			
		run(vargs);
		deleteFiles(filesToCreatePaths);
	}
	
	protected void run(String[] vargs) throws IOException
	{
		Context.clear();
		Notifications.getInstance().clear();		
		
		SmartCmdlineParser parser = new SmartCmdlineParser(vargs);
		
		PrintStream err = new PrintStream(new File(odir, "stderr"));		
		System.setErr(err);
		
		ParseTree tree = parser.parse();
		
		Context context = Context.getInstance();
		context.check();
		
		PrintStream output = new PrintStream(new File(odir, "stdout"));
		output.println("context = " + context.toString());
		output.println(tree.toStringTree(parser));
		err.println(toString(Notifications.getInstance()));
		output.close();
	}
	
	private String cdir = "utests/cmdline/input/common/";
	private File odir;
	
	@Test
	public void nominal_1_1_with_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
	}
	
	@Test
	public void nominal_1_2_without_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String template = "'" + cdir + "_template'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "--template", template,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, template);
	}
	
	@Test
	public void error_2_1_duplicate_reference() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source=", dataSource1, 
				          "data_source", "=", dataSource2, 
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, template, schema);
	}
	
	@Test
	public void error_2_2_template_missing() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source=", dataSource1, 
				          "dataSource", "=", dataSource2, 
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, schema);
	}	
	
	@Test
	public void error_2_3_output_missing() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "=" + dataSource3,
				          "--template", template,
				          "--schema", schema };
		
		run(vargs, dataSource1, dataSource2, dataSource3, schema, template);
	}	
	
	@Test
	public void error_2_4_data_source_missing() throws IOException
	{
		prepareRun();
		
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, schema, template);
	}
	
	@Test
	public void error_2_5_ds_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "not found file'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "=" + dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, schema, template);
	}
	
	@Test
	public void error_2_6_ds_rddenied() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "readdenied_datasource'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		createFiles(dataSource1, dataSource2, dataSource3, schema, template);
		File datasource_denied = new File(dataSource1.substring(1, dataSource1.length()-1));
		datasource_denied.setReadable(false, false);
		run(vargs);
		datasource_denied.setReadable(true, false);
		deleteFiles(dataSource1, dataSource2, dataSource3, schema, template);
	}
	
	@Test
	public void error_2_7_schema_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template);
	}
	
	@Test
	public void error_2_8_template_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, schema);
	}

	@Test
	public void error_2_9_output_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + "hypothetic directory" + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "=" + dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
	}
	
	@Test
	public void error_2_10_output_is_a_dir() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + "'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
	}
	
	@Test
	public void error_2_11_outputdir_wr_denied() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		odir.setWritable(false);
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
		odir.setWritable(true);
	}	
	
	@Test
	public void error_3_1_syntax_error() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1", dataSource1,    // Syntax error : is 'equal sign has been omitted'.
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
	}
	
	@Test
	public void error_3_2_lexical_error() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"6939data_source1=", dataSource1,   // lexical error : is 'an identifier never begins with digits'.
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
	}
	
	@Test
	public void error_3_3_lexical_error() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "data source1'";
		String dataSource2 = "'" + cdir + "data source2'";
		String dataSource3 = "'" + cdir + "data source3'";
		String template = "'" + cdir + "_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "_dataSource2", "=", dataSource2, 
				          "datasource3", "="+dataSource3,
				          "--tempete", template,   // misspelled --template option
				          "--schema", schema,
				          "--output", output };
		
		run(vargs, dataSource1, dataSource2, dataSource3, template, schema);
	}	
}
