package pt.calves.email.client;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang.time.DateUtils;

public class Pop3MailClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String host = "pop.gmail.com";// change accordingly
		String mailStoreType = "pop3";
		String username = "*****";// change accordingly
		String password = "*****";// change accordingly

		new Pop3MailClient().check(host, mailStoreType, username, password);

	}

	private void check(String host, String mailStoreType, String username, String password) {
		try {

			// create properties field
			Properties properties = new Properties();

			properties.put("mail.pop3.host", host);
			properties.put("mail.pop3.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);

			// create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3s");

			Console console = System.console();
			if (console != null) {
		        console.printf("Please enter your username: [%s] ",username);
		        String inputUsername = console.readLine();
		        if (inputUsername.length()>0)
		        	username = inputUsername;
		        console.printf(username + "\n");
	
		        console.printf("Please enter your password: ");
		        char[] passwordChars = console.readPassword();
		        if (passwordChars.length>0)
		        	password = new String(passwordChars);
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				System.out.print(String.format("Please enter your username: [%s] ", username));
				String inputUsername = in.readLine();
		        if (inputUsername.length()>0)
		        	username = inputUsername;

		        System.out.print("Please enter your password: ");
				String inputPassword = in.readLine();
		        if (inputPassword.length()>0)
		        	password = inputPassword;
			}
			store.connect(host, username, password);

			// create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			SearchTerm sterm = new ReceivedDateTerm(ComparisonTerm.GE, DateUtils.addMonths(new Date(),-12));
			Message[] messages = //emailFolder.search(sterm);
					emailFolder.getMessages();
			System.out.println("messages.length---" + messages.length);

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				System.out.println("---------------------------------");
				System.out.println("Email Number " + message.getMessageNumber());
				System.out.println("Subject: " + message.getSubject());
				System.out.println("From: " + message.getFrom()[0]);
				System.out.println("Text: " + message.getContent().toString());

				System.out.println("---------------------------------");
				System.out.println(i +"/"+messages.length+ " [1] Next Message 	[2] Save Message [0] Quit");
				String input = in.readLine();
				
				if ("0".equals(input.trim()))
					break;
				if ("2".equals(input.trim()))
					saveMessage(message);
			}

			// close the store and folder objects
			emailFolder.close(false);
			store.close();

//		} catch (NoSuchProviderException e) {
//			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMessage(Message message) throws MessagingException, IOException {
		String filename = message.getSubject()+".msg";
		File outf = new File(filename);
		FileOutputStream fout = new FileOutputStream(outf);
		message.writeTo(fout);
		fout.flush();
		fout.close();
		System.out.println("Message writen to "+outf.getAbsolutePath());
	}

	private String readPassword() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Input password: ");
		return in.readLine();
	}

}
