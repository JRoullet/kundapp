[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
![Spring](https://img.shields.io/badge/Spring-Framework-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)


# KundApp : Plateforme de Réservation de Sessions de bien-être

KundApp est une application complète de gestion et de réservation de sessions de yoga, kundalini, pilates, méditation et soins énergétiques. Développée en architecture microservices, elle permet aux clients de s'inscrire à des sessions, aux teachers de créer et gérer leurs cours, et aux administrateurs de superviser l'ensemble de la plateforme.

## ✨ Fonctionnalités principales

### Pour les Clients
- 🔐 Inscription et authentification sécurisée
- 💳 Système de crédits avec animations en temps réel
- 📅 Consultation et filtrage des sessions disponibles
- ✅ Inscription/désinscription aux sessions
- 📜 Historique des sessions (terminées et annulées)

### Pour les Teachers
- 📝 Création et modification de sessions (présentiel ou en ligne)
- 👥 Visualisation des participants inscrits
- 🚫 Annulation de sessions avec remboursement automatique
- 🔒 Contraintes métier (crédits non modifiables si participants inscrits)

### Pour les Administrateurs
- 👤 Gestion complète des utilisateurs (clients et teachers)
- 📊 Gestion de toutes les sessions
- 💰 Modification des crédits utilisateurs
- 🎯 Supervision globale de la plateforme

## 🏗️ Architecture

### Microservices
- **ms-webapp** : Gateway Thymeleaf + Gestion sécurité (cookies de session)
- **ms-identity** : Service d'authentification + gestion crédits utilisateurs (MySQL)
- **ms-course-mgmt** : Gestion des sessions et réservations (MySQL)
- **ms-configserver** : Configuration centralisée Spring Cloud Config
- **ms-discovery** : Service discovery Eureka
- **ms-communication** : Service de notifications emails (MongoDB)

### Communication
- FeignClients pour communication inter-microservices
- Sécurité via `internalSecret` entre services
- Gestion d'erreurs avec codes HTTP spécifiques (400, 404)

## 🛠️ Technologies

### Backend
- **Java 17** / **Spring Boot 3.x**
- **Spring Security** (authentification basée sur les rôles)
- **Spring Cloud** (Eureka, Config Server)
- **MySQL** (données relationnelles)
- **MongoDB** (notifications)
- **JPA/Hibernate** (ORM)
- **MapStruct** (mapping DTO/Entity)
- **Docker** (containerisation complète)

### Frontend
- **Thymeleaf** (moteur de templates)
- **Bootstrap MDB** (Material Design Bootstrap)
- **Vanilla JavaScript** (Fetch API uniquement, pas de frameworks)
- **CSS** (styling personnalisé)

### DevOps
- Docker & Docker Compose
- Architecture multi-conteneurs
- Configuration externalisée

## 📋 Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL (si exécution hors Docker)
- MongoDB (si exécution hors Docker)

## 🚀 Installation et démarrage

### Avec Docker (recommandé)
```bash
# Cloner le repository
git clone [URL_DU_REPO]
cd kundapp

# Lancer tous les services
docker-compose up -d

# L'application sera accessible sur http://localhost:8080
```

### Sans Docker
```bash
# Démarrer les services dans l'ordre :
# 1. ms-configserver
# 2. ms-discovery
# 3. ms-identity
# 4. ms-course-mgmt
# 5. ms-communication (optionnel)
# 6. ms-webapp

# Pour chaque service :
cd [service-name]
mvn spring-boot:run
```

## 👥 Rôles et Permissions

| Rôle | Permissions |
|------|-------------|
| **CLIENT** | S'inscrit via l'interface, réserve des sessions |
| **TEACHER** | Créé par admin, crée/modifie/annule ses sessions, consulte participants |
| **ADMIN** | Gestion complète : utilisateurs, teachers, sessions, crédits |

## 🔑 Comptes de démonstration

```
Admin:
Email: [admin@gmail.com]
Password: [admin123]

Teacher:
Email: [teacher@gmail.com]
Password: [teacher123]

Client:
Email: [client@gmail.com]
Password: [client123]
```

## 📸 Captures d'écran

*[À COMPLÉTER avec vos captures d'écran]*

## 🧪 Tests
```bash
# Lancer les tests
mvn test

# Lancer les tests avec couverture
mvn clean verify
```

## 🤝 Contribuer au projet

Les contributions sont les bienvenues ! N'hésitez pas à :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request
6. Me contacter pour échanger

### Règles de développement

- Commentaires en anglais uniquement
- DTOs pour toutes les communications entre services
- Respect de l'architecture Controller/Service/Repository
- Vanilla JavaScript (pas de frameworks frontend)
- Tests unitaires obligatoires pour nouvelle fonctionnalité

## 📝 Roadmap

- [x] Système d'authentification et autorisation
- [x] Gestion complète des sessions
- [x] Système de crédits et transactions
- [x] Annulation avec remboursement automatique
- [x] Service de notifications emails (mocks)
- [ ] Notifications en temps réel
- [ ] Application mobile (future)
- [ ] Intégration paiement en ligne

## 👨‍💻 Auteur

- **[Jacques ROULLET]** - *Développement initial*

## 🙏 Remerciements

*[Merci aux personnes avec qui j'ai eu la chance d'échanger sur ce projet]*

- [Juliette Plazanet, Esteban Barré, Jean-Ely Gendreau, JC Bazin, Alejandro, Noann Nassivet, Salim Oujjet]

---

⭐ **Si ce projet vous plaît, n'hésitez pas à lui donner une étoile !** ⭐
