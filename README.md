# TechSage Capital

## JWT Authentication Setup

Run the following commands to set up JWT authentication:

```bash
cd backend/src/main/resources
keytool -genkeypair -keyalg RSA -validity 365 -alias inholland -keystore inholland.p12 -storetype PKCS12
```

Use password `yourpassword`, or change `application.properties` to match your set password (Do not push to git please).
