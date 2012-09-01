package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

@CompileStatic
abstract class ShipSprite extends Sprite {
    static final Sound shiphit = new Sound("shiphit.wav")
    static final long STARTING_TIME = 2_000_000_000
    static final long EXPLODING_TIME = 1_200_000_000
    public boolean moveLeft = false
    public boolean moveRight = false
    long blinkFrameTime = 0
    long explosionFrameTime = 0
    
    long startingTime = STARTING_TIME
    long explodingTime = 0
    
    int lives = 3
    List<Rectangle> lifeRectangles
    
    ShipSprite(List<Rectangle> rects, Rectangle rect, DoubleRectangle drect) {
        super(rects, rect, drect)
    }
    
    void nextState() {
        frameIndex++
        if(isStarting() && frameIndex > 1) {
            frameIndex = 0
        } else if (isExploding() && frameIndex > 3) {
            frameIndex = 2
        }
    }
    
    void draw(Image spriteSheet, GC gc) {
        if(isStarting()) {
            Rectangle l = lifeRectangles[lives]
            gc.drawImage(spriteSheet,
                l.x, l.y, l.width, l.height,
                (bounds.x+ bounds.width/2 + 20) as int, location.top as int, 42, 20)
            super.draw(spriteSheet, gc)
        } else if (lives > 0) {
            super.draw(spriteSheet, gc)
        }
    }
    
    boolean move(long timePassed, boolean freeze) {
        
        // Show the starting state for 0 lives briefly before game over
        if(isStarting() || lives < 1) {
            speedX = 0
            startingTime -= timePassed
            blinkFrameTime += timePassed
            if(blinkFrameTime > 250_000_000) {
                nextState()
                blinkFrameTime = 0
            }
        } else if(isExploding()) {
            speedX = 0
            explodingTime -= timePassed
            explosionFrameTime += timePassed
            if(explosionFrameTime > 120_000_000) {
                nextState()
                explosionFrameTime = 0
            }
            if(!isExploding()) {
                newLife()
                if(lives > 0) lives--
            }
        } else if(freeze) {
            return false 
        } else {
            frameIndex = 1
            if(moveLeft) {
                speedX = -300
            } else if (moveRight) {
                speedX = 300
            } else {
                speedX = 0
                return false
            }
        } 
        return super.move(timePassed)
    }
    
    void positionShip() {
        
    }
    
    void newLevel() {
        positionShip()
        startingTime = STARTING_TIME
    }
    
    void newLife() {
        positionShip()
        exploding = false
        frameIndex = 0
        startingTime = STARTING_TIME
    }
    
    boolean isStarting() {
        return startingTime > 0
    }
    
    boolean isExploding() {
        return explodingTime > 0
    }
    
    void explode() {
        explodingTime = EXPLODING_TIME
        frameIndex = 2
        shiphit.play()
        super.explode()
    }
}
