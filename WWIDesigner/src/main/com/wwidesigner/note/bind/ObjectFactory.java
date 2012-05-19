//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.15 at 12:02:46 AM MDT 
//


package com.wwidesigner.note.bind;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.wwidesigner.note.bind package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Tuning_QNAME = new QName("http://www.wwidesigner.com/Tuning", "tuning");
    private final static QName _FingeringPattern_QNAME = new QName("http://www.wwidesigner.com/Tuning", "fingeringPattern");
    private final static QName _Scale_QNAME = new QName("http://www.wwidesigner.com/Tuning", "scale");
    private final static QName _ScaleSymbolList_QNAME = new QName("http://www.wwidesigner.com/Tuning", "scaleSymbolList");
    private final static QName _Temperament_QNAME = new QName("http://www.wwidesigner.com/Tuning", "temperament");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.wwidesigner.note.bind
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XmlScale }
     * 
     */
    public XmlScale createXmlScale() {
        return new XmlScale();
    }

    /**
     * Create an instance of {@link XmlScaleSymbolList }
     * 
     */
    public XmlScaleSymbolList createXmlScaleSymbolList() {
        return new XmlScaleSymbolList();
    }

    /**
     * Create an instance of {@link XmlTemperament }
     * 
     */
    public XmlTemperament createXmlTemperament() {
        return new XmlTemperament();
    }

    /**
     * Create an instance of {@link XmlTuning }
     * 
     */
    public XmlTuning createXmlTuning() {
        return new XmlTuning();
    }

    /**
     * Create an instance of {@link XmlFingeringPattern }
     * 
     */
    public XmlFingeringPattern createXmlFingeringPattern() {
        return new XmlFingeringPattern();
    }

    /**
     * Create an instance of {@link XmlFingering }
     * 
     */
    public XmlFingering createXmlFingering() {
        return new XmlFingering();
    }

    /**
     * Create an instance of {@link XmlNote }
     * 
     */
    public XmlNote createXmlNote() {
        return new XmlNote();
    }

    /**
     * Create an instance of {@link XmlScale.Note }
     * 
     */
    public XmlScale.Note createXmlScaleNote() {
        return new XmlScale.Note();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlTuning }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.wwidesigner.com/Tuning", name = "tuning")
    public JAXBElement<XmlTuning> createTuning(XmlTuning value) {
        return new JAXBElement<XmlTuning>(_Tuning_QNAME, XmlTuning.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlFingeringPattern }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.wwidesigner.com/Tuning", name = "fingeringPattern")
    public JAXBElement<XmlFingeringPattern> createFingeringPattern(XmlFingeringPattern value) {
        return new JAXBElement<XmlFingeringPattern>(_FingeringPattern_QNAME, XmlFingeringPattern.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlScale }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.wwidesigner.com/Tuning", name = "scale")
    public JAXBElement<XmlScale> createScale(XmlScale value) {
        return new JAXBElement<XmlScale>(_Scale_QNAME, XmlScale.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlScaleSymbolList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.wwidesigner.com/Tuning", name = "scaleSymbolList")
    public JAXBElement<XmlScaleSymbolList> createScaleSymbolList(XmlScaleSymbolList value) {
        return new JAXBElement<XmlScaleSymbolList>(_ScaleSymbolList_QNAME, XmlScaleSymbolList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlTemperament }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.wwidesigner.com/Tuning", name = "temperament")
    public JAXBElement<XmlTemperament> createTemperament(XmlTemperament value) {
        return new JAXBElement<XmlTemperament>(_Temperament_QNAME, XmlTemperament.class, null, value);
    }

}