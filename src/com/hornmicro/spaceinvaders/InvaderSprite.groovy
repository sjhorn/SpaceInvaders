package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class InvaderSprite extends Sprite {
    
    InvaderSprite(int row, Rectangle bounds, Rectangle location) {
        super(
            [ new Rectangle(row * 64,0,32,20), new Rectangle(row * 64 + 32,0,32,20) ],
            bounds, location, 5d, 0d
        )
    }
    
    void nextState() {
        frameIndex++
        if(frameIndex > 1) {
            frameIndex = 0
        }
    }


}
