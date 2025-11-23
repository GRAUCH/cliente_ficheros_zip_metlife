package main.java.com.scortelemed.clientes.ficheros.zip.metlife.generacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import com.scortelemed.servicios.*;
import org.apache.log4j.Logger;
import org.apache.commons.configuration.Configuration;
import main.java.com.scortelemed.clientes.ficheros.zip.metlife.util.Mail;
import org.apache.axis.client.Call;
public class ConsultaFrontal {

	String frontalPort;
	Usuario usuario;


	public ConsultaFrontal(String frontalPort, String dominio, String unidadOrganizativa, String usuarioFrontal, String clave) {
		this.frontalPort = frontalPort;
		this.usuario = new Usuario(clave, dominio, unidadOrganizativa, usuarioFrontal);
	}
	
	public ConsultaFrontal(Configuration config) {
		this.frontalPort = config.getString("frontalPort_address");
		this.usuario = new Usuario(config.getString("clave"), config.getString("dominio"), config.getString("unidadOrganizativa"), config.getString("usuario"));
	}
	
	/**
	 * 
	 * @param cia
	 * @param finicio
	 * @param ffin
	 * @param log
	 * @param mail
	 * @param propiedadesMail
	 * @return list of Elemento
	 */
	public List<Elemento> consultaFinalizados(String cia, String finicio, String ffin, String estado, Logger log, Mail mail, Properties propiedadesMail) {

		List<Elemento> salida = new ArrayList<>();
		Map<String,Expediente> expedientes = new LinkedHashMap<>();

		try {

			FrontalServiceLocator fs = new FrontalServiceLocator();
			fs.setFrontalPortEndpointAddress(frontalPort);
			Frontal frontal = fs.getFrontalPort();
			((Stub) frontal)._setProperty(Call.CONNECTION_TIMEOUT_PROPERTY, 9000); // 5 segundos para conectar

			if(estado == null) {
				log.info("Consultado expediente para fechas: " + finicio + "-" + ffin);
				//Tipo 10 Cerrados
				RespuestaCRMInforme respuestaCerrados = frontal.informeExpedientesSiniestros(usuario, cia, null, Integer.toString(10), finicio, ffin);
				expedientes.putAll(procesarLista(respuestaCerrados));
				//Tipo 32 Rechazados
				RespuestaCRMInforme respuestaRechazados = frontal.informeExpedientesSiniestros(usuario, cia, null, Integer.toString(32), finicio, ffin);
				expedientes.putAll(procesarLista(respuestaRechazados));
			} else {
				log.info("Consultado expediente para fechas: " + finicio + "-" + ffin+" y estado "+estado);
				//Tipo Personalizado
				RespuestaCRMInforme respuesta = frontal.informeExpedientesSiniestros(usuario, cia, null, estado, finicio, ffin);
				expedientes.putAll(procesarLista(respuesta));
			}
			log.info("Numero de elementos devueltos: " + expedientes.size());

			if (!expedientes.isEmpty()) {

				for (Expediente actual: expedientes.values()) {

					Elemento elemento = new Elemento();
					log.info("Consultando expediente con codigo st: " + actual.getCodigoST());

					String error = verificaSiniestro(actual, finicio, ffin);

					if (error.isEmpty()) {
						elemento.setLiteral(getLiteral(actual.getListaSiniestros(0).getSiniestroNN(), actual.getFechaUltimoCambioEstado()));
						elemento.setDocumentos(Arrays.asList(actual.getListaDocumentos()));
						elemento.setNodo(actual.getNodoAlfresco());

						salida.add(elemento);
						
						log.info("Expediente con codigo st: " + actual.getCodigoST() + " se ha añadido a la lista de expedientes a comprimir");

					} else {
						log.error("No se ha generado informacion para expediente: " + actual.getCodigoST() + ". Error: " + error);
						
						mail.enviarMail((String) propiedadesMail.getProperty("destinatariosNotificacion"), null,
								"NN zips generation errors ( " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ")",
								"No se ha generado informacion para expediente: " + actual.getCodigoST() + ". Error: " + error,
								(String) propiedadesMail.getProperty("ipMailAddress"), (String) propiedadesMail.getProperty("scorFrom"), "text/plain", log);
						break;
					}
				}
			} else {
				log.info("No se han encontrado expedientes para fechas: " + finicio + "-" + ffin);
			}

		} catch (ServiceException e) {
			log.error("Service Exception: " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}

		return salida;
	}
	
	/**
	 * 
	 * @param expediente
	 * @return error or empty if there is no error
	 * @throws ParseException 
	 */
	private String verificaSiniestro(Expediente expediente, String fechaIni, String fechaFin) throws ParseException {
		String error = "";
		if (expediente.getListaSiniestros() == null || expediente.getListaSiniestros().length <= 0) {
			error = "Expediente no ha devuelto siniestros";
		} else if (expediente.getListaSiniestros(0).getSiniestroNN() == null || expediente.getListaSiniestros(0).getSiniestroNN().isEmpty()) {
			error = "Expediente no tiene codigoNN de siniestro";
		} else if (expediente.getFechaUltimoCambioEstado() == null	|| expediente.getFechaUltimoCambioEstado().isEmpty()) {
			error = "Expediente no tiene fecha de cierre";
		} else if (fueraDeFecha(expediente.getFechaUltimoCambioEstado(), fechaIni, fechaFin)) {
			error = "Expediente ha sufrido una modificación posterior a su cierre";
		} else if (expediente.getListaDocumentos() == null || expediente.getListaDocumentos().length <= 0) {
			error = "Expediente no tiene documentos asociados";
		} else if (expediente.getNodoAlfresco() == null || expediente.getNodoAlfresco().isEmpty()) {
			error = "Expediente no tiene nodo de alfresco";
		}
		return error;
	}
	
	private boolean fueraDeFecha(String fechaCierre, String fechaIni, String fechaFin) throws ParseException {
		boolean salida = false;
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		Date dateClosed = format1.parse(fechaCierre);
		Date dateIni = format2.parse(fechaIni);
		Date dateFin = format2.parse(fechaFin);
		
		if(dateIni.compareTo(dateClosed) > 0 || dateFin.compareTo(dateClosed) < 0) {
			salida = true;
		}

		return salida;
	}
	
	private Map<String, Expediente> procesarLista(RespuestaCRMInforme respuesta) {
		Map<String,Expediente> salida = new LinkedHashMap<>();

		if(respuesta != null && respuesta.getNumeroElementos() > 0) {
			for(Expediente actual: respuesta.getListaExpedientes()) {
				salida.put(actual.getCodigoST(), actual);
			}
		}
		return salida;
	}
	
	private String getLiteral(String id, String fecha) throws ParseException {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat format2 = new SimpleDateFormat("ddMMyyyy");
		Date date = format1.parse(fecha);
		String result = id + "_" + format2.format(date);
		return sb.append(result).toString();
	}

}
