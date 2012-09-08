package com.hornmicro.spaceinvaders

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

class ShieldSprite extends Sprite {
    static final int BOTTOM = 0x01
    static final int MIDDLE = 0x02
    static final int TOP = 0x04
    static final int ALL = 0x07
    
    List<Rectangle> damageRects = []
    Map damage = [:]
    
    ShieldSprite(Rectangle bounds, DoubleRectangle location) {
        super(
            new Rectangle(185,118,31,36), 
            bounds, 
            location
        )
        
        // Add gaps in shield
        int left = location.left as int
        int right = location.right as int
        (0..4).each {
            damage[left + it] = TOP
            damage[right - it] = TOP
        }
    }
    
    void draw(Image spriteSheet, GC gc) {
        if(!hidden) {
            super.draw(spriteSheet, gc)
            damageRects.each { Rectangle dr ->
                gc.fillRectangle(dr)
            }
        }
    }
    
    void explode() {
        
    }
    
    boolean collidesWith(Sprite sprite) {
        if(super.collidesWith(sprite)) {
            int left = sprite.location.left as int
            int columnDamage = damage[left] ?: 0
            
            // If this column is fully damaged then let bullet though
            if(columnDamage == ALL ) {
                return false
            }
            
            // Add damage depending on whether we are coming from top or bottom
            if(sprite.speedY > 0) {
                 if(!(columnDamage & TOP)) {
                     addDamage(left, TOP)
                 } else if(!(columnDamage & MIDDLE)) {
                     addDamage(left, MIDDLE)
                 } else {
                     addDamage(left, BOTTOM)
                 }
            } else {
                if(!(columnDamage & BOTTOM)) {
                    addDamage(left, BOTTOM)
                } else if(!(columnDamage & MIDDLE)) {
                    addDamage(left, MIDDLE)
                } else {
                    addDamage(left, TOP)
                }
            }
            return true
        }
        return false
    }
    
    void addDamage(int left, int level) {
        Rectangle locationRect = location.asRectangle()
        int top = location.top + (12 * (level == TOP ? 0 : level == MIDDLE ? 1 : 2)) as int 
        
        damageRects.add(new Rectangle(left, top, 3, 16))
        
        Random random = new Random()
        switch( random.nextInt(5) ) {
             case 0:
                 damageRects.add(new Rectangle(left-3, top+9, 3, 3))
                 break
             case 1:
                 damageRects.add(new Rectangle(left+3, top+9, 3, 3))
                 break
             case 2:
                 damageRects.add(new Rectangle(left-3, top+1, 3, 3))
                 break
             case 3:
                 damageRects.add(new Rectangle(left+3, top+1, 3, 3))
                 break
             default:
                 // do nothing
                 break
        }
        damage[left] = (damage[left] ?: 0) | level
    }
}
