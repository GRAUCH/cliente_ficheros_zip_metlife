package main.java.com.scortelemed.clientes.ficheros.zip.metlife.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class Mail {

  public void enviarMail(String to, String cc, String asunto, String mensaje, String ipMailAddress, String from, String formato, Logger log) {

    List<String> destinatarios = new ArrayList<String>();
    List<String> copia = new ArrayList<String>();
    StringTokenizer st;
    String pass = "les+y.#7&*\"/MA";

    Properties props = new Properties();

    props.put("mail.debug", "false");
    props.put("mail.smtp.auth", true);
    props.put("mail.smtp.port", "25");
    props.put("mail.smtp.host", ipMailAddress);
    props.put("mail.smtp.socketFactory.fallback", "false");

    Session session = Session.getInstance(props);

    try {

      st = new StringTokenizer(to, ",");
      while (st.hasMoreElements()) {
        destinatarios.add(st.nextElement().toString());
      }

      if (cc != null) {
        st = new StringTokenizer(cc, ",");
        while (st.hasMoreElements()) {
          copia.add(st.nextElement().toString());
        }
      }
      
      Message msg = new MimeMessage(session);

      InternetAddress addressFrom = new InternetAddress(from);

      msg.setFrom(addressFrom);

      Transport t = session.getTransport("smtp");

      t.connect("customer", pass);

      for (int i = 0; i < destinatarios.size(); i++) {
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarios.get(i), false));

        if (copia != null && copia.size() > 0) {
          for (int j = 0; j < copia.size(); j++) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copia.get(j), false));
          }
        }

        msg.setSubject(asunto);
        msg.setContent(mensaje, formato + "; charset=UTF-8");
        t.sendMessage(msg, msg.getAllRecipients());
      }

      t.close();

    } catch (MessagingException e) {
      log.info("ERROR: Excepcion en el metodo enviarMail de la clase Mail.java." + e.getMessage());
    }
  }
}
