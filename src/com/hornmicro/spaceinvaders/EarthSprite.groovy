package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.Rectangle

class EarthSprite extends Sprite {
    
    EarthSprite(Rectangle bounds) {
        super(
            new Rectangle(0, 160, 576, 30),
            bounds,
            new DoubleRectangle(0, bounds.height-30, 576, 30)
        )
    }    
}
