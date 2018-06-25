import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

public class Mail {

	public void send(String subject, String text) {
		Email email = EmailBuilder.startingBlank()
			    .from("Monitor", "from-email@gmail.com")
			    .to("Marcelo", "to-email@gmail.com")
			    .withSubject(subject)
			    .withPlainText(text)
			    .buildEmail();
		
		Mailer mailer = MailerBuilder
		          .withSMTPServer("smtp.gmail.com", 465, "from-email@gmail.com", "password")
		          .withTransportStrategy(TransportStrategy.SMTPS)		          
		          .withSessionTimeout(10 * 1000)
		          .clearEmailAddressCriteria() // turns off email validation
		          .withDebugLogging(true)
		          .buildMailer();
		
		mailer.sendMail(email);
	}
}
