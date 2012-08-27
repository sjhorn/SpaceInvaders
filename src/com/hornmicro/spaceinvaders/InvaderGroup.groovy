package com.hornmicro.spaceinvaders

import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

class InvaderGroup {
    long waitTime = 700_000_000
    DoubleRectangle location
    Rectangle bounds
    long moveTime = 0
    long explosionTime = 0
    long fireTime = 0
    long dx = 5
    long dy = 0
    
    Random r = new Random()
    List<InvaderSprite> invaders = []
    List<InvaderSprite> invadersToRemove = []
    Map invaderColumn = [:]
    static final Sound invaderSound = new Sound("invaders.wav")
    
    
    InvaderGroup(Rectangle bounds) {
        this.bounds = bounds
        (0..5).each { int row ->
            (0..5).each { int col ->
                int item = row * 6 + col
                InvaderSprite sprite = new InvaderSprite(row, bounds, new DoubleRectangle(bounds.x + col * 58, bounds.y + row * 35, 32, 20))
                invaders.add(sprite)
                if(row == 0) invaderColumn[col] = []
                invaderColumn[col].add(sprite)
            }
        }
        DoubleRectangle firstInvader = invaders[0].location
        DoubleRectangle lastInvader = invaders[-1].location
        location = new DoubleRectangle(firstInvader.left, firstInvader.top, 
            lastInvader.right - firstInvader.left, 
            lastInvader.bottom - firstInvader.top)
    }
    
    boolean move(long timePassed, boolean freeze) {
        explosionTime += timePassed
        
        if(explosionTime > 120_000_000) {
            invaders.each { InvaderSprite sprite ->
                if(sprite.exploding) {
                    sprite.nextState()
                
                    if(sprite.frameIndex >= sprite.spriteFrames.size()) {
                        waitTime -= 17_000_000
                        invadersToRemove.add(sprite)
                        invaderColumn.each { k, v ->
                            v.remove(sprite)
                        }
                    }
                }
            }
            invaders.removeAll(invadersToRemove)
            invadersToRemove.clear()
            if(invaders.size() == 0) {
                invaderSound.stop()
            }
            explosionTime = 0
        }
        if(!freeze) {
            moveTime += timePassed
            fireTime += timePassed
    
            if(fireTime > 800_000_000) {
                int col = r.nextInt(6) 
                if(invaderColumn[col].size()) {
                   Sprite invader = invaderColumn[col][-1]
                   BulletSprite.fireFromInvader(invader) 
                }
                fireTime = 0
            }
            
            
            if(moveTime > waitTime) {
                invaderSound.play()
                location.left += dx
                dy = 0
                if(location.right >= (bounds.x + bounds.width)) {
                    dy = 22d
                    dx = -5d
                } else if(location.left <= bounds.x) {
                    dy = 22d
                    dx = 5d
                }
                double left = Double.MAX_VALUE, top = Double.MAX_VALUE, right = 0, bottom = 0
                invaders.each { InvaderSprite sprite ->
                    DoubleRectangle loc = sprite.location
                    
                    // Adjust the sprite
                    loc.left += dx
                    loc.top += dy
                    
                    // Adjust the formations bounds
                    left = loc.left < left ? loc.left : left  
                    top = loc.top < top ? loc.top : top  
                    right = loc.right > right ? loc.right : right  
                    bottom = loc.bottom > bottom ? loc.bottom : bottom 
                    
                    if(!sprite.exploding) {
                        sprite.nextState()
                    }
                }
                location.left = left
                location.top = top
                location.width = right - left
                location.height = bottom - top
                moveTime = 0
            }
        }
        return true
    }
    
    void draw(Image spriteSheet, GC gc) {
//        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED))
//        gc.drawRectangle(bounds)
//        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE))
//        gc.drawRectangle(location.getRectangle())
        invaders.each { InvaderSprite sprite ->
            sprite.draw(spriteSheet, gc)
        }
    }
}
