package main.java.com.scor.www.comprimir.documentos.siniestros;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

public class ComprimirDocumentosSiniestrosProxy	implements ComprimirDocumentosSiniestros_PortType {

	private static final String CLAVE = "javax.xml.rpc.service.endpoint.address";
	private String endpoint = null;
	private String portAddress = null;
	private String portServiceName = null;
	private ComprimirDocumentosSiniestros_PortType comprimirDocumentosSiniestrosPortType = null;

	public ComprimirDocumentosSiniestrosProxy(String endpoint, String portAddress, String portServiceName) {
		this.endpoint = endpoint;
		this.portAddress = portAddress;
		this.portServiceName = portServiceName;
		initComprimirDocumentosSiniestrosProxy();
	}

	private void initComprimirDocumentosSiniestrosProxy() {
		try {
			comprimirDocumentosSiniestrosPortType = (new ComprimirDocumentosSiniestros_ServiceLocator(portAddress, portServiceName))
					.getComprimirDocumentosSiniestrosPort();
			if (comprimirDocumentosSiniestrosPortType != null) {
				if (endpoint != null)
					((javax.xml.rpc.Stub) comprimirDocumentosSiniestrosPortType)
							._setProperty(CLAVE, endpoint);
				else
					endpoint = (String) ((javax.xml.rpc.Stub) comprimirDocumentosSiniestrosPortType)
							._getProperty(CLAVE);
			}

		} catch (ServiceException serviceException) {
			serviceException.printStackTrace();
		}
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
		if (comprimirDocumentosSiniestrosPortType != null)
			((javax.xml.rpc.Stub) comprimirDocumentosSiniestrosPortType)
					._setProperty(CLAVE, endpoint);

	}

	public ComprimirDocumentosSiniestros_PortType getComprimirDocumentosSiniestrosPortType() {
		if (comprimirDocumentosSiniestrosPortType == null)
			initComprimirDocumentosSiniestrosProxy();
		return comprimirDocumentosSiniestrosPortType;
	}

	public ParametrosSalida process(
			ParametrosEntrada payload) throws RemoteException {
		if (comprimirDocumentosSiniestrosPortType == null)
			initComprimirDocumentosSiniestrosProxy();
		return comprimirDocumentosSiniestrosPortType.process(payload);
	}

}