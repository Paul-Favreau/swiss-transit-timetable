# ADR 0001 : socle technique

- **Statut :** accepté
- **Date :** 2026-10-15

## Contexte

Le projet est une application d'horaires des transports publics suisses alimentée par le flux GTFS officiel
(ZIP d'environ 200 Mo, publié deux fois par semaine). C'est un projet vitrine pour une recherche d'emploi
en développement Java/Spring et Angular, en Suisse romande et dans la zone frontalière. Il doit tourner en conteneurs,
et chaque choix doit pouvoir se défendre en entretien.

Les décisions ci-dessous sont prises ensemble parce qu'elles conditionnent la génération du projet.

> Les parties « Justification » et « Ce que j'y perds » sont à rédiger par Paul avec ses propres mots,
> en 2 ou 3 phrases chacune. C'est l'exercice : si tu ne sais pas l'écrire, tu ne sauras pas le dire en entretien.

## 1. Java 25 LTS

**Options**
- **Java 21 LTS :** la version la plus déployée en entreprise aujourd'hui.
- **Java 25 LTS :** la LTS actuelle, supportée par Spring Boot 4.1.

**Compromis.** Les fonctionnalités utilisées dans le projet (records, sealed, pattern matching, threads virtuels)
existent déjà en 21. Partir sur 25 ne coûte donc rien, et l'écart avec un projet d'entreprise en 21 reste minime.

**Justification :** _à rédiger_

**Ce que j'y perds :** _à rédiger_

## 2. Spring Boot 4.1

**Options**
- **Spring Boot 3.5 :** la dernière 3.x, celle des projets existants, mais en fin de support open source.
- **Spring Boot 4.1 :** la ligne supportée.

**Compromis.** La 4.x apporte Jakarta EE 11, Jackson 3, des starters plus modulaires et les annotations de nullité
JSpecify. La plupart des entreprises sont encore en 3.x, voire en 2.x : connaître les différences devient un argument.

**Justification :** _à rédiger_

**Ce que j'y perds :** _à rédiger_

## 3. Maven

**Options**
- **Gradle :** déjà connu via Android, builds incrémentaux plus rapides, DSL Kotlin.
- **Maven :** déclaratif, le plus répandu sur les projets Spring en entreprise.

**Compromis.** Maven est moins souple, mais plus prévisible et lisible par n'importe quelle équipe. Il ajoute un
deuxième outil de build au CV.

**Justification :** _à rédiger_

**Ce que j'y perds :** _à rédiger_

## 4. PostgreSQL

**Options**
- **MySQL/MariaDB :** très répandu.
- **PostgreSQL :** également répandu, avec des atouts propres au projet.
- **H2 :** en mémoire, réservé aux tests.

**Compromis.** PostgreSQL offre `COPY` pour l'import massif de `stop_times`, l'extension `pg_trgm` pour la recherche
d'arrêts approximative, et PostGIS pour les arrêts proches plus tard. H2 est écarté y compris pour les tests :
Testcontainers fait tourner les tests sur le vrai moteur.

**Justification :** _à rédiger_

**Ce que j'y perds :** _à rédiger_

## 5. Monorepo

**Options**
- **Deux dépôts :** backend et frontend séparés.
- **Un dépôt :** `backend/`, `frontend/` et `deploy/` réunis.

**Compromis.** Le monorepo donne un seul lien à mettre sur le CV et un seul `docker compose up` pour tout lancer.
Une CI unique, avec des filtres de chemins, construit seulement ce qui a changé. Le prix : une organisation moins
réaliste pour une grande équipe, où front et back ont souvent des cycles de livraison séparés.

**Justification :** _à rédiger_

**Ce que j'y perds :** _à rédiger_

## 6. Données de développement : un sous-ensemble régional jusqu'à J4

**Options**
- **Flux national dès le départ :** on est tout de suite sur le volume réel.
- **Une région (TL Lausanne ou TPG Genève) :** jusqu'à l'import Spring Batch.

**Compromis.** Une région s'importe en quelques secondes et permet d'itérer vite pendant J1 à J3. Le risque est de
découvrir tard les problèmes de volume, mais c'est justement le sujet de J4, et l'import naïf mesuré en J1 en donnera
un avant-goût.

**Justification :** _à rédiger_

**Ce que j'y perds :** _à rédiger_
