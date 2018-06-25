import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

public class Mail {

	public void send(String subject, String text) {
		Email email = EmailBuilder.startingBlank()
			    .from("Monitor", "marcelohnqpl@gmail.com")
			    .to("Marcelo", "henrick939@gmail.com")
			    .withSubject(subject)
			    .withPlainText(text)
			    .buildEmail();

//		MailerBuilder
//		.withSMTPServer("smtp.host.com", 25, "marcelohnqpl@gmail.com", "Disquee8070102@hH")
//		  .buildMailer()
//		  .sendMail(email);
		
		Mailer mailer = MailerBuilder
		          .withSMTPServer("smtp.gmail.com", 465, "marcelohnqpl@gmail.com", "Disquee8070102@hH")
		          .withTransportStrategy(TransportStrategy.SMTPS)		          
		          .withSessionTimeout(10 * 1000)
		          .clearEmailAddressCriteria() // turns off email validation
		          .withDebugLogging(true)
		          .buildMailer();
		
		mailer.sendMail(email);
	}
}