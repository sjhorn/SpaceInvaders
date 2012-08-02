package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.events.PaintEvent
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Shell


@CompileStatic
class SpaceInvaders implements PaintListener, Listener {
    private Canvas canvas
    Display display
    Shell shell
    Image spriteSheet
    ShipSprite shipSprite
    InvaderGroup invaderGroup
    boolean redraw = false
    
    long lastTime = 0L
    long lastTick, shipTick
    
    SpaceInvaders() {
        Display.setAppName("Space Invaders")
        display = new Display()
        configureShell()
        
        spriteSheet = new Image(display, "gfx/SpriteSheet.png")
        Rectangle bounds = shell.getBounds()
        
        // Add space ship
        shipSprite = new ShipSprite(new Rectangle(3,60,25,20), bounds, new Rectangle(10,300,25,20))
        
        // Add space invaders
        invaderGroup = new InvaderGroup(bounds)
    }
    
    void handleEvent(Event event) {
        if(event.type == SWT.KeyDown) {
            switch(event.keyCode) {
               case SWT.ARROW_RIGHT:
                   shipSprite.moveRight = true
                   break
               case SWT.ARROW_LEFT:
                   shipSprite.moveLeft = true
                   break
               case SWT.SPACE:
                   break
            }
        } else if (event.type == SWT.KeyUp) {
            switch(event.keyCode) {
                case SWT.ARROW_RIGHT:
                    shipSprite.moveRight = false
                    break
                case SWT.ARROW_LEFT:
                    shipSprite.moveLeft = false
                    break
             }
        }
    }
    
    void configureShell() {
        display.addFilter(SWT.KeyDown, this)
        display.addFilter(SWT.KeyUp, this)
        
        shell = new Shell(display)
        shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        shell.setImage(new Image(Display.getDefault(), "gfx/SpaceInvaders.png"))
        
        canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED)
        canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        canvas.addPaintListener(this)
        
        shell.setLayout(new FillLayout())
        shell.layout()
        shell.setSize(450,400)
    }
    
    void paintControl(PaintEvent pe) {
        if(shell && !shell.isDisposed())
            draw()
    }
    
    void updateModel() {
        if(lastTime) {
            long timePassed = System.nanoTime() - lastTime
            redraw = invaderGroup.move(timePassed) 
            redraw = shipSprite.move(timePassed) || redraw
        }
        lastTime = System.nanoTime()
    }
    
    void draw() {
        if(!shell || shell.isDisposed()) return
        GC gc = new GC(canvas)
        gc.setInterpolation(SWT.NONE)
        
        gc.fillRectangle(canvas.getClientArea())
        
        invaderGroup.draw(spriteSheet, gc)
        shipSprite.draw(spriteSheet, gc)
        
        gc.dispose()
    }
    
    void playSounds() {
        
    }
    
    void gameLoop() {
        /*
         while( user doesn't exit )
             check for user input
             update model
             draw graphics
             play sounds
         end while
         */
         
         lastTick = shipTick = System.nanoTime()
         
         shell.open()
         while (!shell.isDisposed()) {
             long startTime = System.nanoTime()
             display.readAndDispatch()
             updateModel()
             draw()
             playSounds()
             
             // Aim for 100fps (1s/100 frames = 10,000,000 ns / frame)
             TimeUnit.NANOSECONDS.sleep(startTime + 20_000_000 - System.nanoTime())
         }
         display.dispose()
    }
    
    static main(args) {
        try {
            new SpaceInvaders().gameLoop()
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        }
    }

}
