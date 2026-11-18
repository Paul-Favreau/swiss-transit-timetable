# ADR 0003 : clés des tables GTFS

- **Statut :** accepté
- **Date :** 2026-11-18

## Contexte

Chaque fichier GTFS a son propre identifiant : `stop_id`, `route_id`, `trip_id`, `service_id`. Les lignes se
référencent par ces identifiants. Par exemple, une ligne de `stop_times.txt` pointe vers un trajet (`trip_id`) et un
quai (`stop_id`). Avant d'écrire la première migration, il faut choisir la clé primaire des tables, et donc le type
des clés étrangères.

Faits mesurés sur le flux 2027 (fichier du 2026-11-14) :
- `stop_times.txt` compte 15,9 millions de lignes pour la Suisse, dont 955 468 pour le réseau TPG.
- Un `trip_id` fait en moyenne 25 caractères (`.ojp-91-10-A.1.TA.1.j27`), un `stop_id` 19 à 21 caractères
  (`ch:1:sloid:87057:0:233278`).
- Tous les `trip_id` finissent par `j27`, l'année d'horaire : ils changeront au moins à chaque changement d'horaire.
  Les `stop_id` reprennent le SLOID, l'identifiant suisse des lieux d'arrêt, prévu pour durer (stabilité d'une
  publication à l'autre non vérifiée).
- L'import prévu recharge tout le flux à chaque fois (ROADMAP, J4) : les liens entre tables restent donc cohérents
  quel que soit le choix.

## Options

- **A. Clés naturelles :** l'identifiant GTFS sert de clé primaire (`VARCHAR`), et les clés étrangères pointent
  dessus. `stop_times` a une clé composée (`trip_id`, `stop_sequence`).
- **B. Clés techniques :** chaque table a un `BIGINT` généré par la base comme clé primaire, et garde l'identifiant
  GTFS dans une colonne unique. Les clés étrangères sont des `BIGINT`. Pour `stop_times`, deux formes : avec son
  propre identifiant généré, ou avec une clé composée (trajet, numéro d'arrêt) en `BIGINT`.

## Compromis

- **Import.** Avec A, les colonnes des fichiers vont telles quelles dans les tables, ce qui permet un `COPY` direct.
  Avec B, chaque ligne de `stop_times` doit traduire deux identifiants GTFS en identifiants techniques : une
  jointure avec une table de travail, ou une table de correspondance en mémoire. Mesuré en SQL sur les 15,9
  millions de lignes : 25 à 42 s avec la traduction, 24 à 30 s sans. Le coût est surtout une étape de plus à
  écrire et à tester, pas du temps.
- **Taille.** Mesurée le 2026-11-18 sur les 15,9 millions de lignes nationales de `stop_times` (PostgreSQL 18, clé
  primaire et un index quai + heure de départ) :
  - A : 2,5 Go (1,5 Go de données, 1,05 Go d'index) ;
  - B, avec un identifiant propre à `stop_times` : 2,2 Go ;
  - B, avec la clé composée en `BIGINT` : 1,7 Go.

  L'écart va de 13 % à 31 %, soit 0,3 à 0,8 Go pour la Suisse, et environ 50 Mo pour TPG (6 % des lignes). Pendant
  la bascule de J4, deux copies coexistent : les tailles doublent.
- **JPA.** Avec A, l'identifiant est déjà rempli avant l'enregistrement. `save()` de Spring Data considère alors
  l'entité comme existante et appelle `merge()` au lieu de `persist()` (vérifié dans le code de Spring Data JPA
  4.1.1). `merge()` cherche d'abord la ligne en base, ce qui ferait un `SELECT` par ligne : c'est à observer dans les
  logs SQL pendant la mesure de la version naïve. La clé composée de `stop_times` demande aussi `@EmbeddedId` ou
  `@IdClass`. Avec B, l'identifiant est généré et `save()` appelle `persist()` directement.
- **API.** Avec B, les identifiants techniques changent à chaque rechargement : l'API doit de toute façon exposer
  l'identifiant GTFS (`/api/stops/{stop_id}/departures`). Avec A, c'est déjà la clé.
- **Lisibilité.** Avec A, les identifiants en base sont ceux des fichiers, ce qui facilite le débogage.

**Décision :** option A, clés naturelles.

**Justification :** Je choisis d'utiliser l'identifiant des fichiers comme clé primaire. Ça permettra un import plus
simple des données, et que les identifiants en base soient ceux des fichiers facilitera la lisibilité et le
débogage.

**Ce que j'y perds :** un gain d'espace de stockage. Après avoir fait une projection, la différence est négligeable,
même si utiliser les identifiants des fichiers est un peu plus coûteux.
