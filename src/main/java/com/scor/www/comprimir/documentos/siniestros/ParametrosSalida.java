/**
 * ParametrosSalida.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package main.java.com.scor.www.comprimir.documentos.siniestros;

public class ParametrosSalida  implements java.io.Serializable {
    private java.lang.String codigo;

    private java.lang.String descripcion;

    private DatosZIP datosRespuesta;

    private DetalleError detalleError;

    public ParametrosSalida() {
    }

    public ParametrosSalida(
           java.lang.String codigo,
           java.lang.String descripcion,
           DatosZIP datosRespuesta,
           DetalleError detalleError) {
           this.codigo = codigo;
           this.descripcion = descripcion;
           this.datosRespuesta = datosRespuesta;
           this.detalleError = detalleError;
    }


    /**
     * Gets the codigo value for this ParametrosSalida.
     * 
     * @return codigo
     */
    public java.lang.String getCodigo() {
        return codigo;
    }


    /**
     * Sets the codigo value for this ParametrosSalida.
     * 
     * @param codigo
     */
    public void setCodigo(java.lang.String codigo) {
        this.codigo = codigo;
    }


    /**
     * Gets the descripcion value for this ParametrosSalida.
     * 
     * @return descripcion
     */
    public java.lang.String getDescripcion() {
        return descripcion;
    }


    /**
     * Sets the descripcion value for this ParametrosSalida.
     * 
     * @param descripcion
     */
    public void setDescripcion(java.lang.String descripcion) {
        this.descripcion = descripcion;
    }


    /**
     * Gets the datosRespuesta value for this ParametrosSalida.
     * 
     * @return datosRespuesta
     */
    public DatosZIP getDatosRespuesta() {
        return datosRespuesta;
    }


    /**
     * Sets the datosRespuesta value for this ParametrosSalida.
     * 
     * @param datosRespuesta
     */
    public void setDatosRespuesta(DatosZIP datosRespuesta) {
        this.datosRespuesta = datosRespuesta;
    }


    /**
     * Gets the detalleError value for this ParametrosSalida.
     * 
     * @return detalleError
     */
    public DetalleError getDetalleError() {
        return detalleError;
    }


    /**
     * Sets the detalleError value for this ParametrosSalida.
     * 
     * @param detalleError
     */
    public void setDetalleError(DetalleError detalleError) {
        this.detalleError = detalleError;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ParametrosSalida)) return false;
        ParametrosSalida other = (ParametrosSalida) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codigo==null && other.getCodigo()==null) || 
             (this.codigo!=null &&
              this.codigo.equals(other.getCodigo()))) &&
            ((this.descripcion==null && other.getDescripcion()==null) || 
             (this.descripcion!=null &&
              this.descripcion.equals(other.getDescripcion()))) &&
            ((this.datosRespuesta==null && other.getDatosRespuesta()==null) || 
             (this.datosRespuesta!=null &&
              this.datosRespuesta.equals(other.getDatosRespuesta()))) &&
            ((this.detalleError==null && other.getDetalleError()==null) || 
             (this.detalleError!=null &&
              this.detalleError.equals(other.getDetalleError())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCodigo() != null) {
            _hashCode += getCodigo().hashCode();
        }
        if (getDescripcion() != null) {
            _hashCode += getDescripcion().hashCode();
        }
        if (getDatosRespuesta() != null) {
            _hashCode += getDatosRespuesta().hashCode();
        }
        if (getDetalleError() != null) {
            _hashCode += getDetalleError().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParametrosSalida.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", ">parametrosSalida"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", "codigo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descripcion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", "descripcion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("datosRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", "datosRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", "datosZIP"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detalleError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", "detalleError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.scor.com/ComprimirDocumentosSiniestros", "detalleError"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
