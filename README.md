# TechSage Capital

## JWT Authentication Setup

Run the following commands to set up JWT authentication:

```bash
cd backend
keytool -genkeypair -keyalg RSA -validity 365 -alias inholland -keystore inholland.p12 -storetype PKCS12
```

Use password `yourpassword`, or change `application.properties` to match your set password (Do not push to git please).

Copy the `inholland.p12` file to the `src/main/resources/` directory.
