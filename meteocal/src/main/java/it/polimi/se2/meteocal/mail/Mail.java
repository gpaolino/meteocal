/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import it.polimi.se2.meteocal.boundary.Base64;
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.User;
import javax.ejb.EJB;
/**
 *
 * @author alessandronegrini
 */
@ManagedBean(name="mail")
public class Mail {
   
    @EJB
    private UserManager um;
    String to="meteocalpoli@gmail.com";
    String from;
    String message;
    String smtpServ;
    String subject = "Information";
    String name;

    String messageToSend;
    String clientPass;
    
    private boolean passRec=false;
    private final long  timestamp = System.currentTimeMillis();

    public String getClientPass() {
        return clientPass;
    }

    public void setClientPass(String clientPass) {
        this.clientPass = clientPass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getSubject() {
        return this.subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSmtpServ() {
        return smtpServ;
    }

    public void setSmtpServ(String smtpServ) {
        this.smtpServ = smtpServ;
    }
    
    /**
     * It first prepare the email template and then it sends it by calling sendMail method
     */
    public void send(){
        messageToSend = EmailTemplate.getTemplate("Mail sent by "+this.name+": "+this.from+"<br/><br/>"+this.message);
        sendMail(messageToSend);
    }
    
    /**
     * It sends the email (after having formatted properly) in case of user forgots his password
     */
    public void sendPass(){
        String encoded=Base64.encode(String.valueOf((this.timestamp)).getBytes());
        String htmlCodePass="Hello! <br/>"
                + "Don't worry about forgetting your password, now all you have to do is resetting by clicking on this link: <br/><br/> http://localhost:8080/meteocal/faces/recPass.xhtml?email="+this.clientPass+"&?tok="+encoded+" <br/><br/> If you need help, please contact us in the apposite form that you can find in the home page. <br/><br/> Best Regards <br/>MeteoCal Team";
        messageToSend = EmailTemplate.getTemplate(htmlCodePass);
        User u = um.find(this.clientPass);
        if(u==null){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "This user is not registered", "")); 
        }
        else{
            this.to=this.clientPass;
            this.from="meteocalpoli@gmail.com";
            this.subject="Recover Password instruction";
            passRec=true;
            sendMail(messageToSend);
        }
    }
    
    /**
     * Set mail settings in order to send it correctly. 
     * It sets server address, port and the other important settings. 
     * It gets a feedback in case of mail sent correctly 
     * @param messageToSend Object that contains the email to be sent
     */
    public void sendMail(String messageToSend){
        
        System.out.print(this.from+" "+this.message+" ");
        
        this.smtpServ="smtp.gmail.com";
        try
        {
            Properties props = System.getProperties();
              // -- Attaching to default Session, or we could start a new one --
              props.put("mail.transport.protocol", "smtp" );
              props.put("mail.smtp.starttls.enable","true" );
              props.put("mail.smtp.host",smtpServ);
              props.put("mail.smtp.auth", "true" );
              props.put("mail.smtp.socketFactory.port", "465");
              props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
              props.put("mail.smtp.starttls.enable","true");
              Authenticator auth = new SMTPAuthenticator();
              Session session = Session.getInstance(props, auth);
              // -- Create a new message --
              Message msg = new MimeMessage(session);
              // -- Set the FROM and TO fields --
              msg.setFrom(new InternetAddress(from));
              msg.setSubject(subject);
              msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
              //msg.setText("Message Sent from "+from+"\n\n\n"+message);
              msg.setContent(messageToSend, "text/html; charset=utf-8");
              // -- Set some other header information --
              msg.setSentDate(new Date());
              // -- Send the message --
              Transport.send(msg);
              System.out.println("Message sent to"+to+" OK." );
              if(passRec)
                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Password instruction has been sent to your email address"));
              else{     
                if("Information".equals(this.subject))
                    FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Mail sent correctly"));
              }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          System.out.println("Exception "+ex);
        }
  }

// Also include an inner class that is used for authentication purposes
private class SMTPAuthenticator extends javax.mail.Authenticator {
        
        /**
         * @return authentication with the username and password specified
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            //user email
            String username =  "meteocalpoli@gmail.com";
            //user password
            String password = "politecnico";  
            return new PasswordAuthentication(username, password);
        }
  }
}
