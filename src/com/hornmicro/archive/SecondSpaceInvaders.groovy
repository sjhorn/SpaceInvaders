
package com.hornmicro.archive


import java.util.concurrent.TimeUnit

import org.eclipse.swt.SWT
import org.eclipse.swt.events.PaintEvent
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Shell

class Sprite {
    Rectangle[] imageOffsets
    Point offset
    Integer state = 0
    Closure nextState 
    
    void draw(Image sheet,GC gc) {
        Rectangle ioff = imageOffsets[state < imageOffsets.size() ? state : 0]
        gc.drawImage(sheet,
            ioff.x, ioff.y, ioff.width, ioff.height,
            offset.x, offset.y, ioff.width, ioff.height)
    }
}

class SecondSpaceInvaders implements PaintListener, Listener {
    Display display
    Image spriteSheet
    List sprites = []
    List invaders = []
    Sprite ship
    
    List explosions
    Shell shell
    Long lastTick, shipTick
    Point vaderOffset = new Point(10, 0)
    Boolean vaderForward = true
    
    SecondSpaceInvaders(Display display) {
        Display.appName = "Space Invaders"
        this.display = new Display()
        spriteSheet = new Image(display, "gfx/SpriteSheet.png")
        
        // Load Ship
        ship = new Sprite(
            imageOffsets:[new Rectangle(3, 60, 25, 20)],
            offset: new Point(10, 300)
        ) // 205, 448
        sprites << ship
        
        // Load Invader Sprites
        def enemyNextState = {
            delegate.state = delegate.state ? 0 : 1
        }
        (0..5).each { row ->
            (0..5).each { col ->
                def item = row * 6 + col
                invaders << new Sprite(
                    imageOffsets:[ new Rectangle(row * 64,0,32,20), new Rectangle(row * 64 + 32,0,32,20) ], 
                    offset: new Point(col * 64, row * 30), 
                    nextState: enemyNextState
                )
            } 
        }
        sprites.addAll(invaders)
        
        // Load explosions
        /*
        def explosionNextState = { -> 
            delegate.state += 1
            if(delegate.state == delegate.imageOffsets.size()) {
                delegate.state = 0   
            }
        }
        sprites += [ 
            new Sprite(
                imageOffsets: [
                    new Rectangle( 0, 20, 32, 20),
                    new Rectangle( 32, 20, 32, 20),
                    new Rectangle( 64, 20, 32, 20),
                    new Rectangle( 96, 20, 32, 20)
                ],
                offset: new Point(0,200),
                nextState: explosionNextState
            ),
            new Sprite(
                imageOffsets: [
                   //new Rectangle( 0, 40, 32, 20),
                   new Rectangle( 32, 40, 32, 20),
                   new Rectangle( 64, 40, 32, 20)
                ],
               offset: new Point(0,230),
               nextState: explosionNextState
            ),
            new Sprite(
                imageOffsets: [
                   //new Rectangle( 0, 60, 32, 20),
                   new Rectangle( 32, 60, 32, 20),
                   new Rectangle( 64, 60, 32, 20)
                ],
                offset: new Point(0,260),
                nextState: explosionNextState
            )
        ]
        */
        
        
    }
    
    void updateModel() {
        /*
            run AI
            move enemies
            resolve collisions
         */
        
        // Update Ship position ~ 10ms
        Long shipDiff = System.nanoTime() - shipTick
        if(shipDiff > 15000000) {
            if(ship.state == 1 && ship.offset.x < 400) {
                ship.offset.x += 5
            } else if (ship.state == 2 && ship.offset.x > 0) {
                ship.offset.x -= 5
            }
            shipTick = System.nanoTime()
        }
        
        // Update invader position ~ 500ms
        Long diff = System.nanoTime() - lastTick
        if(diff > 500000000  && vaderOffset.y < 115) {
            sprites.each { Sprite sprite ->
                if(sprite.nextState) {
                    sprite.nextState.delegate = sprite
                    sprite.nextState()
                }
            }
            def dx = 0, dy = 0
            if(vaderForward) {
                if(vaderOffset.x > 100) {
                    vaderOffset.y += 15
                    vaderForward = false
                    dy = 15
                } else {
                    vaderOffset.x += 10
                    dx = 10
                }   
            } else {
                if(vaderOffset.x < 20) {
                    vaderOffset.y += 15
                    dy = 15
                    vaderForward = true
                } else {
                    vaderOffset.x -= 10
                    dx = -10
                }
            }
            invaders.each { Sprite sprite ->
                sprite.offset.x += dx
                sprite.offset.y += dy
             }
            lastTick = System.nanoTime()
        }
    }
    
    void paintControl(PaintEvent pe) {
        if(shell && !shell.isDisposed())
            draw(shell)
    }
    
    def rightArrowDown = false
    def leftArrowDown = false
    void handleEvent(Event event) {
        if(event.type == SWT.KeyDown) {
            switch(event.keyCode) {
               case SWT.ARROW_RIGHT: // Right Key
                   ship.state = 1
                   rightArrowDown = true
                   break
               case SWT.ARROW_LEFT: // Left Key
                   ship.state = 2
                   leftArrowDown = true
                   break
               case SWT.SPACE: // Space Key
                   break;
            }
        } 
        else if (event.type == SWT.KeyUp) {
            switch(event.keyCode) {
                case SWT.ARROW_RIGHT: // Right Key
                    rightArrowDown = false
                    break
                case SWT.ARROW_LEFT: // Left Key
                    leftArrowDown = false
                    break
             }
            ship.state = rightArrowDown ? 1 : leftArrowDown ? 2 : 0
        } 
    }
    
    
    
    void draw(Composite comp) {
        if(!comp || comp.isDisposed()) return
        GC gc = new GC(comp)
        gc.setInterpolation(SWT.NONE)
        
        gc.fillRectangle(comp.clientArea)
        
        invaders.each { Sprite sprite ->
            sprite.draw(spriteSheet, gc)
        }
        ship.draw(spriteSheet, gc)
        
        gc.dispose()
    }
    
    void mainLoop() {
        /*
        while( user doesn't exit )
            check for user input
            update model
            draw graphics
            play sounds
        end while
        */
        
        
        
        display.addFilter(SWT.KeyDown, this)
        display.addFilter(SWT.KeyUp, this)

        
        shell = new Shell(display)
        shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        shell.setImage(new Image(Display.default, "gfx/SpaceInvaders.png"))

        Canvas canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED)        
        canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        canvas.addPaintListener(this)
        shell.setLayout(new FillLayout())
        

        shell.layout()
        shell.setSize(450,400)
        shell.open()
        lastTick = shipTick = System.nanoTime()
        while (!shell.isDisposed()) {
            long startTime = System.nanoTime()
            display.readAndDispatch()
            updateModel()
            
            draw(canvas)
            
            // Aim for 100fps (1s/100frames = 10000000ns / frame)
            TimeUnit.NANOSECONDS.sleep(startTime + 10000000 - System.nanoTime())
        }
        display.dispose()
        
    }
    
    static main(args) {
        new SecondSpaceInvaders().mainLoop()
    }

}
