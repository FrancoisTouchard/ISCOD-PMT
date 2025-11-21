# ISCOD-PMT

Étude de cas Project Management Tool du bloc Pilotage de projet d'ingénierie du logiciel.
C'est un site web qui permet de faire de la gestion de projet collaborative avec des fonctionnalités basiques.

## Stack imposée :

Backend: Java 21 (SpringBoot & Maven)
Frontend : Angular 19
Database : PostgreSQL

## Accéder à l'application

### En local :

Frontend Angular

Se placer dans le répertoire frontend

`cd frontend/pmt`

Installer les dépendances

`npm install`

Lancer le serveur de développement

`ng serve`

Accéder à l'application

URL : http://localhost:4200
Le serveur se recharge automatiquement à chaque modification

Backend Spring Boot

Se placer dans le répertoire backend

`cd backend/pmt`

Lancer l'application avec Maven

`mvn spring-boot:run`

### Avec Docker :

Se placer à la racine du projet

`cd ISCOD-PMT`

Lancer l'application avec Docker Compose

`docker compose up --build`

- **Frontend** : [http://localhost:80](http://localhost:80)
- **Backend API** : [http://localhost:8080](http://localhost:8080)

## Procédure de déploiement

Le projet utilise une pipeline CI/CD automatisée via **GitHub Actions** qui s'exécute sur chaque push ou pull request vers la branche `main`.

### Architecture du pipeline

Le workflow CI/CD comprend 7 jobs orchestrés :

#### 1️⃣ **Tests Frontend** (`test-frontend`)

- Exécution du linting Angular
- Tests unitaires avec Karma/Jasmine
- Génération du rapport de couverture
- Vérification du seuil minimal de couverture (60%)
- Upload des rapports de couverture en artifact

#### 2️⃣ **Tests Backend** (`test-backend`)

- Détection automatique du build tool (Maven/Gradle)
- Compilation et tests unitaires
- Génération du rapport JaCoCo
- Vérification du seuil minimal de couverture (60%)
- Upload des rapports de couverture et résultats de tests

#### 3️⃣ **Build Docker Backend** (`docker-backend`)

- **Déclenché uniquement** sur push vers `main` (pas sur PR)
- Construction de l'image Docker multi-stage :
  - Stage 1 : Build Maven avec JDK 21
  - Stage 2 : Runtime avec JRE Alpine (optimisation taille)
- Push vers Docker Hub avec tags :
  - `latest` (dernière version stable)
  - `main-{sha}` (version spécifique au commit)

#### 4️⃣ **Build Docker Frontend** (`docker-frontend`)

- **Déclenché uniquement** sur push vers `main` (pas sur PR)
- Construction de l'image Docker multi-stage :
  - Stage 1 : Build Angular avec Node.js 22
  - Stage 2 : Serveur Nginx Alpine avec configuration personnalisée
- Push vers Docker Hub avec tags identiques au backend

#### 5️⃣ **Build Frontend Artifact** (`build-frontend`)

- Build de production Angular
- Upload du dossier `dist/` en artifact (conservé 7 jours)

#### 6️⃣ **Build Backend Artifact** (`build-backend`)

- Génération du fichier JAR exécutable
- Upload de l'artifact (conservé 7 jours)

#### 7️⃣ **Notification** (`notify`)

- Récapitulatif du statut de tous les jobs
- Vérification de la réussite globale du pipeline

### Flux de déploiement

```
┌─────────────────────────────────────────────────────┐
│  Push sur main                                   │
└──────────────────┬──────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
┌───────▼────────┐   ┌────────▼───────┐
│ test-frontend  │   │ test-backend   │
│  • Lint        │   │  • Build       │
│  • Tests       │   │  • Tests       │
│  • Coverage    │   │  • Coverage    │
└───────┬────────┘   └────────┬───────┘
        │                     │
        │ (si push main)      │ (si push main)
        │                     │
┌───────▼────────┐   ┌────────▼───────┐
│docker-frontend │   │ docker-backend │
│  • Build image │   │  • Build image │
│  • Push to Hub │   │  • Push to Hub │
└───────┬────────┘   └────────┬───────┘
        │                     │
        └──────────┬──────────┘
                   │
           ┌───────▼────────┐
           │     notify     │
           │ • Status recap │
           └────────────────┘
```

## Variables d'environnement requises

Configurez les secrets GitHub suivants dans votre repository :

DOCKER_USERNAME Nom d'utilisateur Docker Hub

DOCKER_PASSWORD Mot de passe Docker Hub

## Images Docker publiées

Les images sont disponibles sur Docker Hub :

Backend : {DOCKER_USERNAME}/pmt-backend:latest

Frontend : {DOCKER_USERNAME}/pmt-frontend:latest

## Fonctionnalité de notifications par email

Pour tester localement l'envoi d'emails (sans serveur SMTP réel), le projet utilise MailHog :

Lancer MailHog

`docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog`

Visualiser les emails

Interface web : http://localhost:8025
Port SMTP : 1025

## Documentation API Swagger

Accessible via http://localhost:8080/swagger-ui/index.html
