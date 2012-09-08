package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class Ship1Sprite extends ShipSprite {
    
    Ship1Sprite(Rectangle bounds) {
        super(
            [
                new Rectangle(192,60,25,20),  // Blank
                new Rectangle(3,60,25,20),    // Normal 
                new Rectangle(34,60,25,20), new Rectangle(66,60,25,20) // Explosion
            ],
            bounds, new DoubleRectangle(bounds.x, bounds.height/2 + 440/2 - 50, 25,20)
        )
        lifeRectangles = [
            new Rectangle(133, 120, 42, 20),
            new Rectangle(88, 120, 42, 20),
            new Rectangle(43, 120, 42, 20),
            new Rectangle(0, 120, 42, 20)
        ]
        
        livesOffset = -62
    }
    
    void positionShip() {
        location.left = bounds.x
    }
}
