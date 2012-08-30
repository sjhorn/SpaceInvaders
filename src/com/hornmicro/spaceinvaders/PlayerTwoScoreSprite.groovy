package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

@CompileStatic 
class PlayerTwoScoreSprite extends Sprite {
    int score 
    
    PlayerTwoScoreSprite(Rectangle bounds) {
        super(
            [ 
                new Rectangle(0, 100, 42, 20), // 0
                new Rectangle(44, 100, 42, 20),
                new Rectangle(88, 100, 42, 20),
                new Rectangle(132, 100, 42, 20),
                new Rectangle(176, 100, 42, 20),
                new Rectangle(220, 100, 42, 20), // 5
                new Rectangle(264, 100, 42, 20),
                new Rectangle(308, 100, 42, 20),
                new Rectangle(352, 100, 42, 20),
                new Rectangle(396, 100, 42, 20),
                new Rectangle(440, 100, 42, 20) // 9
            ],
            bounds,
            new DoubleRectangle(15, 20, 240, 20)
        )
    }
    
    void draw(Image spriteSheet, GC gc) {
        frameIndex = 0
        String scoreString = String.format('%04d',score)
        
        for(int index  : (0..3)) {
            location.left = 515 - index * 55
            frameIndex = scoreString[3-index] as int
            super.draw(spriteSheet, gc)
        }
    }

}
