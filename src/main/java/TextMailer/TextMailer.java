package TextMailer;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Scanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TextMailer {
    private static final String[][] carrierMMS = {
            {"Alltel", "", "mms.alltelwireless.com"},
            {"AT&T", "", "mms.att.net"},
            {"Boost Mobile", "", "myboostmobile.com"},
            {"Cricket Wireless", "", "mms.cricketwireless.net"},
            {"MetroPCS", "", "mymetropcs.com"},
            {"Google Fi", "", ""},
            {"Republic Wireless", "", ""},
            {"Sprint", "", "pm.sprint.com"},
            {"T-Mobile", "", "tmomail.net"},
            {"U.S. Cellular", "", "mms.uscc.net"},
            {"Verizon Wireless", "", "vzwpix.com"},
            {"Virgin Mobile", "", "vmpix.com"}
    };
    private String host;
    private String smtp;
    private String appPass;
    private Email email = EmailBuilder.startingBlank().buildEmail();
    private Mailer mailer;
    private String[] recipients = new String[0];
    private String bodyMessage = "";

    // Getter for email
    public Email getEmail() {
        return email;
    }
    // Setters for email
    public void addRecipient(String recPhoneNum, String carrier) {
        String rec = "";
        for (String[] carrierMM : carrierMMS) {
            if (carrier.equalsIgnoreCase(carrierMM[0])) {
                rec = recPhoneNum + "@" + carrierMM[2];
            }
        }
        String[] temp = new String[getRecipients().length + 1];
        for (int i = 0; i < getRecipients().length; i++) {
            temp[i] = getRecipients()[i];
        }
        temp[temp.length - 1] = rec;
        this.recipients = temp;
    }
    public void addText(String text) {
        this.bodyMessage += text;
    }

    public String getBodyMessage() {
        return this.bodyMessage;
    }

    // Setter for mailer
    public void setMailer() {
        this.mailer = MailerBuilder
                .withSMTPServer(this.smtp, 587, this.host, this.appPass)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer();
    }



    // Getter for recipients
    public String[] getRecipients() {
        return this.recipients;
    }

    // Setter for recipients
    private ScheduledExecutorService scheduleSend(int hours, int minutes) {
        long initialDelay = calculateInitialDelay(hours, minutes, 0);
        Runnable task = this::send;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(task, initialDelay, TimeUnit.MILLISECONDS);
        return scheduler;
    }

    public long calculateInitialDelay(int targetHour, int targetMin, int targetSec) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).toMillis();
    }

    private void send() {
        for (String recipient : getRecipients()) {
            System.out.println("\nSending message to " + recipient + "...");
            this.email = EmailBuilder.copying(this.email)
                    .from("jwd0526@gmail.com")
                    .to(recipient)
                    .withSubject(null)
                    .withPlainText(this.getBodyMessage())
                    .buildEmail();
            this.mailer.sendMail(this.getEmail());
        }
    }

    public void popMessage() {
        Scanner s = new Scanner(System.in);
        System.out.println("For each recipient, enter the number, then when prompted, enter the carrier. After adding all desired recipients, type \"stop\".");
        String phone;
        String carrier;

        while (true) {
            System.out.print("Enter the recipient's phone number (no dashes or spaces), or type \"stop\" to finish: ");
            phone = s.nextLine().trim();
            if (phone.equalsIgnoreCase("stop")) {
                break;
            }
            if (phone.length() != 10) {
                System.out.println("Not a valid phone number");
                continue;
            }

            while (true) {
                System.out.print("Enter the recipient's cell carrier (i.e. Verizon Wireless): ");
                carrier = s.nextLine().trim();
                int count = 0;
                for (String[] carrierMM : carrierMMS) {
                    if (carrier.equalsIgnoreCase(carrierMM[0])) {
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    System.out.println("Invalid carrier, please try again.");
                    continue;
                }
                addRecipient(phone, carrier);
                break;
            }
        }
        System.out.println("Input message below:");
        String message = s.nextLine();
        addText(message);
    }

    public void sendWithDeliveryMethod(TextMailer txt) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("\nInput a time (hours,minutes) to send the message, or type now: ");
            if (s.hasNext()) {
                String input = s.next();
                if (input.equalsIgnoreCase("now")) {
                    txt.send();
                    break;
                } else {
                    String[] timeParts = input.split(",");
                    if (timeParts.length == 2) {
                        try {
                            int hours = Integer.parseInt(timeParts[0]);
                            int minutes = Integer.parseInt(timeParts[1]);
                            txt.scheduleSend(hours, minutes).close();
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input format for time. Please input in the format 'hours,minutes'.");
                        }
                    } else {
                        System.out.println("Invalid input format for time. Please input in the format 'hours,minutes'.");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        TextMailer txt = new TextMailer();
        txt.host = args[0];
        txt.smtp = args[1];
        txt.appPass = args[2];
        txt.setMailer();
        txt.popMessage();
        txt.sendWithDeliveryMethod(txt);
    }
}