package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

class InvaderGroup {
    DoubleRectangle location
    Rectangle bounds
    long cumulativeTime = 0
    long dx = 5
    long dy = 0
    
    List<InvaderSprite> invaders = []
    
    InvaderGroup(Rectangle bounds) {
        this.bounds = bounds
        (0..5).each { int row ->
            (0..5).each { int col ->
                int item = row * 6 + col
                invaders.add( new InvaderSprite(row, bounds, new Rectangle(col * 64, row * 30, 32, 20)) )
            }
        }
        DoubleRectangle firstInvader = invaders[0].location
        DoubleRectangle lastInvader = invaders[-1].location
        location = new DoubleRectangle(firstInvader.left, firstInvader.top, 
            lastInvader.right - firstInvader.left, 
            lastInvader.bottom - firstInvader.top)
    }
    
    boolean move(long timePassed) {
        cumulativeTime += timePassed
        if(cumulativeTime > 100000000 && location.bottom < 200) {
            location.left += dx
            dy = 0
            if(location.right >= bounds.x + bounds.width) {
                dy = 10d
                dx = -5d
            } else if(location.left <= bounds.x) {
                dy = 10d
                dx = 5d
            }
            double left = Double.MAX_VALUE, top = Double.MAX_VALUE, right = 0, bottom = 0
            invaders.each { InvaderSprite sprite ->
                DoubleRectangle loc = sprite.location
                
                // Adjust the sprite
                loc.left += dx
                loc.top += dy
                
                // Adjust the formations bounds
                left = left < loc.left ? left : loc.left 
                top = top < loc.top ? top : loc.top 
                right = right > loc.right ? right : loc.right 
                bottom = bottom > loc.bottom ? bottom : loc.bottom
                
                sprite.nextState()
            }
            location.left = left
            location.top = top
            location.right = right
            location.bottom = bottom
            
            cumulativeTime = 0
            return true
        }
        return false
    }
    
    void draw(Image spriteSheet, GC gc) {
        invaders.each { InvaderSprite sprite ->
            sprite.draw(spriteSheet, gc)
        }
    }
}
