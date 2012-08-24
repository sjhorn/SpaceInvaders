package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.Rectangle

class BaseSprite extends Sprite {

    BaseSprite(Rectangle bounds, DoubleRectangle location) {
        super(
            new Rectangle(185,119,31,36),
            bounds, 
            location
        )
    }
    
    void explode() {
        // ignore for now
    }
}
