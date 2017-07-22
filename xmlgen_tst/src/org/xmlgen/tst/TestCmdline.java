/*
 * 
 */
package org.xmlgen.tst;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import org.junit.Test;
import org.xmlgen.context.Context;
import org.xmlgen.Xmlgen;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notifications;

// TODO: Auto-generated Javadoc
/**
 * The Class TestCmdline.
 */
public class TestCmdline {
	
	/**
	 * Fixed length string.
	 *
	 * @param string the string
	 * @param length the length
	 * @return the string
	 */
	public String fixedLengthString(String string, int length) 
	{
	    return String.format("%1$"+length+ "s", string);
	}
	
	/**
	 * To string.
	 *
	 * @param notifications the notifications
	 * @return the string
	 */
	public String toString(Notifications notifications)
	{
		String string = "";
			
		for (Notification notification : notifications)
		{
			String specific = "";
			if (notification instanceof ContextualNotification)
			{
				ContextualNotification specNotification = (ContextualNotification) notification;
				Artifact artifact = specNotification.getLocation();
				specific = artifact.getName();
				if (artifact instanceof LocationImpl)
				{					
					LocationImpl location = (LocationImpl) artifact;
					specific += ":o" + location.getCharacterOffset() + ":l" + location.getLineNumber() + ":c" + location.getColumnNumber();
				}
			}
			
			string += fixedLengthString(notification.getGravity().toString(), 6) 
			  + "|" + fixedLengthString(notification.getModule().toString(), 9) 
			  + "|" + fixedLengthString(notification.getSubject().toString(), 11)
			  + "|" + fixedLengthString(specific, 12)
			  + "|" + fixedLengthString(notification.getMessage().toString(), 40)
			  + "\n"; 		
		}

	HashMap<Gravity, Integer> counts = notifications.getCounts();	
		
 	for (Gravity g : Gravity.values())
 	{
 		string += g.toString() + ":" + counts.get(g) + "    ";
 	}
	
 	string += "\n";
 	
	return string;
	}
	
	/**
	 * Prepare run.
	 */
	protected void prepareRun()
	{
		String methodName = new Exception().getStackTrace()[1].getMethodName();
	
		odir = new File(homePath
                      + "output" + File.separator
                      + methodName + File.separator);
			
		if (odir.isDirectory())
		{
			odir.delete();
		}
	    odir.mkdir();
	}
	
	/**
	 * Run.
	 *
	 * @param vargs the vargs
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void run(String[] vargs) throws IOException
	{
		PrintStream err = new PrintStream(new File(odir, "stderr"));		
		System.setErr(err);
		
		new Xmlgen().perform(vargs, null);
		
		Context context = Context.getInstance();
		
		PrintStream output = new PrintStream(new File(odir, "stdout"));
		output.println("context = " + context.toString());
		output.close();
		
		err.println("\n" + toString(Notifications.getInstance()));
	}
	
	private final String homePath = System.getProperty("user.dir") + "/src/org/xmlgen/tst/"; 
	
	/** The cdir. */
	private String cdir = homePath + "/input/common/";
	
	/** The odir. */
	private File odir;
	
