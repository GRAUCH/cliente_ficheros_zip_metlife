package main.java.com.scortelemed.clientes.ficheros.zip.metlife.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.scortelemed.servicios.Documentacion;
import com.scortelemed.servicios.Expediente;
import com.scortelemed.servicios.TipoDocumentacion;
import com.scortelemed.servicios.TipoValidacionDocumento;

public class UtilAlfresco {

	private ParentReference companyHomeParent;
	private UpdateResult[] result;
	private UpdateResult[] resultsEspacionNuevo;
	private Logger log;
	private Properties propiedades;

	public UtilAlfresco(Properties propiedades, Logger log) {
		super();
		this.propiedades = propiedades;
		this.log = log;
	}

	/**
	 * INICIO SESSION ALFRESCO
	 * 
	 */
	private void startSession() {

		WebServiceFactory.setEndpointAddress((String) propiedades.getProperty("endPointAlfresco"));
		try {
			AuthenticationUtils.startSession((String) propiedades.getProperty("usuarioAlfresco"), (String) propiedades.getProperty("passAlfresco"));
		} catch (AuthenticationFault e) {
			log.info("ERROR: Excepcion en startSession de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * END SESSION ALFRESCO
	 * 
	 */
	private void endSession() {
		AuthenticationUtils.endSession();
	}

	/**
	 * SE CREA REFERENCIA AL NODO PADRE
	 * 
	 */
	public void referenciarNodoPadre() {

		Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
		Reference content = resultsEspacionNuevo[0].getDestination();
		companyHomeParent = new ParentReference(storeRef, content.getUuid(), null, Constants.ASSOC_CONTAINS, null);

	}

	/**
	 * SE CREA REFERENCIA AL NODO PADRE PASADO POR PARAMETROS
	 * 
	 */
	public void referenciarNodoPadre(String node) {

		Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
		companyHomeParent = new ParentReference(storeRef, node, null, Constants.ASSOC_CONTAINS, null);
	}

	/**
	 * SE CREA LA REFERENCIA AL FICHERO QUE SE QUIERE SUBIR
	 * 
	 * @param name
	 */
	public void crearReferenciaFichero(String name) {

		companyHomeParent.setChildName("cm:" + name);

		// Comienza la construcci�n de nodo

		NamedValue[] contentProps = new NamedValue[1];
		contentProps[0] = Utils.createNamedValue(Constants.PROP_NAME, name);
		CMLCreate create = new CMLCreate("1", companyHomeParent, null, null, null, Constants.TYPE_CONTENT, contentProps);

		// A�adimos aspectos al nodo

		NamedValue[] titledProps = new NamedValue[2];
		titledProps[0] = Utils.createNamedValue(Constants.PROP_TITLE, name);
		titledProps[1] = Utils.createNamedValue(Constants.PROP_DESCRIPTION, name);
		CMLAddAspect addAspect = new CMLAddAspect(Constants.ASPECT_TITLED, titledProps, null, "1");

		// Contruimos CML Block, con el nodo y sus aspectos

		CML cml = new CML();
		cml.setCreate(new CMLCreate[] { create });
		cml.setAddAspect(new CMLAddAspect[] { addAspect });

		// Creamos y recuperamos el contenido v�a Repository Web Service

		try {
			result = WebServiceFactory.getRepositoryService().update(cml);
		} catch (RepositoryFault e) {
			log.info("ERROR: Excepcion en crearReferenciaFichero de la clase UtilAlfresco.class " + e.getMessage());
		} catch (RemoteException e) {
			log.info("ERROR: Excepcion en crearReferenciaFichero de la clase UtilAlfresco.class " + e.getMessage());
		}

	}

	/**
	 * SUBIR FICHERO
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void subirFichero(String path, String tipo) throws IOException {

		Reference content = result[0].getDestination();

		FileInputStream is = new FileInputStream(path);
		byte[] bytes = null;
		try {
			bytes = ContentUtils.convertToByteArray(is);
		} catch (Exception e) {
			log.info("ERROR: Excepcion en subirFichero de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		}

		ContentFormat format = new ContentFormat(obtenerTipoFichero(tipo), "UTF-8");

		ContentServiceSoapBindingStub contentService = WebServiceFactory.getContentService();
		contentService.write(content, Constants.PROP_CONTENT, bytes, format);

	}

	/**
	 * OBTIENE TIPO DE FICHERO
	 * 
	 * @param ext
	 * @return
	 */
	public String obtenerTipoFichero(String ext) {

		String contType = null;

		if (ext.equals("txt")) {
			contType = "text/plain";
		} else if (ext.equals("xls")) {
			contType = "application/vnd.ms-excel";
		} else if (ext.equals("doc")) {
			contType = "application/msword";
		} else if (ext.equals("html") || ext.equals("htm")) {
			contType = "text/html";
		} else if (ext.equals("jpg") || ext.equals("jpeg")) {
			contType = "image/jpeg";
		} else if (ext.equals("bmp")) {
			contType = "image/bmp";
		} else if (ext.equals("pdf")) {
			contType = "application/pdf";
		} else if (ext.equals("ppt")) {
			contType = "application/vnd.ms-powerpoint";
		} else if (ext.equals("xml")) {
			contType = "text/xml";
		} else if (ext.equals("zip")) {
			contType = "application/vnd.ms-zip";
		}

		return contType;
	}

	/**
	 * CREA ESPACIO EN NODO PADRE
	 * 
	 * @param nodoPadre
	 * @param nombre
	 */
	public void crearEspacio(String nodoPadre, String nombre) {

		Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

		try {
			ParentReference parentReference = new ParentReference(storeRef, nodoPadre, null, Constants.ASSOC_CONTAINS, Constants.createQNameString(
					Constants.NAMESPACE_CONTENT_MODEL, nombre));

			NamedValue[] properties = new NamedValue[] { Utils.createNamedValue(Constants.PROP_NAME, nombre) };
			CMLCreate create = new CMLCreate("1", parentReference, null, null, null, Constants.TYPE_FOLDER, properties);
			CML cml = new CML();
			cml.setCreate(new CMLCreate[] { create });
			resultsEspacionNuevo = WebServiceFactory.getRepositoryService().update(cml);
		} catch (Exception e) {
			log.info("ERROR: Excepcion en crearEspacio de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * OBTIENE EL NODOREF DEL PATH
	 * 
	 * @param pathNode
	 * @return
	 */
	public String obtenerNodeRefNodoDocumental(String pathNode) {

		String path = null;

		Store store = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

		RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService((String) propiedades.getProperty("endPointAlfresco"));

		path = obtenerPathNodoDocumental(pathNode).replaceFirst("/", "\"");

		/**
		 * A�ADIMOS EL NODO DOCUMENTACION PORQUE ES DE DONDE CUELGAN TODA LA
		 * DOCUMENTACI�N DE CADA EXPEDIENTE EN ALFRESCO
		 * 
		 */
		Query query = new Query(Constants.QUERY_LANG_LUCENE, "PATH:" + path + "/cm:documentacion\"");

		QueryResult queryResult;

		try {

			queryResult = repositoryService.query(store, query, false);

			ResultSet resultSet = queryResult.getResultSet();
			ResultSetRow[] rows = resultSet.getRows();

			if (rows != null && rows.length > 0) {

				return rows[0].getNode().getId();

			}

		} catch (RepositoryFault e) {
			log.info("ERROR: Excepcion en obtenerNodeRefNodoDocumental de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		} catch (RemoteException e) {
			log.info("ERROR: Excepcion en obtenerNodeRefNodoDocumental de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * OBTIENE EL PATH DEL NODO
	 * 
	 * @param id
	 * @return
	 */
	private String obtenerPathNodoDocumental(String id) {

		Store store = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

		RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService((String) propiedades.getProperty("endPointAlfresco"));

		Query query = new Query(Constants.QUERY_LANG_LUCENE, "@cm\\:name:'" + id + "'");
		QueryResult queryResult;

		try {

			queryResult = repositoryService.query(store, query, false);

			ResultSet resultSet = queryResult.getResultSet();
			ResultSetRow[] rows = resultSet.getRows();

			if (rows != null) {

				String firstResultId = rows[0].getNode().getId();
				Reference reference = new Reference(store, firstResultId, null);
				Node[] a;

				a = repositoryService.get(new Predicate(new Reference[] { reference }, store, null));

				if (a != null && a.length > 0) {
					return a[0].getReference().getPath();
				}
			}

		} catch (RepositoryFault e) {
			log.info("ERROR: Excepcion en obtenerPathNodoDocumental de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		} catch (RemoteException e) {
			log.info("ERROR: Excepcion en obtenerPathNodoDocumental de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * M�TODO QUE CREA EL DOCUMENTO EN EL NODO DEL EXPEDIENTE EN ALFRESCO Y
	 * DEVUELE EL DOCUMENTO
	 * 
	 * @param expediente
	 * @param rutaDocumento
	 * @return
	 */
	public Documentacion generarDocumentoAlfresco(Expediente expediente, String rutaDocumento, Logger log, String nombreDocumento) {

		String nodeRef = null;

		startSession();
		nodeRef = obtenerNodeRefNodoDocumental(expediente.getCodigoST());
		referenciarNodoPadre(nodeRef);
		crearReferenciaFichero(nombreDocumento);

		try {

			subirFichero(rutaDocumento, nombreDocumento.substring(nombreDocumento.lastIndexOf(".") + 1, nombreDocumento.length()));

		} catch (IOException e) {
			log.info("ERROR: Excepcion en generarDocumentoAlfresco de la clase UtilAlfresco.class " + e.getMessage());
			e.printStackTrace();
		}

		endSession();

		return crearDocumentacion(expediente, nombreDocumento, nodeRef, propiedades);

	}

	/**
	 * CREA DOCUMETACION
	 * 
	 * @param expediente
	 * @param nombreDocumento
	 * @param nodeRef
	 * @param propiedades
	 * @return
	 */
	public static Documentacion crearDocumentacion(Expediente expediente, String nombreDocumento, String nodeRef, Properties propiedades) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		Documentacion documentacion = new Documentacion();

		documentacion.setCodigoST(null);
		documentacion.setDocumentacionId(null);
		documentacion.setExpedienteId(expediente.getCodigoST());
		documentacion.setFechaHoraRecepcion(calendar);
		documentacion.setMetadataAudio(null);
		documentacion.setNodoAlfresco(propiedades.getProperty("nodoAlfresco") + nodeRef);
		documentacion.setNombre(nombreDocumento.substring(0,
				nombreDocumento.replace(nombreDocumento.substring(nombreDocumento.lastIndexOf("."), nombreDocumento.length()), "").length()));
		documentacion.setTipoDocumentacion(TipoDocumentacion.CARTA);
		documentacion.setTipoValidacionDocumento(TipoValidacionDocumento.SIN_INCIDENCIAS);
		documentacion.setUrlAlfresco(propiedades.getProperty("urlAlfresco") + nodeRef + "/" + nombreDocumento);

		return documentacion;

	}

	protected InputStream getContent(Reference node) throws Exception {
		Content content = null;
		Store store = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
		// System.out.println("Getting content of document with path " +
		// node.getPath() + " or id " + node.getUuid() + "." );
		log.info("Getting content of document with path " + node.getPath() + " or id " + node.getUuid() + ".");
		try {
			Content[] read = WebServiceFactory.getContentService().read(new Predicate(new Reference[] { node }, store, null), Constants.PROP_CONTENT);
			content = read[0];
			log.info("Got " + read.length + " content elements.");
			log.info("The first content element has a size of " + content.getLength() + " segments.");
			// System.out.println("Got " + read.length + " content elements.");
			// System.out.println("The first content element has a size of "+
			// content.getLength() + " segments.");
		} catch (Exception e) {
			log.info("Can not get the content.");
			// System.err.println("Can not get the content.");
			throw e;
		}
		InputStream in = ContentUtils.getContentAsInputStream(content);
		return in;
	}

	/**
	 * DESCARGAR DOCUMENTO DE ALFRESCO
	 * 
	 * @param path
	 *            url de descarga de alfresco
	 * @param usuario
	 *            usuario de alfresco
	 * @param password
	 *            password de alfresco
	 * @param rutaDescarga
	 *            ruta de descarga del fichero de Alfresco
	 * @param nombreFichero
	 *            nombre que le damos a la descarga de Alfresco
	 * @return void
	 */
	public String descargar(String path, String usuario, String password, String rutaDescarga, String nombreFichero, String rutaSalida) throws Exception {
		
		//url = url.replace("https://SCOR0-BPM01:8081","http://172.26.0.2:8080");
		//url = url.replace("SCOR0-BPM01","172.26.0.2");
	 
	  File ficheroLocal = null;
	  
		try {
		  URL url = new URL(path);
	    ficheroLocal = new File(rutaSalida+"\\"+path.substring(path.lastIndexOf("/"), path.length()));
			FileUtils.copyURLToFile(url, ficheroLocal);
			log.info("INFO: File downloaded correctly." + path);
		} catch (Exception e) {
			log.info("ERROR: Excepci�n en descargar de la clase UtilAlfresco." + e.getMessage().toString());
		}
		
		return ficheroLocal.getPath();

	}

	public ParentReference getCompanyHomeParent() {
		return companyHomeParent;
	}

	public void setCompanyHomeParent(ParentReference companyHomeParent) {
		this.companyHomeParent = companyHomeParent;
	}

	public UpdateResult[] getResult() {
		return result;
	}

	public void setResult(UpdateResult[] result) {
		this.result = result;
	}

	public UpdateResult[] getResultsEspacionNuevo() {
		return resultsEspacionNuevo;
	}

	public void setResultsEspacionNuevo(UpdateResult[] resultsEspacionNuevo) {
		this.resultsEspacionNuevo = resultsEspacionNuevo;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public Properties getPropiedades() {
		return propiedades;
	}

	public void setPropiedades(Properties propiedades) {
		this.propiedades = propiedades;
	}
}
