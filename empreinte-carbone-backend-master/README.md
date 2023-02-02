# empreinte-carbone-backend
## Prise en main du développement back-end
## Environnement de développement
- Java 8
- Eclipse (version Web Developers) avec les plugins Egit et Spring Tools 3
- Maven
- Plugin Lombok : télécharger le .jar sur projectlombok.org, le copier à la racine du projet et run la commande java -jar sur ce jar.
- deux profils SpringBoot : dev et prod

## Import du projet dans Eclipse
- Se connecter à Github et générer un jeton d’accès personnel (Paramètres > Paramètres du développeur > Jetons d’accès personnels > générer un nouveau jeton
)
- Sur Eclipse : File > Import > Import git with smart import
- Sélectionner Git > Projects from Git > Next > URI 
- Indiquer le chemin du repository GitHub
- Une fenêtre d’authentification apparait. L'identifiant est l'identifiant GitHub et le mot de passe, le jeton d'accès généré sur GitHub.

## Lancer l'API
- Ouvrir un terminal dans Eclipse
- Se placer dans le dossier du projet
- Commande : `mvn clean install`
- Lancer l'application avec la commande : `docker-compose up`


