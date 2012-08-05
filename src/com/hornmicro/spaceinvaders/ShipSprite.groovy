package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class ShipSprite extends Sprite {
    public boolean moveLeft = false
    public boolean moveRight = false
    
    ShipSprite(Rectangle bounds) {
        super(new Rectangle(3,60,25,20), bounds, new Rectangle(10,300,25,20))
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
