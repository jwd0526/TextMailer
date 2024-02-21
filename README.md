# TextMailer
TextMailer is a Java package that allows you to send text messages via email to various mobile carriers. It provides a convenient way to compose and schedule text messages to be delivered at a specified time.

## How TextMailer Works
Email Creation: TextMailer uses the Simple Java Mail library to construct an email. This involves specifying the recipient, message content, and any attachments. The library provides a fluent interface to build the email object.

**SMTP Interaction:** Once the email is created, TextMailer interacts with an SMTP server to send the email. This is done using the Mailer class from the Simple Java Mail library, which handles the connection to the SMTP server and sends the email using the send() method.

**MMS Conversion:** TextMailer will format the recipient's address using the carrier's MMS gateway. For example, if the recipient's phone number is "1234567890" and the carrier's MMS gateway is "@mms.carrier.com", the address used to send the MMS would be "1234567890@mms.carrier.com"

**Sending MMS:** The email, now formatted to be sent as an MMS, is transmitted through the SMTP server to the carrier's MMS gateway, which then converts the email into an MMS message and delivers it to the recipient's mobile device.

## Prerequisites
Before using TextMailer, make sure you have the following:

* Java Development Kit (JDK) installed on your system.
* Access to an SMTP server that supports TLS authentication.
  - Most email services do, I use smtp.google.com in my implementation.
* Some form of authentification token.
  - Google recommends OAuth2, but an app password works just as well.
  - Below is a link on how to setup an app password for your google account.
  - [https://support.google.com/mail/answer/185833?hl=en](url)

## Installation
To use TextMailer in your Java project, you can add the following dependency to your pom.xml if you're using Maven:
```
<dependency>
    <groupId>org.simplejavamail</groupId>
    <artifactId>simple-java-mail</artifactId>
    <version>8.6.3</version>
</dependency>
```
Or if you're using Gradle, add this to your build.gradle file:
```
implementation 'org.simplejavamail:simple-java-mail:8.6.3'`
```
Additionally, be sure to add the supplied 'TextMailer.jar' file to your classpath.
## Usage
**Command Line Arguments:** There are three command line arguments: host, smtp, and appPass
* host: The email address used to host the messaging.
* smtp: The SMTP server gateway (i.e. smtp.gmail.com).
* appPass: Your authentification token.

Add these parameters as command line arguments ([host] [smtp] [appPass])

**Initialize TextMailer:** The main method will create an instance of TextMailer and initialize it with the command line arguments.
```
public static void main(String[] args) {
    TextMailer txt = new TextMailer();
    txt.host = args[0];
    txt.smtp = args[1];
    txt.appPass = args[2];
    txt.setMailer();
    txt.popMessage();
    txt.sendWithDeliveryMethod(txt);
}
```
**Compose Message:** Use the popMessage() method to interactively add recipients and compose your text message. Follow the prompts to enter recipient phone numbers and carrier details.

**Send Immediately or Schedule:** After composing the message, choose whether to send it immediately or schedule it for later delivery.

To send immediately, type "now" when prompted for the delivery time.

To schedule delivery for a specific time, enter the time in hours and minutes format (e.g., 13,30 for 1:30 PM).
## Carrier Support
TextMailer supports sending text messages to the following carriers:

* Alltel
* AT&T
* Boost Mobile
* Cricket Wireless
* MetroPCS
* Google Fi
* Republic Wireless
* Sprint
* T-Mobile
* U.S. Cellular
* Verizon Wireless
* Virgin Mobile

To determine a phone number's cell carrier, use a website like [https://freecarrierlookup.com/](url).
* Additionally, there are API services that could be used to automate this carrier assignment, but I could not find any free of charge.

## Additional Comments
* Designed as a way to send mms/sms messages for free in an automated format.
* On my machine, there is a bug with SLF4J: 'SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
  * SLF4J does offer a solution to this ([http://www.slf4j.org/codes.html#StaticLoggerBinder](url)), but I could not get it to work.
  * This bug does not impact code functionality.
* Due to my fear of annoying people, I've yet to test this on a large scale, 15 people at most, so i'm not sure how well it could handle large numbers of recipients.
  * I'm sure a multiThreading approach would work.
* The ScheduledExecutorService class implementation enables the repetition of the send() method with slight modification.
  * Change the scheduleSend() method to void, and remove the return statement.
  * Line 87: `scheduler.schedule(task, initialDelay, TimeUnit.MILLISECONDS);` --> `scheduler.scheduleAtFixedRate(task, initialDelay, [TimeUnit], TimeUnit.MILLISECONDS);`
  * Line 168: `txt.scheduleSend(hours, minutes).close();` --> `txt.scheduleSend(hours, minutes);`
* To see another implementation of this idea, check out my other project [https://github.com/jwd0526/PuppyPledge](url)

## Contributing
Contributions to TextMailer are much welcome. If you encounter any issues or have suggestions for improvements, feel free to open an issue or submit a pull request.

## License
This project is licensed under the MIT License.
