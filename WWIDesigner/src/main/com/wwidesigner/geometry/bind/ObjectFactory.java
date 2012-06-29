//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.02 at 10:20:05 AM MDT 
//

package com.wwidesigner.geometry.bind;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.wwidesigner.geometry.bind package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory
{

	private final static QName _Instrument_QNAME = new QName(
			"http://www.wwidesigner.com/Instrument", "instrument");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.wwidesigner.geometry.bind
	 * 
	 */
	public ObjectFactory()
	{
	}

	/**
	 * Create an instance of {@link XmlMouthpiece }
	 * 
	 */
	public XmlMouthpiece createXmlMouthpiece()
	{
		return new XmlMouthpiece();
	}

	/**
	 * Create an instance of {@link XmlInstrument }
	 * 
	 */
	public XmlInstrument createXmlInstrument()
	{
		return new XmlInstrument();
	}

	/**
	 * Create an instance of {@link XmlBorePoint }
	 * 
	 */
	public XmlBorePoint createXmlBorePoint()
	{
		return new XmlBorePoint();
	}

	/**
	 * Create an instance of {@link XmlEndBoreSection }
	 * 
	 */
	public XmlEndBoreSection createXmlEndBoreSection()
	{
		return new XmlEndBoreSection();
	}

	/**
	 * Create an instance of {@link XmlHole }
	 * 
	 */
	public XmlHole createXmlHole()
	{
		return new XmlHole();
	}

	/**
	 * Create an instance of {@link XmlMoreThanZero }
	 * 
	 */
	public XmlMoreThanZero createXmlMoreThanZero()
	{
		return new XmlMoreThanZero();
	}

	/**
	 * Create an instance of {@link XmlZeroOrMore }
	 * 
	 */
	public XmlZeroOrMore createXmlZeroOrMore()
	{
		return new XmlZeroOrMore();
	}

	/**
	 * Create an instance of {@link XmlKey }
	 * 
	 */
	public XmlKey createXmlKey()
	{
		return new XmlKey();
	}

	/**
	 * Create an instance of {@link XmlDimension }
	 * 
	 */
	public XmlDimension createXmlDimension()
	{
		return new XmlDimension();
	}

	/**
	 * Create an instance of {@link XmlMouthpiece.EmbouchureHole }
	 * 
	 */
	public XmlMouthpiece.EmbouchureHole createXmlMouthpieceEmbouchureHole()
	{
		return new XmlMouthpiece.EmbouchureHole();
	}

	/**
	 * Create an instance of {@link XmlMouthpiece.Fipple }
	 * 
	 */
	public XmlMouthpiece.Fipple createXmlMouthpieceFipple()
	{
		return new XmlMouthpiece.Fipple();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link XmlInstrument }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.wwidesigner.com/Instrument",
			name = "instrument")
	public JAXBElement<XmlInstrument> createInstrument(XmlInstrument value)
	{
		return new JAXBElement<XmlInstrument>(_Instrument_QNAME,
				XmlInstrument.class, null, value);
	}

}