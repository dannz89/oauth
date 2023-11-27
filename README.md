# oauth
Minimal OAuth2 custom provider example to show spring OAuth2 authentication fails using this custom provider (WordPress OAuth2 plugin).

<h1>Purpose of project</h1>
Basic OAuth2 custom provider example to show spring OAuth2 authentication fails using this custom provider (Wordpress OAuth2 plugin).
This project is to re-create a problem with Spring security 6 / OAuth2 when used with a custom OAuth2 provider, namely a <a href="https://wp-oauth.com/">WordPress OAuth2 plugin.</a> 

<h1>How the project demonstrates the problem</h1>
When the app is started, it serves on <a href="http://localhost:8080/home">http://localhost:8080/home </a>

com.ddt.auth.MVCConfig maps to the error.html, index.html and secret.html to /error, /home and /secret respectively.

com.ddt.oauth.configuration.OAuth2SecurityConfig secures "/secret" (secret.html) using Spring Security (which in turn uses OAuth2). 

When opening the browser on the 'home' page (index.html), click the link to take you to the secure page ("secret.html"). You will then be taken into the OAuth2 authentication process. This will redirect you to the OAuth2 provider and the login page will appear. After successful login, the authentication will fail you will be redirected to spring boot OAuth2 default login failure page with an [authorization_request_not_found] error. 

<h1>Why it fails</h1>
I turned on tracing for the relevant packages then used debugging to see what was happening. I discovered that the state variable (for example "XXXYYY12345" is padded with an '=' ("XXXYYY12345%3D") when calling out to the provider but either the provider strips the '=' or spring security strips it. 

In any case, once the provider calls back to spring OAuth2, OAuth2 cannot find the session because it is holding state "XXXYYY12345%3D" but receives "XXXYYY12345" in the callback. It then fails
to authenticate because it has lost the relevant session. 

On the other hand, if using Postman to authenticate, the provider works fine. Even if copying the value of state="XXXYYY12345%3D" and pasting in to the 'state' field in Postman, the authentication still works and a token is generated successfully.

<h2>Config</h2>
I have .gitignore'd my real application properties. Here are all the properties you need for it to work. The values I have provided here are correct in that they reflect the same endpoint mappings used by the WordPress plugin but I've chnaged the site URL. I have also, needless to say given fake random values for the sensitive data rather than the real ones. Contact me if you want to test privately with the actual plugin and I can provide you with the client secret / id and site URL.

spring.security.oauth2.client.registration.crm.clientID=01234abcdefveryimportantsecretid111222333
spring.security.oauth2.client.registration.crm.clientSecret=abcdefghijklman9998888veryimportantsecretsecret1111
spring.security.oauth2.client.registration.crm.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.crm.client-name=CRM
spring.security.oauth2.client.registration.crm.redirect-uri=http://localhost:8080/login/oauth2/code/crm
spring.security.oauth2.client.registration.crm.userNameAttributeName=user_nicename

spring.security.oauth2.client.registration.crm.provider=crm
spring.security.oauth2.client.provider.crm.authorization-uri=https://my.wordpress.site.com/oauth/authorize
spring.security.oauth2.client.provider.crm.token-uri=https://my.wordpress.site.com/oauth/token
spring.security.oauth2.client.provider.crm.user-info-uri=https://my.wordpress.site.com/oauth/me

logging.level.org.springframework.security=TRACE
logging.level.org.springframework.security.oauth2=TRACE
logging.level.org.springframework.web=TRACE
spring.mvc.log-request-details=true