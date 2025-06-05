Ja es ist eine README - ich weiß dass die nicht gelesen werden, but here we go.

Die Modelle sind von mir, Nils Dellemann erstellt und sind alle unter der CC BY 2.0 DE (https://creativecommons.org/licenses/by/2.0/de/) verfügbar.
Als Namensnennung ist dabei dieses File, und das Lizenz File ausreichend (also einfach die Files mitkopieren).

Um die Modelle zu importieren müssen sowohl das .gltf und die .bin im selben ordner liegen, es muss aber über den AssetManager nur die .gltf importiert werden.

Turrets:
	Sowohl der Gatling als auch der Missile Turret/Tower (ich benutze das interchangeable) sind Artikulierbar.
	
	Die Idee dieser Modelle ist, dass sie auf ein Ziel zeigen können. 
	Das wird erreicht mithilfe der Armatur die hinter diesen Modellen steckt (siehe die Blender Files). 
	Darauf greift man zu indem man in der ModelInstance von dem Importierten SceneAsset (muss vorher noch zu einer Scene gemacht werden bevor man an die ModelInstance ran kommt) über .getNode() an die Bones der Armatur kommt, und von da aus diese Bones rotiert. 
	Das zeigen bekommt ihr hin, indem ihr einen Vektor vom Rotationsursprung der beim gatling um 1.1102 units, und beim missile um 1.38668 units vom Model Ursprung nach oben verschoben sind zum Zielpunkt bildet, 
	diesen Vektor als koordinaten zu sphereisch koordinaten umwandelt und dann die jeweiligen bones 
	(namen: "Turret" für den Kopf (also pitch/theta/oben-unten) und "Turret Rotator" für den halter (also yaw/phi/rechts-links)) 
	um die entsprechenden axen rotiert:

		pitchQuaternion = new Quaternion().set(new Vector3(0, 0, 1), (float) pitch);
		turretHead.rotation.set(pitchQuaternion);

	so stelle ich die Rotationen dar (in diesem fall um die z achse) turretHead ist hierbei ModelInstance.getNode("Turret")
	beim yaw/phi/rechts-links muss beachtete werden dass eventuell ein rotations-offset benötigt wird:

		yawQuaternion = new Quaternion().set(new Vector3(0, 1, 0), (float) yaw + 90)
		turretRotator.rotation.set(yawQuaternion);

	Am Ende muss man dann nurnoch modelInstance.calculateTransforms() aufrufen um die veränderungen zu bestätigen.
	
	Der Sonic Tower hat eine Animation namens "Rotate".
	Animationen können folgendermaßen abgespielt werden:
		
		sonicTowerAnimationController = new AnimationController(model);
        //Animations are Actions in the .blend file
        sonicTowerAnimationController.setAnimation("Rotate", -1);
		
	und in der .render() vom Screen muss iregenwo dann auf irgendeine art und weise diese Animation controller geupdated werden (siehe die Vorlage).
	Anzumerken ist, dass ein AnimationController nur eine Animation abspielen kann.
	
Gegner:
	Infanterie:
		Infanterie modelle sind am besten zu mehren als eine "Einheit" zu verwenden, wie/ob ihr das macht ist euch überlassen.
		Die Animation zum Laufen ist "Walk".
		
	Harvester:
		Hat keine Animation, ist eines der Simpelsten Modelle
		
	
	Boss:
		Oh boy der Boss. Es ist ein Ornithopter mit acht Flügeln die sich bewegen. Leider ist jeder einzelne dieser Flügel eine eigene Animation (technisch bedingt), daher hier der code um sie alle ans laufen zu bekommen:
		
			ArrayList<AnimationController> animationControllerArrayList = new ArrayList<>();
			//start animations
			for(int i = 0; i < 8; i++) {
				AnimationController animationController = new AnimationController(model);
				animationController.setAnimation("Fly.00" + i,-1);
				animationControllerArrayList.add(animationController);
			}
			
		vergesst nicht diese controller auch zu updaten!
		
Sonstiges:
	Sandwurm:
		Die Animation für den Sandwurm ist "Move"
	
	Portale:
		Keine Animationen. Einfach platzieren
		
	Standart_Tile:
		Das Tile (und Tile größe) auf der diese Modelle aufbauen. Die Tile size ist 2x2x1 (breite,länge,tiefe).
		
Das wars - ich hoffe das reicht um die Modelle zu verwenden, ansonsten könnt ihr mich unter nils.dellemann@uni-ulm.de (oder discord wenn ihr den kennt) erreichen.
Frohes schaffen!

-Nils