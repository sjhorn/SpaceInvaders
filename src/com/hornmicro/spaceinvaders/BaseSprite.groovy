package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

class BaseSprite extends Sprite {

    BaseSprite(Rectangle bounds, DoubleRectangle location) {
        super(
            new Rectangle(185,119,31,36),
            bounds, 
            location
        )
    }
    
    void draw(Image spriteSheet, GC gc) {
        if(!hidden) {
            super.draw(spriteSheet, gc)
        }
    }
    
    void explode() {
        // todo damage sprite here
    }
    
}
