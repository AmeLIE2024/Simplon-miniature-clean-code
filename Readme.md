# Miniature — Mini Réseau Social

Application web Java de type réseau social minimaliste, développée dans le cadre de la formation Simplon.  
Stack : **Jakarta Servlets · JSP · Tomcat embarqué · Gradle**

---

## Lancement de l'application

### Prérequis

- Java 21+
- Gradle (pas d'installation nécessaire)

### Démarrer le serveur

```bash
./gradlew run
```

L'application démarre sur **http://localhost:8080** (ou le port configuré dans `App.java`).

### Build sans lancer

```bash
./gradlew build
```

### Nettoyer le build

```bash
./gradlew clean
```

> Tomcat est embarqué dans l'application via Gradle — aucune installation de serveur externe n'est nécessaire.

---

## Comptes de test

| Utilisateur | Mot de passe |
|-------------|--------------|
| `admin`     | `admin`      |
| `user`      | `password`   |

Ces comptes sont initialisés au démarrage dans `AppContextListener`.

---

## Structure des packages

```
src/main/java/fr/simplon/
│
├── application/
│   └── usecase/              # Cas d'usage métier
│
├── domain/
│   ├── exception/            # Exceptions métier
│   ├── models/               # Entités (User, Post, Comment…)
│   ├── repository/           # Interfaces des repositories
│   ├── services/             # Interfaces des services
│   └── strategy/             # Stratégies métier
│
├── infrastructure/
│   ├── repository/           # Implémentations des repositories (en mémoire)
│   └── services/             # Implémentations des services
│
└── presentation/
    ├── listeners/            # Initialisation des dépendances
    ├── servlets/             # Servlets HTTP (LoginServlet, PostServlet…)
    └── App.java              # Point d'entrée — démarrage Tomcat embarqué
```

---

## Pourquoi ce découpage ?

### `domain/` — le cœur métier

Contient uniquement les règles métier pures : modèles, interfaces de services et de repositories.  
Ce package **ne dépend de rien d'autre** dans le projet. Il ne connaît ni Tomcat, ni Jakarta, ni les implémentations concrètes.

> Objectif : si on change de framework demain, le domaine reste intact.

### `application/usecase/`

Chaque use case représente une action utilisateur précise (`LoginUseCase`, `RegisterUseCase`).  
Ils orchestrent le domaine sans connaître la couche HTTP.

> Principe appliqué : **Single Responsibility** — un use case = une responsabilité.

### `infrastructure/`

Implémentations concrètes des interfaces définies dans le domaine.  
Actuellement en mémoire (`ArrayList`), facilement remplaçables par une vraie base de données sans toucher au reste.

> Principe appliqué : **Dependency Inversion** — le domaine dépend des interfaces, l'infrastructure implémente.

### `presentation/`

Couche HTTP : servlets, JSP, listener. C'est la seule couche qui connaît Jakarta Servlet.  
Les servlets ne font que lire les paramètres HTTP, valider le format, déléguer aux services, et répondre.

> Principe appliqué : **Separation of Concerns** — la présentation ne contient pas de logique métier.

### `presentation/listeners/AppContextListener`

Point d'assemblage unique de toute l'application (**Composition Root**).  
C'est ici et seulement ici que les implémentations concrètes sont instanciées et injectées dans le `ServletContext`.  
Les servlets récupèrent leurs dépendances via `context.getAttribute()` sans jamais faire de `new ServiceImpl()`.

> Principe appliqué : **Dependency Inversion + Open/Closed** — ajouter un service = modifier uniquement le listener.

---

## Flux d'une requête

```
Navigateur
   │
   ▼
Servlet (lecture des paramètres HTTP, validation du format)
   │
   ▼
Service (logique métier, orchestration)
   │
   ▼
Repository (accès aux données)
   │
   ▼
Servlet (redirige ou forward vers la JSP)
   │
   ▼
JSP (rendu HTML)
```

---

## Fonctionnalités

- Inscription et connexion (session HTTP)
- Fil de recommandations et fil d'abonnements
- Création de posts avec contenu Markdown
- Pièces jointes : image, vidéo, lien externe (HTTPS uniquement)
- Système de likes par utilisateur
- Commentaires sur les posts
- Suivi d'autres utilisateurs (follow)
- Déconnexion sécurisée (POST uniquement)
