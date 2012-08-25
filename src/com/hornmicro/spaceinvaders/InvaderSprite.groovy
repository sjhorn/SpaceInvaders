package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class InvaderSprite extends Sprite {
    static final Sound invaderhit = new Sound("invaderhit.wav")
    InvaderSprite(int row, Rectangle bounds, DoubleRectangle location) {
        super(
            [ 
                new Rectangle(row * 64,0,32,20), new Rectangle(row * 64 + 32,0,32,20), // Left / Right
                new Rectangle(0, 20, 32, 20), new Rectangle(32, 20, 32, 20), // Explosions sequence
                new Rectangle(64, 20, 32, 20), new Rectangle(96, 20, 32, 20)
            ],
            bounds, location, 5d, 0d
        )
    }
    
    void nextState() {
        frameIndex++
        if(frameIndex > 1 && !exploding) {
            frameIndex = 0
        }
    }
    
    void explode() {
        frameIndex = 2
        invaderhit.play()
        super.explode()
    }

}
