package com.hornmicro.spaceinvaders

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

class ScoringSprite extends Sprite {
    int score
    
    ScoringSprite() {
        
    }
    
    ScoringSprite(List<Rectangle> spriteFrames, Rectangle bounds, DoubleRectangle location, double speedX=0, double speedY=0) {
        this.spriteFrames = spriteFrames
        this.bounds = bounds
        this.location = location
        this.speedX = speedX
        this.speedY = speedY
    }
}
