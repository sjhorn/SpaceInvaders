package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class ShipSprite extends Sprite {
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
    
    
    ShipSprite(Rectangle bounds) {
        super(
            [
                new Rectangle(128,60,25,20),  // Blank
                new Rectangle(3,60,25,20),    // Normal 
                new Rectangle(34,60,25,20), new Rectangle(66,60,25,20) // Explosion
            ],
            bounds, new DoubleRectangle(bounds.x,bounds.height-50,25,20)
        )
        lifeRectangles = [
            new Rectangle(133, 120, 42, 20),
            new Rectangle(88, 120, 42, 20),
            new Rectangle(43, 120, 42, 20),
            new Rectangle(0, 120, 42, 20)
        ]
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
        super.draw(spriteSheet, gc)
        
        if(isStarting()) {
            Rectangle l = lifeRectangles[lives]
            gc.drawImage(spriteSheet,
                l.x, l.y, l.width, l.height,
                (bounds.x+ bounds.width/2 + 20) as int, location.top as int, 42, 20)
        }
    }
    
    boolean move(long timePassed) {
        
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
    
    void newLife() {
        location.left = bounds.x
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
