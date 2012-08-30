package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle

class BulletSprite extends Sprite {
    enum TYPE { SHIP1, SHIP2,  INVADER }
    static public List<BulletSprite> bullets = []
    static public List<BulletSprite> bulletsToRemove = []
    static final Sound shipfire = new Sound("shipfire.wav")
    public TYPE type
    
    BulletSprite(Rectangle bounds, DoubleRectangle location, TYPE type) {
        super(new Rectangle(96, 39, 3, 16), bounds, location)
        this.type = type
        this.speedY = (type == TYPE.SHIP1 || type == TYPE.SHIP2) ? -200d : 200d
    }
    
    static void fireFromInvader(Sprite invader) {
        DoubleRectangle invaderLoc = invader.location
        DoubleRectangle location = new DoubleRectangle(
            invaderLoc.left + invaderLoc.width / 2 - 1.5d,
            invaderLoc.bottom,
            3d,
            16d
        )
        Rectangle bounds = new Rectangle(invader.bounds.x, invader.bounds.y-16, invader.bounds.width, invader.bounds.height+32)
        bullets.add(new BulletSprite(bounds, location, TYPE.INVADER))
    }
    
    static void fireFromShip(ShipSprite ship) {
        TYPE type = (ship instanceof Ship1Sprite ? TYPE.SHIP1 : TYPE.SHIP2)
        
        // Only create a new bullet if the last one is gone!
        if(!ship.exploding && ! ship.starting && ! bullets.type.find { it == type } ) {
            DoubleRectangle shipLoc = ship.location
            DoubleRectangle location = new DoubleRectangle(
                shipLoc.left + shipLoc.width / 2 - 1.5d,
                shipLoc.top - 16d,
                3d,
                16d
            )
            Rectangle bounds = new Rectangle(ship.bounds.x, ship.bounds.y-16, ship.bounds.width, ship.bounds.height+32)
            bullets.add(new BulletSprite(bounds, location, type))
            shipfire.play()
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
        for(Sprite sprite: sprites.flatten()) {
            for(BulletSprite bullet: bullets) {
                if(sprite instanceof InvaderSprite && bullet.type == TYPE.INVADER) {
                    continue
                }
                if(!sprite.hidden && !sprite.exploding && sprite.collidesWith(bullet)) {
                    sprite.hitBy = bullet
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
    
    void explode() {
        BulletSprite.bullets.remove(this)
    }
}
