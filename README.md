# Architecture Components and Retrofit

Dans ce TP, vous allez apprendre à faire un appel API avec Retrofit et à afficher les données dans
une RecyclerView.

## Configuration

- Cloner le projet et compilez-le. Il se peut que vous ayez à modifier les versions de gradle en
  fonction de la version d'Android Studio que vous utilisez.
- Prendre le temps d'analyser les codes, il ya quelques TODO à traiter.

## Améliorer l'app

- Ajouter une option permettant de supprimer 1 ou plusieurs photos de la liste. Par exemple, quand
  l'utilisateur fait un appui long sur une photo, vous pouvez faire passer la liste en mode
  selectionnable. L'utilisateur peut alors sélectionner plusieurs photos et les supprimer en
  cliquant sur un bouton. L'API ne supportant pas la suppression, vous pouvez le faire sur la liste
  des objets en mémoire.
- Ajouter une option permettant de partager une photo. 
- Ajouter une vue détail (i.e un fragment). Quand l'utilisateur clique sur une photo, il doit voir la photo en grand.
- Permettre de rechercher/filtrer les photos par tag, like, partage ou suppression

## Mettre en place une base de données

- Ajouter une base de données Room à votre projet. Quand les photos sont récupérées depuis l'API,
  vous les sauvegardez dans une base de données locales afin de permettre l'utilisation de l'
  application en mode Offline. Ainsi, au lancement de l'application, si l'utilisateur n'est pas
  connecté à internet; vous affichez les photos sauvegardées dans la base de données locales.
- Modifiez la fonction de suppression pour supprimer les photos dans la base de données locales et
  non plus dans la liste en mémoire.
- Enrichir les données et la vue :
    - Mécanisme permettant de noter si une photo a été supprimée.
    - Mécanisme permettant d'indiquer si une photo a été partagée;
    - Mécanisme permettant d'indiquer si une photo a été visualisée;
    - Mécanisem permettant d'indiquer si une photo a été likée;
- Permettre de tagger les photos (ajoutant des tags et éditer l'image). Utiliser des librairies du
  type PhotoEditor.
- Ajouter une vue gallerie permettant de swiper pour passer à la photo suivante ou précédente.

## Fonctionnalités au choix (1 dans la liste)

- Possibilité de prendre une photo ou d'en selectionner depuis la gallerie et l'enregistrer dans la
  base de données
- Sécuriser votre application avec un système d'authentification (Firebase Auth par exemple) et
  permettre à l'utilisateur de se connecter avec son compte Google, Facebook ou Twitter.
- Sauvegarder les photos dans Firebase Storage en plus de la base de données locale.
- Ajouter une vue chat permettant de discuter avec les autres utilisateurs de l'application; en
  utilisant Firebase Realtime Database.

## Bonus

- 2+ fonctionnalités parmi la liste des fonctionnalités au choix
- Injection de dépendance avec HILT
- Tests unitaires avec JUnit
- Tests d'intégration avec Espresso

## Rendu
- Ajouter une fichier (RELEASE.md) dans lequel vous spécifierez :
    - La liste des fonctionnalités de l'application developpée (captures d'écran des vues assiciées)
    - Une vidéo de démo de votre application
    - La liste des fonctionnalités bonus
    - Les membres du groupe et les fonctionnalités qu'ils ont développées