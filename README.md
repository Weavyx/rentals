# ChâTop — API REST Backend

API REST de gestion de locations saisonnières, développée avec Spring Boot 3 dans le cadre du projet ChâTop (OpenClassrooms).

Elle expose des endpoints pour l'authentification des utilisateurs, la gestion des annonces de location et l'envoi de messages. L'accès aux routes protégées est sécurisé par JWT.

---

## Stack technique

| Technologie | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.5 |
| Spring Security | (géré par Spring Boot) |
| Spring Data JPA | (géré par Spring Boot) |
| MySQL | 8.0 (via Docker) |
| JJWT | 0.11.5 |
| springdoc-openapi | 2.1.0 |
| Lombok | (géré par Spring Boot) |
| Maven Wrapper | fourni (`mvnw` / `mvnw.cmd`) |

---

## Prérequis

Les outils suivants doivent être installés sur la machine avant de démarrer le projet :

- **Java 17** — le projet requiert exactement Java 17 (voir [Dépannage](#dépannage) si une version plus récente est installée)
- **Maven** — non requis si l'on utilise le wrapper fourni (`mvnw` / `mvnw.cmd`)
- **Docker** et **Docker Compose** — pour lancer la base de données MySQL
- **Git** — pour cloner le dépôt

---

## Installation pas à pas

### 1. Cloner le dépôt

```bash
git clone <url-du-dépôt>
cd rentals
```

### 2. Configurer les variables d'environnement

Copier le fichier d'exemple et renseigner les valeurs :

```bash
# Linux / macOS
cp .env.example .env

# Windows (PowerShell)
Copy-Item .env.example .env
```

Ouvrir `.env` et compléter chaque variable :

| Variable | Obligatoire | Valeur par défaut | Description |
|---|---|---|---|
| `DB_URL` | Non | `jdbc:mysql://localhost:3306/rentals_db` | URL JDBC de connexion à MySQL |
| `DB_USERNAME` | **Oui** | — | Nom d'utilisateur MySQL |
| `DB_PASSWORD` | **Oui** | — | Mot de passe MySQL |
| `MYSQL_ROOT_PASSWORD` | **Oui** | — | Mot de passe root du conteneur Docker MySQL |
| `JWT_SECRET` | **Oui** | — | Clé secrète HS256 — **minimum 64 caractères** |
| `UPLOAD_DIR` | Non | `uploads/` | Répertoire de stockage des images uploadées |
| `BASE_URL` | Non | `http://localhost:3001` | URL de base du serveur (utilisée pour construire les URLs des images) |

> **Important :** le fichier `.env` est listé dans `.gitignore` et ne doit jamais être commité. Il contient des secrets.

### 3. Lancer la base de données

```bash
docker-compose up -d
```

Docker démarre un conteneur MySQL 8.0 (`chatop_mysql`) sur le port `3306`. Le schéma de la base de données (`rentals_db`) est **créé automatiquement** au premier démarrage : le script `src/main/resources/sql/init.sql` est monté dans `/docker-entrypoint-initdb.d/` et exécuté par MySQL lors de l'initialisation du conteneur sur un volume vierge.

Les tables créées automatiquement sont : `users`, `rentals`, `messages`.

> Aucune commande SQL manuelle n'est nécessaire.

### 4. Lancer le backend

**Windows (PowerShell ou invite de commande) :**

```powershell
mvnw spring-boot:run
```

**Linux / macOS :**

```bash
./mvnw spring-boot:run
```

### 5. Vérifier que l'API répond

Une fois démarré, l'API est disponible sur le port **3001**.

Tester avec curl :

```bash
curl http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

Ou ouvrir directement la documentation Swagger : [http://localhost:3001/swagger-ui/index.html](http://localhost:3001/swagger-ui/index.html)

---

## Base de données

### Schéma

Le fichier `src/main/resources/sql/init.sql` définit le schéma complet de l'application. Il est monté automatiquement dans le conteneur Docker via `docker-compose.yml` :

```
./src/main/resources/sql/init.sql
  → /docker-entrypoint-initdb.d/init.sql (lecture seule)
```

MySQL exécute ce script **une seule fois**, lors du premier démarrage sur un volume vide. Le schéma contient trois tables :

- `users` — comptes utilisateurs (email unique, mot de passe BCrypt)
- `rentals` — annonces de location (nom, surface, prix, image, description, propriétaire)
- `messages` — messages envoyés à propos d'une annonce

### Réinitialiser le volume (repartir de zéro)

Le script d'initialisation ne s'exécute que sur un **volume vierge**. Si le volume existe déjà (par exemple, après un premier `docker-compose up -d`), modifier `init.sql` n'aura aucun effet au prochain démarrage.

Pour forcer la réinitialisation complète :

```bash
docker-compose down -v   # supprime le conteneur ET le volume nommé chatop_mysql_data
docker-compose up -d     # recrée le conteneur et réexécute init.sql
```

> **Attention :** l'option `-v` supprime définitivement toutes les données contenues dans le volume. Cette opération est irréversible.

---

## Documentation de l'API (Swagger)

| Ressource | URL |
|---|---|
| Interface Swagger UI | [http://localhost:3001/swagger-ui/index.html](http://localhost:3001/swagger-ui/index.html) |
| Spécification OpenAPI (JSON) | [http://localhost:3001/v3/api-docs](http://localhost:3001/v3/api-docs) |

Swagger UI est accessible **sans authentification**.

### Tester les routes protégées dans Swagger

1. Appeler `POST /api/auth/login` (ou `/api/auth/register`) dans Swagger pour obtenir un token JWT.
2. Copier la valeur du champ `token` dans la réponse.
3. Cliquer sur le bouton **Authorize** (icône cadenas en haut à droite de Swagger UI).
4. Dans le champ **Bearer Authentication**, coller le token, puis cliquer sur **Authorize**.
5. Toutes les requêtes suivantes incluront automatiquement l'en-tête `Authorization: Bearer <token>`.

---

## Endpoints

| Méthode | URL | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Non | Inscription — retourne un token JWT |
| `POST` | `/api/auth/login` | Non | Connexion — retourne un token JWT |
| `GET` | `/api/auth/me` | Oui | Profil de l'utilisateur connecté |
| `GET` | `/api/rentals` | Oui | Liste des locations (`{ "rentals": [...] }`) |
| `GET` | `/api/rentals/{id}` | Oui | Détail d'une location |
| `POST` | `/api/rentals` | Oui | Création d'une location (`multipart/form-data`, image incluse) |
| `PUT` | `/api/rentals/{id}` | Oui | Mise à jour d'une location (champs texte uniquement) |
| `POST` | `/api/messages` | Oui | Envoi d'un message à propos d'une location |
| `GET` | `/api/user/{id}` | Oui | Détail d'un utilisateur par son identifiant |

**Sécurité :** les sessions sont `STATELESS` (pas de cookie de session). Toutes les routes, à l'exception des endpoints d'authentification et de la documentation Swagger, exigent un token JWT valide passé dans l'en-tête HTTP :

```
Authorization: Bearer <token>
```

Toute requête sans token ou avec un token invalide reçoit une réponse `401 Unauthorized`.

---

## Dépannage

### Erreur de version Java (JDK 25 ou supérieur détecté)

Le projet requiert **Java 17**. Sur Windows, si une version plus récente de JDK est installée (par exemple JDK 25), IntelliJ IDEA peut basculer automatiquement dessus, ce qui provoque une erreur de compilation.

**Solution — forcer Java 17 dans PowerShell avant de lancer Maven :**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
.\mvnw spring-boot:run
```

> Adapter le chemin à l'emplacement réel de votre installation Java 17.

**Dans IntelliJ IDEA :**

- `File → Project Structure → Project → SDK` → sélectionner **Java 17**
- Pour éviter que le paramètre soit réinitialisé : `File → New Projects Setup → Structure for New Projects → SDK` → **Java 17**

### Vérifier la version Java active

```bash
java -version
```

La sortie doit indiquer `version "17.x.x"`.