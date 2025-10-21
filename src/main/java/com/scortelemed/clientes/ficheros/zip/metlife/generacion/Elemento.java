package main.java.com.scortelemed.clientes.ficheros.zip.metlife.generacion;

import java.util.List;

import com.scortelemed.servicios.Documentacion;

public class Elemento {
  
  String literal;
  String extension = "ZIP";
  String nodo;
  List<Documentacion> documentos;
  
 
  public String getLiteral() {
    return literal;
  }
  public void setLiteral(String literal) {
    this.literal = literal;
  }
  public List<Documentacion> getDocumentos() {
    return documentos;
  }
  public void setDocumentos(List<Documentacion> documentos) {
    this.documentos = documentos;
  }
  public String getExtension() {
    return extension;
  }
  public void setExtension(String extension) {
    this.extension = extension;
  }
  public String getNodo() {
    return nodo;
  }
  public void setNodo(String nodo) {
    this.nodo = nodo;
  }
  
}
