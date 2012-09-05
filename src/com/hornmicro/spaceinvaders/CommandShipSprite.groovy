package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.Rectangle;

class CommandShipSprite extends ScoringSprite {
    static final long EXPLODING_TIME = 1_200_000_000
    static final Sound commandshiphit = new Sound("invaderhit.wav")
    static final Sound commandshipmove = new Sound("commandship.wav")
    
    
    long soundTime = 0
    long explosionFrameTime = 0
    
    CommandShipSprite(Rectangle bounds) {
        this.spriteFrames = [ 
            new Rectangle(0, 40, 30, 20), 
            new Rectangle(30, 40, 30, 20), new Rectangle(60, 40, 30, 20), // Explosions sequence
            new Rectangle(30, 40, 30, 20), new Rectangle(60, 40, 30, 20)
        ]
        Random r = new Random()
        int choice = r.nextInt(2)
        
        this.bounds = new Rectangle(bounds.x - 20, bounds.y, bounds.width + 40, bounds.height) 
        this.location = new DoubleRectangle(bounds.x, bounds.y, 30, 20)
        this.location.left = choice ? bounds.x - 20 : bounds.x + bounds.width
        this.speedX = choice ? 70d : -70d
        this.score = 200
    }
    
    boolean move(long timePassed) {
        if(isHidden()) {
            return
        }
        if(exploding) {
            speedX = 0
            explosionFrameTime += timePassed
            if(explosionFrameTime > 120_000_000) {
                nextState()
                explosionFrameTime = 0
            }
            
        } else if(location.left < (bounds.x + bounds.width) && location.left > (bounds.x - location.width) ) {
            soundTime += timePassed
            if(soundTime > 250_000_000) {
                commandshipmove.play()
                soundTime = 0
            }
            
            return super.move(timePassed, false)
        } else {
            hide()
        }
        return false
    }
    
    void nextState() {
        if(frameIndex >= spriteFrames.size()) {
            hide()
        } else if(exploding) {
            frameIndex++
        } else {
            frameIndex = 0
        }
    }
    
    void explode() {
        frameIndex = 1
        exploding = true
        commandshiphit.play()
    }
}
