package com.hornmicro.spaceinvaders

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

class ShieldSprite extends Sprite {
    List<DoubleRectangle> damage = []
    
    ShieldSprite(Rectangle bounds, DoubleRectangle location) {
        super(
            new Rectangle(185,118,31,36),
            bounds, 
            location
        )
    }
    
    void draw(Image spriteSheet, GC gc) {
        if(!hidden) {
            super.draw(spriteSheet, gc)
            damage.each { DoubleRectangle dr ->
                gc.fillRectangle(dr.asRectangle())
            }
        }
    }
    
    void explode() {
        
    }
    
    boolean collidesWith(Sprite sprite) {
        Rectangle rect 
        if(super.collidesWith(sprite)) {
            DoubleRectangle location = sprite.location
            BulletSprite bs = (BulletSprite) sprite
            boolean invaderBullet = (bs.type == BulletSprite.TYPE.INVADER)
            for(DoubleRectangle dr : damage) {
                if(
                    (invaderBullet && dr.contains(location.left,location.bottom-8)) ||
                    (!invaderBullet && dr.contains(location.left,location.top+8) ) 
                ) {
                    return false
                }
            }
            if(invaderBullet) {
                damage += new DoubleRectangle(location.left-1, location.bottom-15, 5, 30)   
            } else {
                damage += new DoubleRectangle(location.left-1, location.top-15, 5, 30)
            }
            return true
        }
        return false
    }
    
}
