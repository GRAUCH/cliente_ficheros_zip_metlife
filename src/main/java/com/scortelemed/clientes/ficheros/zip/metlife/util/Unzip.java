package main.java.com.scortelemed.clientes.ficheros.zip.metlife.util;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class Unzip {
	
	   public int unzipFile(String filePath, Logger log){
	         
		    int unzipFileError = 0;
	        FileInputStream fis = null;
	        ZipInputStream zipIs = null;
	        ZipEntry zEntry = null;
	        boolean tieneDocumentos = false;
	        
	        try {
	            fis = new FileInputStream(filePath);
	            zipIs = new ZipInputStream(new BufferedInputStream(fis));
	            log.info("Content of "+filePath);
	            while((zEntry = zipIs.getNextEntry()) != null) {
	            	log.info("Document: "+zEntry.getName());
	            	tieneDocumentos = true;
	            }
	            
	            if (!tieneDocumentos){
	            	/**SI NO TIENE DOCUMENTOS DEVOLVEMOS UN CODIGO 3 PARA QUE SE VUELVA A INTENTAR
	            	 *  
	            	 */
	            	unzipFileError = 3;
	            }
	            
	        } catch (FileNotFoundException e) {
	        	  unzipFileError = 2;
	            log.info("FileNotFoundException in class " + getClass().getName() + ". " + e.getMessage());
	        } catch (EOFException e) {
	        	  unzipFileError = 1;
	        	log.info("EOFException in class " + getClass().getName() + ". " + e.getMessage());
	        } catch (IOException e) {
	        	  unzipFileError = 1;
	            log.info("IOException in class " + getClass().getName() + ". " + e.getMessage());
	        } catch (Exception e) {
	            unzipFileError = 3;
	            log.info("Exception in class " + getClass().getName() + ". " + e.getMessage());
          }
	        
	        return unzipFileError;
	    }
}
