package com.hornmicro


import org.eclipse.swt.SWT
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class Sprite {
    Rectangle[] imageOffsets
    Point offset
    Integer state = 0
    Closure nextState 
}

class SpaceInvaders implements PaintListener {
    Display display
    Image spriteSheet
    List sprites
    Shell shell
    Long lastTick
    Point vaderOffset = new Point(10, 0)
    Boolean vaderForward = true
    
    SpaceInvaders(Display display) {
        this.display = new Display()
        spriteSheet = new Image(display, "SpriteSheet.png")
        def enemyNextState = {
            delegate.state = delegate.state ? 0 : 1
        }
        
        // Load Sprites
        sprites = []
        (0..5).each { row ->
            (0..5).each { col ->
                def item = row * 6 + col
                sprites << new Sprite(
                    imageOffsets:[ new Rectangle(row * 64,0,32,20), new Rectangle(row * 64 + 32,0,32,20) ], 
                    offset: new Point(col * 64, row * 30), 
                    nextState: enemyNextState
                )
            } 
        }
        
    }
    
    void updateModel() {
        /*
            run AI
            move enemies
            resolve collisions
         */
        
        Long diff = System.nanoTime() - lastTick
        if(diff > 100000000  && vaderOffset.y < 115) {
            sprites.each { Sprite sprite ->
                if(sprite.nextState) {
                    sprite.nextState.delegate = sprite
                    sprite.nextState()
                }
            }
            if(vaderForward) {
                
                if(vaderOffset.x > 100) {
                    vaderOffset.y += 15
                    vaderForward = false
                } else {
                    vaderOffset.x += 10
                }   
            } else {
                if(vaderOffset.x < 20) {
                    vaderOffset.y += 15
                    vaderForward = true
                } else {
                    vaderOffset.x -= 10
                }
            }
            lastTick = System.nanoTime()
        }
    }
    
    void paintControl(PaintEvent pe) {
        if(shell && !shell.isDisposed())
            draw(shell)
    }
    
    void draw(Shell shell) {
        if(!shell || shell.isDisposed()) return
        GC gc = new GC(shell)
        gc.setInterpolation(SWT.NONE)
        
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        gc.fillRectangle(shell.clientArea)
        
        sprites.each { Sprite sprite ->
            Rectangle ioff = sprite.imageOffsets[sprite.state]
            Point off = sprite.offset
            gc.drawImage(spriteSheet, 
                ioff.x, ioff.y, ioff.width, ioff.height,
                off.x + vaderOffset.x, off.y + vaderOffset.y, ioff.width, ioff.height)
        }
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
