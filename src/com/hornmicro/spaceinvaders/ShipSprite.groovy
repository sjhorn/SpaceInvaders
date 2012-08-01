package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class ShipSprite extends Sprite {
    
    ShipSprite(Rectangle spriteFrame, Rectangle bounds, Rectangle location) {
        super(spriteFrame, bounds, location)
    }
    
    void nextState() {
        // Add explosion here later :)
    }
}
