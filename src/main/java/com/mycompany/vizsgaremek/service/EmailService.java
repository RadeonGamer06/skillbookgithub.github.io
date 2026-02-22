package com.mycompany.vizsgaremek.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailService {
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "skillbookweb@gmail.com";
    private static final String PASSWORD = "chzpsbshqezoeuvx";

    public boolean sendContactEmail(String senderName, String senderEmail, String subject, String messageText) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook Kapcsolat"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_FROM));
            message.setSubject("Új üzenet a kapcsolati űrlapról: " + subject);

            String htmlContent = buildContactEmailHTML(senderName, senderEmail, subject, messageText);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            
            System.out.println("✅ Kapcsolati email elküldve: " + senderEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Regisztrációs üdvözlő email küldése
     */
    public boolean sendWelcomeEmail(String userName, String userEmail) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Üdvözlünk a SkillBook-on!");

            String htmlContent = buildWelcomeEmailHTML(userName);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            
            System.out.println("✅ Üdvözlő email elküldve: " + userEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🆕 PROFIL MÓDOSÍTÁS ÉRTESÍTŐ EMAIL
     * Akkor küldődik, amikor a felhasználó megváltoztatja nevét, emailjét vagy jelszavát
     */
    public boolean sendProfileChangeEmail(String userName, String userEmail, 
                                          boolean nameChanged, String oldName, String newName,
                                          boolean emailChanged, String oldEmail, String newEmail,
                                          boolean passwordChanged) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook Biztonság"));
            
            if (emailChanged) {
                message.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(oldEmail + "," + newEmail));
            } else {
                message.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(userEmail));
            }
            
            message.setSubject("🔒 Fiókod adatai módosultak - SkillBook");

            String htmlContent = buildProfileChangeEmailHTML(
                userName, nameChanged, oldName, newName,
                emailChanged, oldEmail, newEmail, passwordChanged
            );
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            
            System.out.println("✅ Profil módosítási email elküldve: " + userEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🆕 EMAIL CÍM VÁLTOZÁS MEGERŐSÍTŐ EMAIL
     * Az ÚJ email címre küldjük megerősítés céljából
     */
    public boolean sendEmailChangeConfirmation(String userName, String newEmail, String oldEmail) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(newEmail));
            message.setSubject("✅ Email cím sikeresen megváltoztatva - SkillBook");

            String htmlContent = buildEmailChangeConfirmationHTML(userName, newEmail, oldEmail);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            
            System.out.println("✅ Email változás megerősítő elküldve: " + newEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🆕 JELSZÓ VÁLTOZÁS ÉRTESÍTŐ EMAIL
     */
    public boolean sendPasswordChangeEmail(String userName, String userEmail) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook Biztonság"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("🔐 Jelszavad megváltozott - SkillBook");

            String htmlContent = buildPasswordChangeEmailHTML(userName);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            
            System.out.println("✅ Jelszó változás email elküldve: " + userEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🆕 PROFILKÉP VÁLTOZÁS ÉRTESÍTŐ EMAIL
     */
    public boolean sendProfilePictureChangeEmail(String userName, String userEmail) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook Biztonság"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("🖼️ Profilképed megváltozott - SkillBook");

            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;background:#f4f4f4;margin:0;padding:20px}" +
                ".container{max-width:600px;margin:auto;background:#fff;border-radius:10px;overflow:hidden;box-shadow:0 4px 6px rgba(0,0,0,.1)}" +
                ".header{background:linear-gradient(135deg,#7c3aed,#059669);color:white;padding:30px;text-align:center}" +
                ".header h1{margin:0;font-size:26px}" +
                ".content{padding:30px;color:#333;line-height:1.6}" +
                ".change-item{margin:20px 0;padding:15px;background:#f8f9fa;border-radius:8px;display:flex;align-items:center;gap:15px}" +
                ".change-icon{font-size:36px}" +
                ".timestamp{background:#e5e7eb;padding:10px;border-radius:5px;text-align:center;font-size:14px;margin:20px 0}" +
                ".security-notice{background:#dbeafe;border-left:4px solid #3b82f6;padding:15px;margin:20px 0;border-radius:5px}" +
                ".footer{background:#f8f9fa;padding:20px;text-align:center;color:#666;font-size:14px}" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>🖼️ Profilképed módosult</h1><p style='margin:8px 0 0;opacity:.9'>SkillBook Biztonsági Értesítés</p></div>" +
                "<div class='content'>" +
                "<p>Kedves " + htmlEscape(userName) + "!</p>" +
                "<p>Értesítünk, hogy a SkillBook fiókod profilképe megváltozott.</p>" +
                "<div class='change-item'><span class='change-icon'>🖼️</span><div><strong>Profilkép frissítve</strong><br><span style='color:#16a34a'>Az új profilképed sikeresen feltöltésre került.</span></div></div>" +
                "<div class='timestamp'><strong>Módosítás időpontja:</strong> " + timestamp + "</div>" +
                "<div class='security-notice'><strong>⚠️ Nem te voltál?</strong><br>Ha te nem változtattad meg a profilképed, azonnal lépj kapcsolatba velünk: <a href='mailto:skillbookweb@gmail.com'>skillbookweb@gmail.com</a></div>" +
                "</div><div class='footer'><p>© 2025 SkillBook – A biztonságod a legfontosabb számunkra</p></div></div></body></html>";

            message.setContent(html, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("✅ Profilkép módosítási email elküldve: " + userEmail);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Profilkép email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🆕 FIÓK TÖRLÉS ÉRTESÍTŐ EMAIL
     */
    public boolean sendAccountDeletedEmail(String userName, String userEmail) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("👋 Fiókod törölve lett - SkillBook");

            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;background:#f4f4f4;margin:0;padding:20px}" +
                ".container{max-width:600px;margin:auto;background:#fff;border-radius:10px;overflow:hidden;box-shadow:0 4px 6px rgba(0,0,0,.1)}" +
                ".header{background:linear-gradient(135deg,#dc2626,#f59e0b);color:white;padding:30px;text-align:center}" +
                ".header h1{margin:0;font-size:26px}" +
                ".content{padding:30px;color:#333;line-height:1.6}" +
                ".info-box{background:#fef2f2;border-left:4px solid #dc2626;padding:18px;margin:20px 0;border-radius:6px}" +
                ".timestamp{background:#e5e7eb;padding:10px;border-radius:5px;text-align:center;font-size:14px;margin:20px 0}" +
                ".notice{background:#dbeafe;border-left:4px solid #3b82f6;padding:15px;margin:20px 0;border-radius:5px}" +
                ".footer{background:#f8f9fa;padding:20px;text-align:center;color:#666;font-size:14px}" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>👋 Fiókod törölve</h1><p style='margin:8px 0 0;opacity:.9'>SkillBook – Törlési Értesítés</p></div>" +
                "<div class='content'>" +
                "<p>Kedves " + htmlEscape(userName) + "!</p>" +
                "<p>Értesítünk, hogy a SkillBook felhasználói fiókod <strong>véglegesen törlésre került</strong>.</p>" +
                "<div class='info-box'>🗑️ <strong>Minden adatod, beiratkozásod és előzményed törölve lett.</strong><br>Ez a folyamat visszafordíthatatlan.</div>" +
                "<div class='timestamp'><strong>Törlés időpontja:</strong> " + timestamp + "</div>" +
                "<div class='notice'><strong>⚠️ Nem te törölted a fiókodat?</strong><br>Ha te nem kezdeményezted a törlést, azonnal vedd fel velünk a kapcsolatot: <a href='mailto:skillbookweb@gmail.com'>skillbookweb@gmail.com</a></div>" +
                "<p>Ha a jövőben ismét szeretnéd használni a SkillBook szolgáltatásait, bármikor regisztrálhatsz újra.</p>" +
                "<p>Köszönjük, hogy a SkillBook tagja voltál! 🙏</p>" +
                "</div><div class='footer'><p>© 2025 SkillBook. Minden jog fenntartva.</p></div></div></body></html>";

            message.setContent(html, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("✅ Fiók törlési email elküldve: " + userEmail);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Törlési email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Properties buildSmtpProps() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    private Session buildSession(Properties props) {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
            }
        });
    }


    /**
     * Kapcsolati email HTML sablon
     */
    private String buildContactEmailHTML(String senderName, String senderEmail, String subject, String messageText) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #60a5fa, #a78bfa); color: white; padding: 30px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".content { padding: 30px; color: #333; }" +
                ".info-row { margin: 15px 0; padding: 10px; background-color: #f8f9fa; border-left: 4px solid #60a5fa; }" +
                ".info-label { font-weight: bold; color: #555; }" +
                ".message-box { margin-top: 20px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; white-space: pre-wrap; }" +
                ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>📧 Új Kapcsolati Üzenet</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Új üzenet érkezett a SkillBook kapcsolati űrlapján keresztül:</p>" +
                "<div class='info-row'>" +
                "<span class='info-label'>Feladó neve:</span> " + htmlEscape(senderName) +
                "</div>" +
                "<div class='info-row'>" +
                "<span class='info-label'>Email cím:</span> <a href='mailto:" + htmlEscape(senderEmail) + "'>" + htmlEscape(senderEmail) + "</a>" +
                "</div>" +
                "<div class='info-row'>" +
                "<span class='info-label'>Tárgy:</span> " + htmlEscape(subject) +
                "</div>" +
                "<div class='message-box'>" +
                "<strong>Üzenet:</strong><br><br>" + htmlEscape(messageText) +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Ez egy automatikus email a SkillBook rendszerétől</p>" +
                "<p>© 2025 SkillBook. Minden jog fenntartva.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Üdvözlő email HTML sablon
     */
    private String buildWelcomeEmailHTML(String userName) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #60a5fa, #a78bfa); color: white; padding: 40px; text-align: center; }" +
                ".header h1 { margin: 0 0 10px 0; font-size: 32px; }" +
                ".header p { margin: 0; font-size: 18px; opacity: 0.9; }" +
                ".content { padding: 40px 30px; color: #333; line-height: 1.6; }" +
                ".welcome-text { font-size: 18px; margin-bottom: 20px; }" +
                ".features { margin: 30px 0; }" +
                ".feature { margin: 15px 0; padding-left: 30px; position: relative; }" +
                ".feature:before { content: '✓'; position: absolute; left: 0; color: #60a5fa; font-weight: bold; font-size: 20px; }" +
                ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🎉 Üdvözlünk a SkillBook-on!</h1>" +
                "<p>Örülünk, hogy csatlakoztál hozzánk</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p class='welcome-text'>Kedves " + htmlEscape(userName) + "!</p>" +
                "<p>Sikeres regisztrációd alkalmából szeretnénk üdvözölni a SkillBook közösségében! 🎓</p>" +
                "<div class='features'>" +
                "<p><strong>Most már elérhető számodra:</strong></p>" +
                "<div class='feature'>50+ professzionális tanfolyam böngészése</div>" +
                "<div class='feature'>Rugalmas időbeosztás választása</div>" +
                "<div class='feature'>Tapasztalt oktatók előadásai</div>" +
                "<div class='feature'>Hivatalos tanúsítványok megszerzése</div>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Köszönjük, hogy a SkillBook-ot választottad!</p>" +
                "<p>© 2025 SkillBook. Minden jog fenntartva.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 🆕 PROFIL MÓDOSÍTÁS EMAIL HTML SABLON
     */
    private String buildProfileChangeEmailHTML(String userName, 
                                               boolean nameChanged, String oldName, String newName,
                                               boolean emailChanged, String oldEmail, String newEmail,
                                               boolean passwordChanged) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder changes = new StringBuilder();
        
        if (nameChanged) {
            changes.append("<div class='change-item'>" +
                          "<span class='change-icon'>👤</span>" +
                          "<div class='change-details'>" +
                          "<strong>Név megváltoztatva</strong><br>" +
                          "<span class='old-value'>Régi: " + htmlEscape(oldName) + "</span><br>" +
                          "<span class='new-value'>Új: " + htmlEscape(newName) + "</span>" +
                          "</div></div>");
        }
        
        if (emailChanged) {
            changes.append("<div class='change-item'>" +
                          "<span class='change-icon'>📧</span>" +
                          "<div class='change-details'>" +
                          "<strong>Email cím megváltoztatva</strong><br>" +
                          "<span class='old-value'>Régi: " + htmlEscape(oldEmail) + "</span><br>" +
                          "<span class='new-value'>Új: " + htmlEscape(newEmail) + "</span>" +
                          "</div></div>");
        }
        
        if (passwordChanged) {
            changes.append("<div class='change-item'>" +
                          "<span class='change-icon'>🔐</span>" +
                          "<div class='change-details'>" +
                          "<strong>Jelszó megváltoztatva</strong><br>" +
                          "<span class='password-note'>Új jelszavad beállításra került</span>" +
                          "</div></div>");
        }
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #3b82f6, #8b5cf6); color: white; padding: 30px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".header p { margin: 10px 0 0 0; opacity: 0.9; }" +
                ".content { padding: 30px; color: #333; }" +
                ".alert-box { background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".change-item { margin: 20px 0; padding: 15px; background-color: #f8f9fa; border-radius: 8px; display: flex; align-items: start; }" +
                ".change-icon { font-size: 32px; margin-right: 15px; }" +
                ".change-details { flex: 1; }" +
                ".old-value { color: #dc2626; }" +
                ".new-value { color: #16a34a; font-weight: 600; }" +
                ".password-note { color: #6b7280; font-style: italic; }" +
                ".timestamp { background-color: #e5e7eb; padding: 10px; border-radius: 5px; text-align: center; font-size: 14px; margin: 20px 0; }" +
                ".security-notice { background-color: #dbeafe; border-left: 4px solid #3b82f6; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🔒 Fiókod adatai módosultak</h1>" +
                "<p>SkillBook Biztonsági Értesítés</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Kedves " + htmlEscape(userName) + "!</p>" +
                "<p>Értesítünk, hogy a következő módosítások történtek a fiókodban:</p>" +
                
                changes.toString() +
                
                "<div class='timestamp'>" +
                "<strong>Módosítás időpontja:</strong> " + timestamp +
                "</div>" +
                
                "<div class='security-notice'>" +
                "<strong>⚠️ Nem te voltál?</strong><br>" +
                "Ha te nem hajtottad végre ezeket a változtatásokat, azonnal lépj kapcsolatba velünk a " +
                "<a href='mailto:skillbookweb@gmail.com'>skillbookweb@gmail.com</a> címen!" +
                "</div>" +
                
                "<p style='margin-top: 30px; font-size: 14px; color: #666;'>" +
                "Ez egy automatikus biztonsági értesítés. Kérjük, ne válaszolj erre az emailre." +
                "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>© 2025 SkillBook - A biztonságod a legfontosabb számunkra</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 🆕 EMAIL VÁLTOZÁS MEGERŐSÍTŐ HTML SABLON
     */
    private String buildEmailChangeConfirmationHTML(String userName, String newEmail, String oldEmail) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #10b981, #3b82f6); color: white; padding: 40px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 32px; }" +
                ".content { padding: 40px 30px; color: #333; line-height: 1.6; }" +
                ".success-box { background-color: #d1fae5; border-left: 4px solid #10b981; padding: 20px; margin: 20px 0; border-radius: 8px; }" +
                ".email-info { background-color: #f8f9fa; padding: 15px; border-radius: 8px; margin: 15px 0; }" +
                ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>✅ Email cím megerősítve</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Kedves " + htmlEscape(userName) + "!</p>" +
                
                "<div class='success-box'>" +
                "<strong>✓ Sikeres email cím változtatás</strong><br>" +
                "Az email címed sikeresen megváltozott a SkillBook fiókodon." +
                "</div>" +
                
                "<div class='email-info'>" +
                "<strong>Régi email:</strong> " + htmlEscape(oldEmail) + "<br>" +
                "<strong>Új email:</strong> " + htmlEscape(newEmail) +
                "</div>" +
                
                "<p>Ezentúl ezzel az email címmel tudsz bejelentkezni a SkillBook-ra.</p>" +
                
                "<p style='margin-top: 30px; font-size: 14px; color: #666;'>" +
                "<strong>Fontos:</strong> A régi email címed (" + htmlEscape(oldEmail) + ") már nem használható bejelentkezéshez." +
                "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>© 2025 SkillBook. Minden jog fenntartva.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 🆕 JELSZÓ VÁLTOZÁS EMAIL HTML SABLON
     */
    private String buildPasswordChangeEmailHTML(String userName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #dc2626, #f59e0b); color: white; padding: 40px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".content { padding: 40px 30px; color: #333; line-height: 1.6; }" +
                ".alert-box { background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 20px; margin: 20px 0; border-radius: 8px; }" +
                ".success-box { background-color: #d1fae5; border-left: 4px solid #10b981; padding: 20px; margin: 20px 0; border-radius: 8px; }" +
                ".timestamp { background-color: #e5e7eb; padding: 10px; border-radius: 5px; text-align: center; font-size: 14px; margin: 20px 0; }" +
                ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🔐 Jelszó megváltoztatva</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Kedves " + htmlEscape(userName) + "!</p>" +
                
                "<div class='success-box'>" +
                "<strong>✓ Jelszavad sikeresen megváltoztatva</strong><br>" +
                "A SkillBook fiókodon új jelszó került beállításra." +
                "</div>" +
                
                "<div class='timestamp'>" +
                "<strong>Módosítás időpontja:</strong> " + timestamp +
                "</div>" +
                
                "<div class='alert-box'>" +
                "<strong>⚠️ Nem te voltál?</strong><br>" +
                "Ha te nem változtattad meg a jelszavadat, <strong>AZONNAL</strong> lépj kapcsolatba velünk:<br>" +
                "<a href='mailto:skillbookweb@gmail.com'>skillbookweb@gmail.com</a>" +
                "</div>" +
                
                "<p><strong>Biztonsági tippek:</strong></p>" +
                "<ul>" +
                "<li>Soha ne oszd meg a jelszavadat másokkal</li>" +
                "<li>Használj egyedi jelszót minden szolgáltatáshoz</li>" +
                "<li>Változtasd meg a jelszavad rendszeresen</li>" +
                "</ul>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>© 2025 SkillBook - A biztonságod a legfontosabb számunkra</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * HTML escape a biztonságos megjelenítéshez
     */
    private String htmlEscape(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("\n", "<br>");
    }
    
    public boolean sendForgotPasswordEmail(String userName, String userEmail, String tempPassword) {
    try {
        Properties props = buildSmtpProps();
        Session session = buildSession(props);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
        message.setSubject("SkillBook – Egyszer használatos jelszó");

        String html = buildForgotPasswordEmailHTML(userName, tempPassword);
        message.setContent(html, "text/html; charset=utf-8");

        Transport.send(message);
        System.out.println("Elfelejtett jelszó email elküldve: " + userEmail);
        return true;

    } catch (Exception e) {
        System.err.println("Elfelejtett jelszó email küldési hiba: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

/**
 * HTML sablon az egyszer használatos jelszó emailhez
 */
private String buildForgotPasswordEmailHTML(String userName, String tempPassword) {
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    return "<!DOCTYPE html>\n" +
           "<html lang=\"hu\">\n" +
           "<head>\n" +
           "  <meta charset=\"UTF-8\">\n" +
           "  <style>\n" +
           "    body { font-family: Arial, sans-serif; background:#f4f4f4; margin:0; padding:20px; }\n" +
           "    .container { max-width:580px; margin:auto; background:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 10px rgba(0,0,0,0.1); }\n" +
           "    .header { background:#4f46e5; color:white; padding:30px; text-align:center; }\n" +
           "    .content { padding:30px; color:#333; line-height:1.6; }\n" +
           "    .code-box { background:#f3f4f6; border:1px solid #d1d5db; padding:20px; margin:25px 0; text-align:center; font-size:24px; font-family:monospace; letter-spacing:4px; border-radius:6px; }\n" +
           "    .alert { background:#fef3c7; border-left:4px solid #d97706; padding:15px; margin:20px 0; border-radius:4px; }\n" +
           "    .footer { background:#f9fafb; padding:20px; text-align:center; color:#6b7280; font-size:13px; }\n" +
           "  </style>\n" +
           "</head>\n" +
           "<body>\n" +
           "  <div class=\"container\">\n" +
           "    <div class=\"header\">\n" +
           "      <h2>SkillBook – Jelszó visszaállítás</h2>\n" +
           "    </div>\n" +
           "    <div class=\"content\">\n" +
           "      <p>Kedves <strong>" + escapeHtml(userName) + "</strong>!</p>\n" +
           "      <p>Az alábbi egyszer használatos jelszóval tudsz most bejelentkezni:</p>\n" +
           "      <div class=\"code-box\">" + escapeHtml(tempPassword) + "</div>\n" +
           "      <div class=\"alert\">\n" +
           "        <strong>Fontos:</strong><br>\n" +
           "        • Ez a jelszó <strong>csak egyszer használható</strong><br>\n" +
           "        • Bejelentkezés után azonnal <strong>állíts be új jelszót</strong> a profilodban!<br>\n" +
           "        • Ha nem te kérted ezt, azonnal jelezd nekünk!\n" +
           "      </div>\n" +
           "      <p><small>Kérés időpontja: " + now + "</small></p>\n" +
           "    </div>\n" +
           "    <div class=\"footer\">\n" +
           "      © 2026 SkillBook – Minden jog fenntartva\n" +
           "    </div>\n" +
           "  </div>\n" +
           "</body>\n" +
           "</html>";
}

    /**
     * Számla / vásárlás visszaigazoló email küldése.
     */
    public boolean sendInvoiceEmail(String userName, String userEmail,
                                    String courseName,
                                    long coursePrice, long vatAmount, long totalAmount,
                                    String transactionId,
                                    String courseStart, String courseEnd,
                                    String instructorName) {
        try {
            Properties props = buildSmtpProps();
            Session session = buildSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook Számlázás"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("\uD83E\uDDFE Számla és vásárlás visszaigazolása – SkillBook");

            String html = buildInvoiceEmailHTML(userName, courseName,
                    coursePrice, vatAmount, totalAmount,
                    transactionId, courseStart, courseEnd, instructorName);
            message.setContent(html, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Számla email elküldve: " + userEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Számla email küldési hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String buildInvoiceEmailHTML(String userName, String courseName,
                                          long coursePrice, long vatAmount, long totalAmount,
                                          String transactionId,
                                          String courseStart, String courseEnd,
                                          String instructorName) {

        String purchaseDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm"));

        String invoiceNumber = "SB-INV-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-"
                + transactionId.replace("SB-", "");

        String dateRow = "";
        if (courseStart != null && !courseStart.isEmpty()) {
            String endPart = (courseEnd != null && !courseEnd.isEmpty()) ? " – " + htmlEscape(courseEnd) : "";
            dateRow = "<tr><td style=\'padding:10px 16px;color:#555;border-bottom:1px solid #f0f0f0\'>Időpont</td>" +
                      "<td style=\'padding:10px 16px;text-align:right;font-weight:600;border-bottom:1px solid #f0f0f0\'>" +
                      htmlEscape(courseStart) + endPart + "</td></tr>";
        }

        String instructorRow = "";
        if (instructorName != null && !instructorName.isEmpty()) {
            instructorRow = "<tr><td style=\'padding:10px 16px;color:#555;border-bottom:1px solid #f0f0f0\'>Oktató</td>" +
                            "<td style=\'padding:10px 16px;text-align:right;font-weight:600;border-bottom:1px solid #f0f0f0\'>" +
                            htmlEscape(instructorName) + "</td></tr>";
        }

        String netto  = String.format("%,d", coursePrice).replace(",", "\u00a0");
        String vat    = String.format("%,d", vatAmount).replace(",", "\u00a0");
        String brutto = String.format("%,d", totalAmount).replace(",", "\u00a0");

        return "<!DOCTYPE html><html lang=\'hu\'><head><meta charset=\'UTF-8\'>" +
            "<style>" +
            "body{margin:0;padding:0;background:#f0f4ff;font-family:Arial,Helvetica,sans-serif}" +
            ".wrap{max-width:620px;margin:30px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.10)}" +
            ".top-bar{height:6px;background:linear-gradient(90deg,#7c3aed,#059669)}" +
            ".header{background:linear-gradient(135deg,#7c3aed 0%,#4338ca 100%);color:white;padding:36px 32px;text-align:center}" +
            ".header h1{margin:0 0 6px;font-size:26px}" +
            ".header p{margin:0;opacity:.88;font-size:14px}" +
            ".badge{display:inline-block;background:rgba(255,255,255,0.2);border:1px solid rgba(255,255,255,0.35);border-radius:20px;padding:4px 16px;font-size:13px;margin-top:14px}" +
            ".body{padding:32px}" +
            ".success-box{background:#f0fdf4;border:1px solid #86efac;border-radius:10px;padding:18px 22px;display:flex;align-items:center;gap:14px;margin-bottom:28px}" +
            ".success-icon{font-size:32px;flex-shrink:0}" +
            ".success-text h3{margin:0 0 4px;color:#15803d;font-size:16px}" +
            ".success-text p{margin:0;color:#166534;font-size:13px}" +
            ".section-title{font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.1em;color:#7c3aed;margin:0 0 12px}" +
            ".course-box{background:#faf5ff;border:1px solid #e9d5ff;border-radius:10px;overflow:hidden;margin-bottom:24px}" +
            ".course-name{background:linear-gradient(135deg,#7c3aed,#4338ca);color:white;padding:14px 18px;font-size:15px;font-weight:700}" +
            ".inv-table{width:100%;border-collapse:collapse}" +
            ".inv-table td{padding:11px 16px;font-size:14px;color:#333;border-bottom:1px solid #f0f0f0}" +
            ".inv-table .total td{background:#ede9fe;font-weight:700;font-size:15px;border-top:2px solid #c4b5fd;border-bottom:none}" +
            ".meta-row{display:flex;justify-content:space-between;font-size:12px;color:#888;margin-bottom:28px;gap:8px}" +
            ".meta-item{background:#f8f9fa;border-radius:6px;padding:8px 14px;flex:1;text-align:center}" +
            ".meta-item strong{display:block;color:#333;font-size:13px;margin-top:3px}" +
            ".info-box{background:#fffbeb;border:1px solid #fde68a;border-radius:8px;padding:16px 18px;font-size:13px;color:#78350f;margin-bottom:24px}" +
            ".footer{background:#f8f9fa;padding:22px 32px;text-align:center;color:#888;font-size:12px;border-top:1px solid #f0f0f0}" +
            ".footer a{color:#7c3aed;text-decoration:none}" +
            "</style></head>" +
            "<body><div class=\'wrap\'>" +
            "<div class=\'top-bar\'></div>" +
            "<div class=\'header\'><h1>&#129518; Vásárlás visszaigazolása</h1>" +
            "<p>Köszönjük, hogy a SkillBook-ot választottad!</p>" +
            "<div class=\'badge\'>&#10003; Fizetés sikeres</div></div>" +
            "<div class=\'body\'>" +
            "<p style=\'font-size:16px;color:#333;margin:0 0 24px\'>Kedves <strong>" + htmlEscape(userName) + "</strong>!</p>" +
            "<div class=\'success-box\'><div class=\'success-icon\'>&#127881;</div>" +
            "<div class=\'success-text\'><h3>Sikeres vásárlás!</h3>" +
            "<p>Tanfolyamra való beiratkozásod megerősítve és elmentve.</p></div></div>" +
            "<div class=\'meta-row\'>" +
            "<div class=\'meta-item\'>Vásárlás dátuma<strong>" + purchaseDate + "</strong></div>" +
            "<div class=\'meta-item\'>Tranzakció ID<strong>" + htmlEscape(transactionId) + "</strong></div>" +
            "<div class=\'meta-item\'>Számla száma<strong>" + htmlEscape(invoiceNumber) + "</strong></div>" +
            "</div>" +
            "<p class=\'section-title\'>Tanfolyam részletei</p>" +
            "<div class=\'course-box\'><div class=\'course-name\'>&#128218; " + htmlEscape(courseName) + "</div>" +
            "<table style=\'width:100%;border-collapse:collapse\'>" +
            dateRow + instructorRow +
            "<tr><td style=\'padding:10px 16px;color:#555\'>Hozzáférés</td>" +
            "<td style=\'padding:10px 16px;text-align:right;font-weight:600\'>Azonnal elérhető</td></tr>" +
            "</table></div>" +
            "<p class=\'section-title\'>Számla összesítő</p>" +
            "<div style=\'background:#f8faff;border:1px solid #e0e7ff;border-radius:10px;overflow:hidden;margin-bottom:24px\'>" +
            "<table class=\'inv-table\'>" +
            "<tr><td>Nettó ár</td><td style=\'text-align:right;font-weight:600\'>" + netto + " Ft</td></tr>" +
            "<tr><td>ÁFA (27%)</td><td style=\'text-align:right;font-weight:600\'>" + vat + " Ft</td></tr>" +
            "<tr class=\'total\'><td>Fizetett összeg</td><td style=\'text-align:right;color:#7c3aed\'>" + brutto + " Ft</td></tr>" +
            "</table></div>" +
            "<div class=\'info-box\'>&#9888;&#65039; <strong>Fontos:</strong> Ez az email egyben a vásárlásod igazolása. " +
            "Kérjük, őrizd meg. Kérdés esetén: <a href=\'mailto:skillbookweb@gmail.com\'>" +
            "skillbookweb@gmail.com</a></div>" +
            "</div>" +
            "<div class=\'footer\'><p>&#169; 2025 SkillBook – Minden jog fenntartva</p>" +
            "<p><a href=\'mailto:skillbookweb@gmail.com\'>skillbookweb@gmail.com</a></p></div>" +
            "</div></body></html>";
    }

/** Egyszerű HTML escape – biztonsági okokból */
private String escapeHtml(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
}
}