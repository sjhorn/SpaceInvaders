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
    Integer state
    Closure nextState
    Sprite cloneToOffset(x,y) {
        return new Sprite(imageOffsets:this.imageOffsets, 
            state: 0, 
            nextState: this.nextState,
            offset:new Point(x , y))
    } 
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
        def enemy1 = new Sprite(
            imageOffsets: [new Rectangle(0,0,32,20), new Rectangle(32,0,32,20)],
            offset: new Point(0, 30),
            state: 0,
            nextState: {
                delegate.state = delegate.state ? 0 : 1
            }
        )
        def enemy2 = new Sprite(
            imageOffsets: [new Rectangle(64,0,32,20), new Rectangle(96,0,32,20)],
            offset: new Point(0, 60),
            state: 0,
            nextState: enemy1.nextState
        )
        def enemy3 = new Sprite(
            imageOffsets: [new Rectangle(128,0,32,20), new Rectangle(160,0,32,20)],
            offset: new Point(0, 60),
            state: 0,
            nextState: enemy1.nextState
        )
        def enemy4 = new Sprite(
            imageOffsets: [new Rectangle(192,0,32,20), new Rectangle(224,0,32,20)],
            offset: new Point(0, 60),
            state: 0,
            nextState: enemy1.nextState
        )
        def enemy5 = new Sprite(
            imageOffsets: [new Rectangle(256,0,32,20), new Rectangle(288,0,32,20)],
            offset: new Point(0, 60),
            state: 0,
            nextState: enemy1.nextState
        )
        def enemy6 = new Sprite(
            imageOffsets: [new Rectangle(320,0,32,20), new Rectangle(352,0,32,20)],
            offset: new Point(0, 60),
            state: 0,
            nextState: enemy1.nextState
        )
        def enemy = [enemy1, enemy2, enemy3, enemy4, enemy5, enemy6]
        
        // Load Sprites
        sprites = []
        (1..6).each { i ->
            (0..5).each {
                sprites << enemy[i-1].cloneToOffset(it*64, 30 * i)
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
        if(diff > 1000000000 ) {
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
