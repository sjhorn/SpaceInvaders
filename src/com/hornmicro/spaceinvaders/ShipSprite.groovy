package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class ShipSprite extends Sprite {
    public boolean moveLeft = false
    public boolean moveRight = false
    
    ShipSprite(Rectangle spriteFrame, Rectangle bounds, Rectangle location) {
        super(spriteFrame, bounds, location)
    }
    
    void nextState() {
        // Add explosion here later :)
    }
    
    boolean move(long timePassed) {
        if(moveLeft) {
            speedX = -300
        } else if (moveRight) {
            speedX = 300
        } else {
            speedX = 0
            return false
        }
        return super.move(timePassed)
    }
}
