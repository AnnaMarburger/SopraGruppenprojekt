# Server (Team 04) Instructions

## Structure
- Im Ordner docker befinden sich alle benötigten Dateien um den zur Messe erworbenen Server zu starten
  - Starten ist entweder über die docker-compose.yml möglich oder direkt mit der Dockerfile. Wird die Dockerfile verwendet, so müssen alle Angaben aus der docker-compose.yml beachtet werden.
  - Werden program parameters an den Server übergeben, so werden diese benutzt, ansonsten werden die environment variables verwendet
  - Environment Variables werden in der separaten Datei .env.dev eingetragen:
    - CONFIG_PARTY=**party config path**
    - CONFIG_SCENARIO=**scenario config path**
- Im Ordner server_imp befindet sich der Server source code (Maven)

## Usage
- mit docker-compose:
  - (MacOS - zsh) docker compose create -> erstellt docker container
  - (MacOS - zsh) docker start **containerName** -> startet server

## Technologien
- Java 15.0.1 Maven
- GSON
- everit (json validation)
- SonarQube
## **WICHTIG!**
  - Port des Servers ist 10191