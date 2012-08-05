package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

class BulletSprite extends Sprite {
    enum Type { SHIP, INVADER }
    static public List<BulletSprite> bullets = []
    static public List<BulletSprite> bulletsToRemove = []
    public Type type
    
    BulletSprite(Rectangle bounds, DoubleRectangle location, Type type) {
        super(new Rectangle(96, 60, 3, 16), bounds, location.getRectangle())
        this.type = type
        this.speedY = type == Type.SHIP ? -200d : 200d
    }
    
    
    static void fireFromShip(ShipSprite ship) {
        
        // Only create a new bullet if the last one is gone!
        if(! bullets.type.find { it == Type.SHIP } ) {
            DoubleRectangle shipLoc = ship.location
            DoubleRectangle location = new DoubleRectangle(
                shipLoc.left + shipLoc.width / 2 - 1.5d,
                shipLoc.top - 16d,
                3d,
                16d
            )
            Rectangle bounds = new Rectangle(ship.bounds.x, ship.bounds.y-16, ship.bounds.width, ship.bounds.height+32)
            bullets.add(new BulletSprite(bounds, location, Type.SHIP))
        }    
    }
    
    static boolean moveAll(long timeElapsed) {
        boolean redraw = false
        for(BulletSprite bullet: bullets) {
            if(bullet.move(timeElapsed)) {
                redraw = true
            }
        }
        for(BulletSprite bullet: bulletsToRemove) {
            bullets.remove(bullet)
        }
        bulletsToRemove.clear()
        return redraw
    }
    
    static void drawAll(Image spriteSheet, GC gc) {
        for(BulletSprite bullet: bullets) {
            bullet.draw(spriteSheet, gc)
        }
    }
    
    static List<Sprite> detectCollisions(List sprites) {
        List<Sprite> collisions = []
        sprites.each { Sprite sprite ->
            bullets.each { Sprite bullet ->
                if(!sprite.exploding && sprite.collidesWith(bullet)) {
                    collisions.add(sprite)
                    collisions.add(bullet)
                }
            }
        }
        return collisions
    }
    
    
    void nextState() {
        
    }

    boolean move(long timePassed) {
        boolean result = super.move(timePassed)
        if(location.outside(bounds)) {
            bulletsToRemove.add(this)
        }
        return result
    }
    
    
}
