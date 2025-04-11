# Prosjekt README

## Installasjon og kjøring

### Forutsetninger

- Docker og Docker Compose installert
- Maven installert (for backend-tester)
- Node.js installert (for frontend-tester)
- .env fil med api nøkkler o.l. kan sendes ved behov

### Slik kommer du i gang

1. Klon prosjektet:

```
git clone <prosjekt-url>
cd <prosjekt-mappe>
```

2. Start applikasjonen med Docker Compose:

```
docker compose build
docker compose up -d
```

Dette vil bygge og starte alle nødvendige tjenester definert i docker-compose.yml filen.

3. Åpne applikasjonen i nettleseren:

```
http://localhost:5173
```

### Å stoppe applikasjonen

```
docker compose down
```

For å fjerne alle volumer og containere fullstendig:

```
docker compose down -v
```

## Testing

### Backend-tester

For å kjøre backend-testene:

```
cd backend
mvn test
```

Dette vil kjøre alle enhetstester og integrasjonstester for backend-delen av applikasjonen.

### Frontend-tester

For å kjøre Cypress E2E-tester:

```
cd frontend
npm install     # Installererer avhengigheter (kun nødvendig første gang)
npm run cypress:run   # Kjører Cypress-tester i headless modus
```

For å åpne Cypress testrunner i interaktiv modus:

```
cd frontend
npm run cypress:open
```

## Vanlige problemer

- Hvis docker-containere ikke starter korrekt, sjekk at portene ikke er i bruk av andre tjenester.
- Hvis frontend tester feiler med nettverksfeil, sjekk at applikasjonen kjører og er tilgjengelig på forventet URL.

## Teknisk dokumentasjon

For mer detaljert dokumentasjon, se eventuelt:

- Backend API-dokumentasjon med swagger på `http://localhost:8080/api-docs/` (når applikasjonen kjører)
