/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Jndi.java,v 1.1 2010/06/18 16:44:07 smhuang Exp $
 */

package org.exolab.jms.config;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Jndi.
 * 
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:44:07 $
 */
public class Jndi implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _initialContextClass
     */
    private java.lang.String _initialContextClass;


      //----------------/
     //- Constructors -/
    //----------------/

    public Jndi() {
        super();
    } //-- org.exolab.jms.config.Jndi()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'initialContextClass'.
     * 
     * @return the value of field 'initialContextClass'.
     */
    public java.lang.String getInitialContextClass()
    {
        return this._initialContextClass;
    } //-- java.lang.String getInitialContextClass() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'initialContextClass'.
     * 
     * @param initialContextClass the value of field
     * 'initialContextClass'.
     */
    public void setInitialContextClass(java.lang.String initialContextClass)
    {
        this._initialContextClass = initialContextClass;
    } //-- void setInitialContextClass(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static org.exolab.jms.config.Jndi unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.exolab.jms.config.Jndi) Unmarshaller.unmarshal(org.exolab.jms.config.Jndi.class, reader);
    } //-- org.exolab.jms.config.Jndi unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
