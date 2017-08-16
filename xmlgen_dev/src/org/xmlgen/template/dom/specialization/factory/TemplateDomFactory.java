package org.xmlgen.template.dom.specialization.factory;

import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Namespace;
import org.jdom2.located.LocatedJDOMFactory;
import org.xmlgen.Xmlgen;
import org.xmlgen.template.dom.specialization.content.CDATA;
import org.xmlgen.template.dom.specialization.content.Comment;
import org.xmlgen.template.dom.specialization.content.DocType;
import org.xmlgen.template.dom.specialization.content.Element;
import org.xmlgen.template.dom.specialization.content.EntityRef;
import org.xmlgen.template.dom.specialization.content.ProcessingInstruction;
import org.xmlgen.template.dom.specialization.content.Template;
import org.xmlgen.template.dom.specialization.content.Text;
import org.xmlgen.template.dom.specialization.instructions.ExpansionInstruction;

public class TemplateDomFactory extends LocatedJDOMFactory
{
	public TemplateDomFactory(Xmlgen xmlgen)
	{
		this.xmlgen = xmlgen;
	}

	public Document document(Element rootElement, DocType docType)
	{
		Template template = new Template(rootElement, docType);
		return template;
	}

	/**
	 * This will create a new <code>Document</code>,
	 * with the supplied <code>{@link org.jdom2.Element}</code>
	 * as the root element and the supplied
	 * <code>{@link org.jdom2.DocType}</code> declaration.
	 *
	 * @param rootElement <code>Element</code> for document root.
	 * @param docType <code>DocType</code> declaration.
	 * @param baseURI the URI from which this doucment was loaded.
	 * @return the created Document instance
	 */
	@Override
	public Document document(org.jdom2.Element rootElement, org.jdom2.DocType docType, String baseURI)
	{
		Template template = new Template(rootElement, docType, baseURI);
		return template;
	}

	/**
	 * This will create a new <code>Document</code>,
	 * with the supplied <code>{@link org.jdom2.Element}</code>
	 * as the root element, and no <code>{@link org.jdom2.DocType}</code>
	 * declaration.
	 *
	 * @param rootElement <code>Element</code> for document root
	 * @return the created Document instance
	 */
	@Override
	public Document document(org.jdom2.Element rootElement)
	{
		Template template = new Template(rootElement);
		return template;
	}
	
	@Override
	public CDATA cdata(int line, int col, String text) {
		final CDATA ret = new CDATA(text, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public Text text(int line, int col, String text) {
		final Text ret = new Text(text, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public Comment comment(int line, int col, String text) {
		final Comment ret = new Comment(text, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public DocType docType(int line, int col, String elementName,
			String publicID, String systemID) {
		final DocType ret = new DocType(elementName, publicID, systemID);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public DocType docType(int line, int col, String elementName,
			String systemID) {
		final DocType ret = new DocType(elementName, systemID);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public DocType docType(int line, int col, String elementName) {
		final DocType ret = new DocType(elementName, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public Element element(int line, int col, String name, Namespace namespace) {
		final Element ret = new Element(name, namespace, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public Element element(int line, int col, String name) {
		final Element ret = new Element(name, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public Element element(int line, int col, String name, String uri) {
		final Element ret = new Element(name, uri, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public Element element(int line, int col, String name, String prefix,
			String uri) {
		final Element ret = new Element(name, prefix, uri, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public EntityRef entityRef(int line, int col, String name) {
		final EntityRef ret = new EntityRef(name, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public EntityRef entityRef(int line, int col, String name, String publicID,
			String systemID) {
		final EntityRef ret = new EntityRef(name, publicID, systemID, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public EntityRef entityRef(int line, int col, String name, String systemID) {
		final EntityRef ret = new EntityRef(name, systemID, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}
	
	
	@Override
	public ProcessingInstruction processingInstruction(int line, int col, java.lang.String target, java.lang.String data)
	{
		ProcessingInstruction processingInstruction;
		
		if (ExpansionInstruction.piMarker.compareToIgnoreCase(target) == 0)
		{
			processingInstruction = ExpansionInstruction.create(data, line, col, xmlgen);
		}
		else
		{
			processingInstruction = new ProcessingInstruction(target, data, xmlgen);
		}
		
		processingInstruction.setLine(line);
		processingInstruction.setColumn(col);
			
		return processingInstruction;
	}
	
	@Override
	public ProcessingInstruction processingInstruction(int line, int col,
			String target) {
		final ProcessingInstruction ret = new ProcessingInstruction(target, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}

	@Override
	public ProcessingInstruction processingInstruction(int line, int col,
			String target, Map<String, String> data) {
		final ProcessingInstruction ret = new ProcessingInstruction(target, data, xmlgen);
		ret.setLine(line);
		ret.setColumn(col);
		return ret;
	}
	
	private Xmlgen xmlgen;
}
