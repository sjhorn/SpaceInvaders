package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class PlayerOneScoreSprite extends Sprite {
    PlayerOneScoreSprite(Rectangle bounds) {
        super(
            [ 
                new Rectangle(0, 80, 42, 20), // 0
                new Rectangle(44, 80, 42, 20),
                new Rectangle(88, 80, 42, 20),
                new Rectangle(132, 80, 42, 20),
                new Rectangle(176, 80, 42, 20),
                new Rectangle(220, 80, 42, 20), // 5
                new Rectangle(264, 80, 42, 20),
                new Rectangle(308, 80, 42, 20),
                new Rectangle(352, 80, 42, 20),
                new Rectangle(396, 80, 42, 20),
                new Rectangle(440, 80, 42, 20) // 10
            ],
            bounds,
            new DoubleRectangle(15, 20, 240, 20)
        )
    }
    
    void draw(Image spriteSheet, GC gc) {
        frameIndex = 0
        for(int index  : (0..3)) {
            location.left = 15 + index * 55
            super.draw(spriteSheet, gc)
        }
    }

}
