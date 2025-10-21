/**
 * ComprimirDocumentosSiniestros_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package main.java.com.scor.www.comprimir.documentos.siniestros;

import java.rmi.Remote;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;

public class ComprimirDocumentosSiniestros_ServiceLocator extends Service
		implements ComprimirDocumentosSiniestros_Service {
	
	private static final String namespaceURI = "http://www.scor.com/ComprimirDocumentosSiniestros";
	
	// Use to get a proxy class for ComprimirDocumentosSiniestrosPort
	private String comprimirDocumentosSiniestrosPortAddress = "http://SCOR0-BPM01:8888/orabpel/default/ComprimirDocumentosSiniestros/1.0";

	// The WSDD service name defaults to the port name.
	private String comprimirDocumentosSiniestrosPortWSDDServiceName = "ComprimirDocumentosSiniestrosPort";

	public ComprimirDocumentosSiniestros_ServiceLocator(String portAddress, String portServiceName) {
		comprimirDocumentosSiniestrosPortAddress = portAddress;
		comprimirDocumentosSiniestrosPortWSDDServiceName = portServiceName;
	}

	public ComprimirDocumentosSiniestros_ServiceLocator(EngineConfiguration config) {
		super(config);
	}

	public ComprimirDocumentosSiniestros_ServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	public String getComprimirDocumentosSiniestrosPortAddress() {
		return comprimirDocumentosSiniestrosPortAddress;
	}

	public void setComprimirDocumentosSiniestrosPortEndpointAddress(String address) {
		comprimirDocumentosSiniestrosPortAddress = address;
	}

	public String getComprimirDocumentosSiniestrosPortWSDDServiceName() {
		return comprimirDocumentosSiniestrosPortWSDDServiceName;
	}

	public void setComprimirDocumentosSiniestrosPortWSDDServiceName(String name) {
		comprimirDocumentosSiniestrosPortWSDDServiceName = name;
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(String portName, String address)
			throws javax.xml.rpc.ServiceException {

		if (comprimirDocumentosSiniestrosPortWSDDServiceName.equals(portName)) {
			setComprimirDocumentosSiniestrosPortEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}
	
	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, String address)
			throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

	public ComprimirDocumentosSiniestros_PortType getComprimirDocumentosSiniestrosPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(comprimirDocumentosSiniestrosPortAddress);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getComprimirDocumentosSiniestrosPort(endpoint);
	}

	public ComprimirDocumentosSiniestros_PortType getComprimirDocumentosSiniestrosPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			ComprimirDocumentosSiniestrosBindingStub _stub = new ComprimirDocumentosSiniestrosBindingStub(portAddress, this);
			_stub.setPortName(getComprimirDocumentosSiniestrosPortWSDDServiceName());
			return _stub;
		} catch (AxisFault e) {
			return null;
		}
	}


	/**
	 * For the given interface, get the stub implementation. If this service has no
	 * port for the given interface, then ServiceException is thrown.
	 */
	public Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (ComprimirDocumentosSiniestros_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
				ComprimirDocumentosSiniestrosBindingStub _stub = new ComprimirDocumentosSiniestrosBindingStub(new java.net.URL(comprimirDocumentosSiniestrosPortAddress), this);
				_stub.setPortName(getComprimirDocumentosSiniestrosPortWSDDServiceName());
				return _stub;
			}
		} catch (Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
				+ (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no
	 * port for the given interface, then ServiceException is thrown.
	 */
	public Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		String inputPortName = portName.getLocalPart();
		if (comprimirDocumentosSiniestrosPortWSDDServiceName.equals(inputPortName)) {
			return getComprimirDocumentosSiniestrosPort();
		} else {
			Remote _stub = getPort(serviceEndpointInterface);
			((Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(namespaceURI,
				"ComprimirDocumentosSiniestros");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(namespaceURI, comprimirDocumentosSiniestrosPortWSDDServiceName));
		}
		return ports.iterator();
	}

}
