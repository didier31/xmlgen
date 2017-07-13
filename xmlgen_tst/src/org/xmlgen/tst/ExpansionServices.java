package org.xmlgen.tst;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.image.ImageFileFormat;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ExpansionServices 
{
	public String svg(EObject object, String outputDir, String imagesPath, int width, int height) 
	{
		if (object != null) 
		{
			Diagram diagram = (Diagram) object;

			CopyToImageUtil util = new CopyToImageUtil();
			try 
			{
				byte[] stream = util.copyToImageByteArray(diagram, width, height, ImageFileFormat.SVG, new NullProgressMonitor(), null, false);				
				SAXBuilder jdomBuilder = new SAXBuilder();
				jdomBuilder.setJDOMFactory(new LocatedJDOMFactory());
				Document svgDoc = jdomBuilder.build(new ByteArrayInputStream(stream));
				
				XMLOutputter xml = new XMLOutputter();
				xml.setFormat(Format.getPrettyFormat());
				
				if (svgDoc != null)
				{
					String svgFilename = generateSvgFilename(diagram);
					PrintStream xmlOutput = new PrintStream(outputDir + File.separator + imagesPath + File.separator + svgFilename);
					xmlOutput.println(xml.outputString(svgDoc));
					xmlOutput.close();
					return imagesPath + '/' + svgFilename;
				}
				else
				{
					return "";
				}
				
			} 
			catch (JDOMException | IOException | CoreException e) 
			{
				return "";
			}
		} 
		else 
		{
			return "";
		}
	}
	
	protected String generateSvgFilename(Diagram diagram)
	{
		return Integer.toHexString(diagram.hashCode()) + ".svg";
	}
}