	/**
	 * Nominal 1 1 with schema.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void nominal_1_1_with_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output,
				            "--services", ""};
		
		run(vargs); 
	}
	
	/**
	 * Nominal 1 2 without schema.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void nominal_1_2_without_schema() throws IOException
	{
		prepareRun();
		
		String uml = "'/" + cdir + "design.uml'";
		String notation = "'" + cdir + "design.notation'";
		String info = "'/" + cdir + "info.xml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + odir.getPath() + File.separator + "output.xml'";
		
		String[] vargs = { "uml=", uml,
				           "notation=", notation,
				           "info=", info,			
				            "--template", template,
				            "--trace",
				            "--services", "org.xmlgen.tst.ExpansionServices",
				            "--output", output};
		
		run(vargs);
	}

	/**
	 * Nominal 1 3 with schema and http template.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void nominal_1_3_with_schema_and_http_template() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'https://raw.githubusercontent.com/didier31/xmlgen/master/xmlgen_dev/utests/cmdline/input/common/docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Nominal 1 4 http datasource.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void nominal_1_4_http_datasource() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'https://raw.githubusercontent.com/didier31/xmlgen/master/xmlgen_dev/utests/cmdline/input/common/design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Nominal 1 5 with rng schema.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void nominal_1_5_with_rng_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook5.1_template.xml'";
		String schema = "'http://docbook.org/xml/5.1/rng/docbook.rng'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Nominal 1 6 with rnc schema.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void nominal_1_6_with_rnc_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook5.1_template.xml'";
		String schema = "'http://docbook.org/xml/5.1/rng/docbook.rnc'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 2 1 duplicate reference.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_1_duplicate_reference() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "data_source1=", dataSource1,
				            "--template", template,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 2 2 template missing.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_2_template_missing() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}	
	
	/**
	 * Error 2 3 output missing.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_3_output_missing() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template 
				           };
		
		run(vargs);
	}	
	
	/**
	 * Error 2 4 data source missing.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_4_data_source_missing() throws IOException
	{
		prepareRun();
		
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {
				            "--template", template,
				            "--schema", schema,
				            "--output", output 
				           };
		
		run(vargs);
	}
	
	/**
	 * Error 2 5 ds not found.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_5_ds_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "mispelledFile.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 2 6 ds rddenied.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_6_ds_rddenied() throws IOException
	{
		prepareRun();
		
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
		
		File datasource_denied = new File(dataSource1.substring(1, dataSource1.length()-1));
		datasource_denied.setReadable(false, false);
		run(vargs);
		datasource_denied.setReadable(true, false);
	}
	
	/**
	 * Error 2 7 schema not found.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_7_schema_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd_mispelled'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 2 8 template not found.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_8_template_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "fantasy_template'";
		String schema = "'" + cdir + "schema'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "--template", template,
				          "--schema", schema,
				          "--output", output };
		
		run(vargs);
	}

	/**
	 * Error 2 9 output not found.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_9_output_not_found() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + "hypothetic directory" + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "--template", template,
				          "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 2 10 output is a dir.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_10_output_is_a_dir() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + "'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
		
		prepareRun();
	}
	
	/**
	 * Error 2 11 outputdir wr denied.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_2_11_outputdir_wr_denied() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String schema = "'http://docbook.org/xml/5.0/xsd/docbook.xsd'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		odir.setWritable(false);
		run(vargs);
		odir.setWritable(true);
	}	
	
	/**
	 * Error 3 1 syntax error.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_3_1_syntax_error() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1", dataSource1,    // Syntax error : is 'equal sign has been omitted'.
				          "--template", template,
				          "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 3 2 lex err incorrect dsid.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_3_2_lex_err_incorrect_dsid() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"6939data_source1=", dataSource1,   // lexical error : is 'an identifier never begins with digits'.
				          "--template", template,
				          "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 3 3 lex err.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_3_3_lex_err() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook_template.xml'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				          "--tempate", template,   // misspelled --template option
				          "--output", output };
		
	run(vargs);
	}	
	
	/**
	 * Error 4 1 with rng schema.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_4_1_with_rng_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook5.1_template_title_missing.xml'";
		String schema = "'http://docbook.org/xml/5.1/rng/docbook.rng'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}
	
	/**
	 * Error 4 2 with rnc schema.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void error_4_2_with_rnc_schema() throws IOException
	{
		prepareRun();
		
		String dataSource1 = "'" + cdir + "design.uml'";
		String template = "'" + cdir + "docbook5.1_template_title_missing.xml'";
		String schema = "'http://docbook.org/xml/5.1/rng/docbook.rnc'";
		String output = "'" + odir.getPath() + File.separator + "output'";
		
		String[] vargs = {"data_source1=", dataSource1, 
				            "--template", template,
				            "--schema", schema,
				            "--output", output };
		
		run(vargs);
	}	
}
