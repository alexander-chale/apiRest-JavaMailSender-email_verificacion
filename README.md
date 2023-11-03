# email-verifycation-spring-boot-3
User Registration with send email link verification to activate user account

![Screenshot 2023-04-06 at 12 39 03 (2)](https://user-images.githubusercontent.com/87047616/230273451-b23bd11a-182d-4c0e-b757-5eddd611ac5a.png)


Run dummy SMTP Server locally I use python for this.

sudo python -m smtpd -n -c DebuggingServer localhost:25

Trigger email curl -X POST http://localhost:8080/email/trigger
