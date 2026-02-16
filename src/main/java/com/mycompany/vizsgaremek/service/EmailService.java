package com.mycompany.vizsgaremek.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailService {
    
    // Gmail SMTP beállítások
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "skillbookweb@gmail.com";
    private static final String PASSWORD = "chzpsbshqezoeuvx";

    /**
     * Kapcsolati űrlap email küldése
     */
    public boolean sendContactEmail(String senderName, String senderEmail, String subject, String messageText) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
                }
            });

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
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
                }
            });

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
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "SkillBook Biztonság"));
            
            // Ha email cím változott, MINDKÉT címre küldjünk
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
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
                }
            });

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
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
                }
            });

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

    // ========================================
    // HTML SABLONOK
    // ========================================

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
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
            }
        });

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