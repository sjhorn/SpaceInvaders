
package com.hornmicro


import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.events.PaintEvent
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Display
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

class SpaceInvaders implements PaintListener, Listener {
    Display display
    Image spriteSheet
    List sprites = []
    List invaders = []
    Sprite ship
    
    List explosions
    Shell shell
    Long lastTick
    Point vaderOffset = new Point(10, 0)
    Boolean vaderForward = true
    
    SpaceInvaders(Display display) {
        this.display = new Display()
        spriteSheet = new Image(display, "SpriteSheet.png")
        
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
        sprites += invaders
        
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
        
        Long diff = System.nanoTime() - lastTick
        if(diff > 10000000) {
            if(ship.state == 1 && ship.offset.x < 400) {
                ship.offset.x += 2
            } else if (ship.state == 2 && ship.offset.x > 0) {
                ship.offset.x -= 2
            }
        }
        
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
    
    void handleEvent(Event event) {
        if(event.type == SWT.KeyDown) {
            switch(event.keyCode) {
               case 16777220: // Right Key
                   ship.state = 1
                   break
               case 16777219: // Left Key
                   ship.state = 2
                   break
               case 32: // Space Key
                   break;
            }
        } else if (event.type) {
            ship.state = 0
        }
    }
    
    
    
    void draw(Shell shell) {
        if(!shell || shell.isDisposed()) return
        GC gc = new GC(shell)
        gc.setInterpolation(SWT.NONE)
        
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        gc.fillRectangle(shell.clientArea)
        
        
        invaders.each { Sprite sprite ->
            sprite.draw(spriteSheet, gc)
        }
        ship.draw(spriteSheet, gc)
        
        
        gc.dispose()
        display.update()
        
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
        Display.appName = "Space Invaders"
        display.addFilter(SWT.KeyDown, this)
        display.addFilter(SWT.KeyUp, this)

        
        shell = new Shell(display)
        shell.addPaintListener(this)
        shell.setLayout(new GridLayout(1, false))
        

        shell.pack()
        shell.setSize(400,400)
        shell.open()
        lastTick = System.nanoTime()
        while (!shell.isDisposed()) {
            display.readAndDispatch()
            updateModel()
            draw(shell)
            Thread.yield()
        }
        display.dispose()
        
    }
    
    static main(args) {
        new SpaceInvaders().mainLoop()
    }

}
