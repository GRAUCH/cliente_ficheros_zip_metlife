package main.java.com.scortelemed.clientes.ficheros.zip.metlife.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import main.java.com.scortelemed.clientes.ficheros.zip.metlife.generacion.Elemento;
import org.apache.log4j.Logger;


import com.scortelemed.servicios.Expediente;

public class Util {

  public static boolean fechaEsDomingo(String fechaInicio, Logger log) {

    String anno = fechaInicio.substring(0, 4);
    String mes = fechaInicio.substring(4, 6);
    String dia = fechaInicio.substring(6, 8);

    Date convertedDate = null;
    SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
    Calendar fecha = Calendar.getInstance();

    try {

      convertedDate = df1.parse(anno + "/" + mes + "/" + dia);
      fecha.setTime(convertedDate);

    } catch (ParseException e) {
      log.info("ERROR: Error en el metodo fechaEsDomingo de la clase FechaUtil.java." + e.getMessage());
    }

    if (fecha.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      return true;
    }
    return false;
  }
  
  public static String calcularFechaViernes(String fechaInicio, Logger log) {

    String anno = fechaInicio.substring(0, 4);
    String mes = fechaInicio.substring(4, 6);
    String dia = fechaInicio.substring(6, 8);

    Date convertedDate = null;
    SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
    Calendar fecha = Calendar.getInstance();

    try {

      convertedDate = df1.parse(anno + "/" + mes + "/" + dia);

      fecha.setTime(convertedDate);
      fecha.add(Calendar.DATE, -2);

    } catch (ParseException e) {
      log.info("ERROR: Error en el metodo calcularFechaViernes de la clase FechaUtil.java." + e.getMessage());
    }

    return df2.format(fecha.getTime());
  }

  public static String obtenerTt(Expediente listaExpedientes) {

    if (listaExpedientes.getServicios() != null) {

      for (int i = 0; i < listaExpedientes.getServicios().length; i++) {

        if (listaExpedientes.getServicios(i).getCodigoStFacturacion().equals("000761")) {
          return "1";
        }

        if (listaExpedientes.getServicios(i).getCodigoStFacturacion().equals("000763")) {
          return "2";
        }

        if (listaExpedientes.getServicios(i).getCodigoStFacturacion().equals("000762")) {
          return "3";
        }
      }
    }

    return null;

  }

  public static boolean esTrygp(String producto, String codigoProducto) {

    if (producto != null && !producto.isEmpty()) {

      return producto.equals(codigoProducto);

    } else {

      return false;

    }

  }

  public static boolean esSf90(String producto, String codigoProducto) {

    if (producto != null && !producto.isEmpty()) {

      return producto.equals(codigoProducto);

    } else {

      return false;

    }

  }

  public static boolean esForet(String producto, String codigoProducto) {

    if (producto != null && !producto.isEmpty()) {

      return producto.equals(codigoProducto);

    } else {

      return false;

    }
  }
  
  public static String generarMensajes(List<Elemento> entrada, String entidad) {

    StringBuffer message = new StringBuffer();
    message.append(entidad + " error information: \r\n");
    message.append("\r\n");
    message.append("\r\n");

    for (int i = 0; i < entrada.size(); i++) {
      message.append(entrada.get(i).getLiteral() + " not created \r\n");
      message.append("\r\n");
    }

    return message.toString();
  }

}
