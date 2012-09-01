package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.Rectangle

class EarthSprite extends Sprite {
    
    EarthSprite(Rectangle bounds) {
        super(
            new Rectangle(0, 160, 576, 30),
            bounds,
            new DoubleRectangle(bounds.width/2 - 576/2, bounds.height/2 + 440/2 - 30, 576, 30)
        )
    }    
}
