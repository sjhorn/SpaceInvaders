#Groovy Space Invaders

![Groovy Space Invaders](https://github.com/sjhorn/SpaceInvaders/raw/master/gfx/GroovySpaceInvader.png)

## Download:

### Cross platform jar
<https://github.com/downloads/sjhorn/SpaceInvaders/SpaceInvaders_1.0.jar>

### Mac OSX Application Bundle
<https://github.com/sjhorn/SpaceInvaders/releases/download/1.1/SpaceInvaders_1.1.dmg>

## Run


### For the cross platform jar
````
java -XstartOnFirstThread -jar SpaceInvaders.jar
````

### For Mac OSX Application Bundle
```
Download the dmg file above.
Double click to mount the dmg
Drag the SpaceInvaders icon into the Application Folder
```

## Keys

###Player 1:

````
space - fire
left key - left
right key - right
````

###Player 2: 

````
f - fire
g - left 
j - right
````

### Game Control
````
1 - Restart in one player mode(default).
2 - Restart in two player mode.
3 - Toggle AI for Player 1.
4 - Toggle invincible bullets.
x - Destroy all invaders (for testing and humour)
````


## Build,run and package source

###Build
Clone the repo and run the following:
````
ant build
````

###Run source
````
ant run
````

###Package into runnable jar

````
ant package
````

This jar is then available in the `dist/SpaceInvaders.jar` file. This can be run on most OSX, Linux and Windows using
````
java -XstartOnFirstThread -jar SpaceInvaders.jar
````
On linux and windows you should be able to double click that jar

