#Groovy Space Invaders

![Groovy Space Invaders](https://github.com/sjhorn/SpaceInvaders/raw/master/gfx/GroovySpaceInvader.png)

## Download:

<https://github.com/downloads/sjhorn/SpaceInvaders/SpaceInvaders.jar>

## Run

````
java -XstartOnFirstThread -jar SpaceInvaders.jar
````

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

