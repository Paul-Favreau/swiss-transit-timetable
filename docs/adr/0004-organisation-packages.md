# ADR 0004 : organisation des packages du backend

- **Statut :** accepté
- **Date :** 2026-11-18

## Contexte

Le backend n'a pour l'instant qu'un package, `com.transport.backend`, qui contient la classe principale. Avant la
première entité, il faut choisir où ranger les classes.

Faits :
- Spring Boot n'impose aucune organisation (« Spring Boot does not require any specific code layout to work »). Il
  recommande seulement de placer la classe principale dans un package racine, au-dessus des autres :
  `@SpringBootApplication` cherche les composants et les entités `@Entity` dans ce package et ses sous-packages.
  Les deux options respectent cette règle. Source : [Structuring Your Code](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html),
  Spring Boot 4.1.
- L'exemple de cette page range par domaine : un package `customer` avec `Customer`, `CustomerController`,
  `CustomerService` et `CustomerRepository`, puis un package `order` construit de la même façon. La page renvoie
  vers Spring Modulith pour qui veut imposer une structure par domaine.
- Dans ce projet, les tables GTFS forment un seul modèle utilisé de deux façons : l'import les écrit toutes, et
  l'API des départs en lit la plupart (arrêts, trajets, horaires de passage, calendriers). Une table n'appartient
  donc pas à une seule fonctionnalité.
- La v1 reste petite : environ sept tables, deux ou trois endpoints (recherche d'arrêts, départs) et un import.
- Déplacer une classe d'un package à l'autre est une refactorisation que l'IDE fait seul. Tant que le code est
  petit, ce choix se corrige en quelques minutes.

## Options

- **A. Par couche :** un package par rôle technique, par exemple `controller`, `service`, `repository`, `entity`,
  `dto`, plus un package pour l'import.
- **B. Par fonctionnalité :** un package par usage, par exemple `stop` (recherche d'arrêts), `departure` (départs)
  et `gtfsimport` (import), plus un package commun pour les entités et repositories GTFS, partagés par l'import et
  l'API.

## Compromis

- **Trouver le code d'un usage.** Avec A, le code des départs est réparti entre quatre ou cinq packages. Avec B, il
  est regroupé dans un seul.
- **Modèle partagé.** Avec A, les entités vont toutes dans `entity` : la question ne se pose pas. Avec B, les
  entités GTFS n'entrent dans aucune fonctionnalité, puisque l'import et l'API les utilisent toutes : il faut un
  package commun, et décider ce qui y va. L'exemple de Spring Boot suppose une entité par fonctionnalité
  (`Customer` dans `customer`), ce qui n'est pas le cas ici.
- **Visibilité.** Avec B, une classe qui ne sert qu'à une fonctionnalité peut rester sans `public` : la visibilité
  par défaut de Java la limite à son package, et le compilateur empêche les autres packages de l'utiliser. Spring
  crée et injecte ces beans sans souci (vérifié avec Spring Framework 7.0.9, celui de Spring Boot 4.1.1, sur une
  classe et un constructeur sans `public`). Avec A, presque tout doit être `public`, puisque le controller appelle
  un service situé dans un autre package.
- **Taille.** Avec A, sept tables donnent sept entités dans `entity` et sept repositories dans `repository`, tandis
  que `controller` et `service` restent petits. Avec B, on a trois ou quatre packages de quelques classes chacun. À
  cette taille, les deux se lisent bien.

**Décision :** option A, par couche.

**Justification :** Je pars sur l'organisation par couche, avec un package par rôle technique. J'ai déjà travaillé
dans cette configuration, donc je m'y retrouverai plus facilement. Elle est aussi plus adaptée au projet, car les
entités GTFS n'entrent dans aucune fonctionnalité.

**Ce que j'y perds :** il est parfois plus compliqué de trouver le code d'un usage, surtout quand on est nouveau sur
un projet organisé ainsi. Je suis seul sur ce projet, donc c'est moins embêtant pour moi.
