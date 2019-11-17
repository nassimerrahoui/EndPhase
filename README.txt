Pour lancer le mode mono JVM :

1 - lancer la classe CVM du package src/app

Pour executer le mode distribué :

0 - modifier le path de dir dans config.xml
1 - lancer le script start-greistry
2 - lancer le script start-cyclicbarrier
3 - lancer la classe DistributedCVM du package src/app 7 fois avec les paramètres suivants :
	
	controleur config.xml
	frigo config.xml
	ordinateur config.xml
	chargeur config.xml
	compteur config.xml
	batterie config.xml
	panneau config.xml
	 