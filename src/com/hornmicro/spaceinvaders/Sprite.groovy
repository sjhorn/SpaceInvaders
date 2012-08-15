package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle

/**
 * Sprite - a simple animation capable sprite with rectable hit detection
 * and 2d movement within a container at speedX and speedY
 * 
 * @author Scott Horn
 * 
 */
@CompileStatic
abstract class Sprite {
    List<Rectangle> spriteFrames
    int frameIndex = 0
    DoubleRectangle location
    Rectangle bounds
    double speedX // pixels / sec
    double speedY // pixels / sec
    public boolean exploding = false
    
    public Sprite() {
    }
    
    /**
     * Create a single frame sprite
     * 
     * @param spriteFrame - sprite offset in spritesheet
     * @param bounds - bounds for drawing sprite
     * @param location - initial location 
     * @param speedX - the pixel/sec rate in the x (hortizontal) direction that this sprite moves
     * @param speedY - the pixel/sec rate in the y (vertical) direction that this sprite moves
     */
    public Sprite(Rectangle spriteFrame, Rectangle bounds, DoubleRectangle location, double speedX=0, double speedY=0 ) { 
        this([spriteFrame] as List<Rectangle>, bounds, location, speedX, speedY)
    }
    
    /**
     * Create a multi frame sprite
     * 
     * @param spriteFrames - an array sprite offsets in spritesheet
     * @param bounds - bounds for drawing sprite
     * @param location - initial location 
     * @param speedX - the pixel/sec rate in the x (hortizontal) direction that this sprite moves
     * @param speedY - the pixel/sec rate in the y (vertical) direction that this sprite moves
     */
    public Sprite(List<Rectangle> spriteFrames, Rectangle bounds, DoubleRectangle location, double speedX=0, double speedY=0) {
        this.spriteFrames = spriteFrames
        this.bounds = bounds
        this.location = location
        this.speedX = speedX
        this.speedY = speedY
    }
    
    
    /**
     * Draw the sprite onto the graphics context
     * 
     * @param spriteSheet - the sprite sheet for the relative frame coords
     * @param gc - the graphic context to draw to
     */
    void draw(Image spriteSheet, GC gc) {
        if(gc.isDisposed()) return
        Rectangle frame = spriteFrames[frameIndex]
        if(frame) {
            gc.drawImage(spriteSheet,
                frame.x, frame.y, frame.width, frame.height,
                location.left as int, location.top as int, frame.width, frame.height)
        }
    }
    
    /**
     * Move the sprite to its next location
     * 
     * @param timePassed - the time passed in nanoseconds since the last movement
     * @return - return true if redraw is required
     */
    boolean move(long timePassed) {
        location.left += (speedX ? ((timePassed * speedX) / 1_000_000_000) : 0d) 
        int maxX = bounds.x + bounds.width
        if(location.right > maxX) {
            location.left = maxX - location.width
        } else if (location.left < bounds.x) {
            location.left = bounds.x
        }
        
        location.top += (speedY ? ((timePassed * speedY) / 1_000_000_000) : 0d)
        int maxY = bounds.y + bounds.height
        if(location.bottom > maxY) {
            location.top = maxY - location.height   
        } else if (location.top < bounds.y) {
            location.top = bounds.y
        }
        return true
    }
    
    /**
     * Detect a collision between this and another sprite
     * 
     * @param sprite - the sprite to detect collision
     * @return - return true if the sprite collides with this sprite
     */
    boolean collidesWith(Sprite sprite) {
        DoubleRectangle rect = sprite.location
        if (location.left < rect.right && rect.left < location.right && location.top < rect.bottom && rect.top < location.bottom) {
            return true
        }
        return false
    }
    
    /**
     *
     * Simple logic to update the internal state/model of the sprite
     * at regular intervals
     */
    abstract void nextState()
    
    void explode() {
        exploding = true
    }
}
