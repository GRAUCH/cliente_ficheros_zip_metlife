package main.java.com.scortelemed.clientes.ficheros.zip.metlife.generacion;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import main.java.com.scortelemed.clientes.ficheros.zip.metlife.util.Mail;
import main.java.com.scortelemed.clientes.ficheros.zip.metlife.util.Util;

public class GenerarFichero {
	
	private static Configuration config;
	private static Configuration alfresco;
	private static Properties propiedadesMail = new Properties();
	private static Mail mail;

	private static final String ERROR = "Exception in main class. ";
	private static final String DATE_FORMAT = "yyyyMMdd";
	public static final Logger log = Logger.getLogger(GenerarFichero.class);
	
	private static String estado = null;
	private static String finicio = null;
	private static String ffin = null;
	
	private static ConsultaFrontal frontal = null;
	private static GenerarZip generarZip = null;
	private static StringBuilder zipsErroneos = new StringBuilder();

	public static void main(String[] args) {
		try {

			inicializar();
			gestionarParametros(args);
			List<Elemento> salida = frontal.consultaFinalizados(config.getString("cia"), finicio, ffin, estado, log, mail, propiedadesMail);
			List<Elemento> salidaFallidos = crearZips(salida);
			List<Elemento> salidaRevisada = revisarZips(salidaFallidos, 10);
			notificarFallos(salidaRevisada);
			log.info("End process: " + new SimpleDateFormat("kk:mm:ss").format(new Date()));

		} catch (ConfigurationException e) {
			log.error("ConfigurationException" + ERROR + e.getMessage());
		} catch (IOException e) {
			log.error("IOException" + ERROR + e.getMessage());
		} catch (Exception e) {
			log.error(ERROR + e.getMessage());
		}
	}
	
	private static List<Elemento> crearZips(List<Elemento> entrada) {
		List<Elemento> salida = new ArrayList<>();
		int codigoError = 0;

		if (entrada != null && !entrada.isEmpty()) {
			log.info("Generando zips para "+entrada.size()+" elementos");
			for (Elemento actual : entrada) {
				codigoError = generarZip.generarZIP(actual, log);

				// Error 0 fallo externo, error 1 fichero con 0 bytes
				if (codigoError == 0 || codigoError == 1) {
					salida.add(actual);
					log.info("Zip failed: " + actual.getLiteral() + ".zip");
				} else if(codigoError == -1) {
					salida.add(actual);
					log.info("Zip failed by Out of Memory: " + actual.getLiteral() + ".zip");
				}
			}
		}
		return salida;
	}
	
	/**
	 * GCB. 18/12/2013: Cambio introducido para evitar que se graben zips con cero
	 * bytes o corruptos.
	 *
	 * @param entrada fallidos
	 * @return expedientes revisados
	 */
	private static List<Elemento> revisarZips(List<Elemento> entrada, int intentos) {
		List<Elemento> salida = new ArrayList<>();
		int codigoError = 0;

		if (entrada != null && !entrada.isEmpty()) {
			log.info("***********Execute faild ZIPS*************");
			for (Elemento actual : entrada) {
				boolean generado = false;
				int limite = 0;
				zipsErroneos.append(actual.getLiteral() + " ");

				do {
					log.info("Trying to execute failed Zip: " + actual.getLiteral() + ".zip");

					codigoError = generarZip.generarZIP(actual, log);

					// Error 0 fallo externo, error 1 fichero con 0 bytes
					if (codigoError == 0 || codigoError == 1) {
						limite++;
						log.info("Trying to execute failed Zip again. Try" + limite + " :"
								+ actual.getLiteral() + "_" + actual.getDocumentos() + ".zip");
						log.info("limite: " + limite);
					} else if(codigoError == -1) {
						log.info("Zip failed by Out of Memory: " + actual.getLiteral() + ".zip");
						limite = intentos;
					} else {
						generado = true;
					}

				} while (!generado && limite < intentos);
				
				if(!generado) {
					salida.add(actual);
				}
			}
		}
		return salida;
	}
	
	private static void notificarFallos(List<Elemento> entrada) {
		if (entrada != null && !entrada.isEmpty()) {
			log.info("ZIPS CER0: " + zipsErroneos);

			mail.enviarMail((String) propiedadesMail.getProperty("destinatariosNotificacion"), null,
					"NN zips generation errors ( " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ")",
					Util.generarMensajes(entrada, "Zips"), (String) propiedadesMail.getProperty("ipMailAddress"),
					(String) propiedadesMail.getProperty("scorFrom"), "text/plain", log);

		}
	}
	
	private static void gestionarParametros(String [] args) throws ParseException {
		
		Date date = null;

		/**
		 * Si se pasa dos parametros
		 * 
		 */
		if (args.length == 2) {

			finicio = args[0];
			ffin = args[1];

			/**
			 * Si no se pasa nada
			 * 
			 */
		} else if (args.length == 3) {
			finicio = args[0];
			ffin = args[1];
			estado = args[2];

		} else {

			SimpleDateFormat df2 = new SimpleDateFormat(DATE_FORMAT);
			Calendar calendar = Calendar.getInstance();

			String fecha = df2.format(calendar.getTime());

			if (Util.fechaEsDomingo(fecha, log)) {
				finicio = Util.calcularFechaViernes(fecha, log);
				ffin = finicio;

			} else {

				finicio = fecha;
				ffin = fecha;

			}

		}

		log.info("Starting process: " + new SimpleDateFormat("kk:mm:ss").format(new Date()));

		if (finicio == null) {

			finicio = new SimpleDateFormat(DATE_FORMAT).format(new Date());

		} else {

			date = new SimpleDateFormat(DATE_FORMAT).parse(finicio);
			finicio = new SimpleDateFormat(DATE_FORMAT).format(date);

		}

		if (ffin == null) {

			ffin = new SimpleDateFormat(DATE_FORMAT).format(new Date());

		} else {

			date = new SimpleDateFormat(DATE_FORMAT).parse(ffin);
			ffin = new SimpleDateFormat(DATE_FORMAT).format(date);

		}
	}
	
	private static void inicializar() throws ConfigurationException, IOException {
		config = new PropertiesConfiguration("conexion.properties");
		alfresco = new PropertiesConfiguration("conexionAlfresco.properties");
		propiedadesMail.load(new FileInputStream("direccionesMail.properties"));
		mail = new Mail();

		frontal = new ConsultaFrontal(config);

		generarZip = new GenerarZip(alfresco);
	}
}
