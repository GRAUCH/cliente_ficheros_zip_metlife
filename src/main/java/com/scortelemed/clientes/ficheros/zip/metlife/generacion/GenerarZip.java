package main.java.com.scortelemed.clientes.ficheros.zip.metlife.generacion;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import main.java.com.scor.www.comprimir.documentos.siniestros.ComprimirDocumentosSiniestros_PortType;
import main.java.com.scor.www.comprimir.documentos.siniestros.ComprimirDocumentosSiniestros_ServiceLocator;
import main.java.com.scor.www.comprimir.documentos.siniestros.DatosZIP;
import main.java.com.scor.www.comprimir.documentos.siniestros.ParametrosEntrada;
import main.java.com.scor.www.comprimir.documentos.siniestros.ParametrosSalida;
import main.java.com.scortelemed.clientes.ficheros.zip.metlife.util.Unzip;

class GenerarZip {

	private String usuario;
	private String rutaSalidaFichero;
	private String password;
	private String portAddress;
	private String portServiceName;

	public GenerarZip(String usuario, String password, String rutaSalidaFichero, String portAddress, String portServiceName) {
		this.rutaSalidaFichero = rutaSalidaFichero;
		this.password = password;
		this.usuario = usuario;
		this.portAddress = portAddress;
		this.portServiceName = portServiceName;
	}

	public GenerarZip(Configuration alfresco) {
		this.rutaSalidaFichero = alfresco.getString("rutaSalidaZips");
		this.password = alfresco.getString("passAlfresco");
		this.usuario = alfresco.getString("usuarioAlfresco");
		this.portAddress = alfresco.getString("portAddress");
		this.portServiceName = alfresco.getString("portServiceName");
	}

	/**
	 * GCB. 18/12/2013: Cambio introducido para evitar que se graben zips con cero
	 * bytes.
	 * 
	 */
	public int generarZIP(Elemento elemento, Logger log) {

		Unzip unzip = new Unzip();
		int codigoResultado = 0;
		if(elemento != null) {
			String mascaraFichero = elemento.getLiteral() + "." + elemento.getExtension();
	
			try {
	
				ParametrosEntrada parametrosEntrada = new ParametrosEntrada();
				parametrosEntrada.setUsuario(usuario);
				parametrosEntrada.setClave(password);
				parametrosEntrada.setRefNodo(elemento.getNodo());
				ParametrosSalida parametrosSalida = new ParametrosSalida();
	
				ComprimirDocumentosSiniestros_ServiceLocator comprimirDocumentosServiceLocator = new ComprimirDocumentosSiniestros_ServiceLocator(
						portAddress, portServiceName);
				ComprimirDocumentosSiniestros_PortType port = comprimirDocumentosServiceLocator
						.getComprimirDocumentosSiniestrosPort();
	
				parametrosSalida = port.process(parametrosEntrada);
	
				DatosZIP datosZip = parametrosSalida.getDatosRespuesta();
	
				byte[] compressedData = datosZip.getContent();
				FileOutputStream fos = new FileOutputStream(rutaSalidaFichero + mascaraFichero);
				if (compressedData.length != 0) {
					fos.write(compressedData);
					fos.close();
	
					/**
					 * GCB. 04/02/2014: Cambio introducido para evitar que se guarden zips que no se
					 * pueden descomprimir
					 * 
					 */
					codigoResultado = unzip.unzipFile(rutaSalidaFichero + mascaraFichero, log);
					if (codigoResultado == 2) {
						/**
						 * FALLO POR QUE LOS DOCUMENTOS DEL EXPEDIENTE NO TIENEN UNA URL DE ALFRESCO
						 * ASOCIADA
						 * 
						 */
						log.info("INFO: Zip generated corruptly: Document does not have a valid Alfresco url address. "
								+ mascaraFichero);
	
					} else if (codigoResultado == 1) {
						log.info("INFO: Zip generated corruptly: Document with size error. " + mascaraFichero);
	
					} else if (codigoResultado == 3) {
						/**
						 * ESTE CASO SE DEBERIA COMPORTAR COMO SI FUERA ZIP GENERADO CON CERO BYTES
						 * 
						 */
						log.info("INFO: Zip generated whit no documents. " + mascaraFichero);
						codigoResultado = 0;
					} else {
						codigoResultado = 3;
						log.info("INFO: Zip generated correctly. " + mascaraFichero);
					}
				} else {
					log.info("INFO: Zip generated whit zero. " + mascaraFichero);
					codigoResultado = 0;
				}
			} catch (ServiceException e) {
				log.error("ServiceException in class " + getClass().getName() + ". Zip not generated: "+mascaraFichero+". Message: " + e.getMessage());
			} catch (RemoteException e) {
				log.error("RemoteException in class " + getClass().getName() + ". Zip not generated: "+mascaraFichero+". Message: " + e.getMessage());
			} catch (IOException e) {
				log.error("IOException in class " + getClass().getName() + ". Zip not generated: "+mascaraFichero+". Message: " + e.getMessage());
			} catch (OutOfMemoryError e) {
				codigoResultado = -1;
				log.error("OutOfMemoryError in class " + getClass().getName() + ". Zip not generated: "+mascaraFichero+". Message: " + e.getMessage());
			} catch (Exception e) {
				log.error("Exception in class " + getClass().getName() + ". Zip not generated: "+mascaraFichero+". Message: " + e.getMessage());
			}
		}

		return codigoResultado;
	}
}
