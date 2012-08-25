package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent
import org.eclipse.swt.events.DisposeListener
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
class SpaceInvaders implements PaintListener, DisposeListener, Listener {
    Canvas canvas
    Display display
    Shell shell
    Image spriteSheet
    BaseSprite baseSprite1
    BaseSprite baseSprite2
    BaseSprite baseSprite3
    ShipSprite shipSprite
    EarthSprite earthSprite
    PlayerOneScoreSprite playerOneScoreSprite
    PlayerTwoScoreSprite playerTwoScoreSprite
    
    InvaderGroup invaderGroup
    Rectangle bounds
    boolean redraw = false
    long newLifeTime = 0
    long lastTime = 0L
    
    SpaceInvaders() {
        Display.setAppName("Space Invaders")
        display = new Display()
        configureShell()
        
        InputStream imageInputStream = getClass().getResourceAsStream("SpriteSheet.png")
        spriteSheet = (imageInputStream != null ? new Image(display, imageInputStream) : new Image(display, "gfx/SpriteSheet.png"))  
        bounds = shell.getClientArea()
        
        // Scores
        playerOneScoreSprite = new PlayerOneScoreSprite(bounds)
        playerTwoScoreSprite = new PlayerTwoScoreSprite(bounds)
        
        // Add Earth
        earthSprite = new EarthSprite(bounds)
        
        // Add bases
        baseSprite1 = new BaseSprite(bounds, new DoubleRectangle(bounds.width / 2 - 130,bounds.height - 110,31,36))
        baseSprite2 = new BaseSprite(bounds, new DoubleRectangle(bounds.width / 2 - 15,bounds.height - 110,31,36))
        baseSprite3 = new BaseSprite(bounds, new DoubleRectangle(bounds.width / 2 + 100,bounds.height - 110,31,36))
        
        // Add space ship
        Rectangle shipBounds = new Rectangle(bounds.width / 2 - 156, 0, 312, bounds.height)
        shipSprite = new ShipSprite(shipBounds)
        
        // Add space invaders
        Rectangle invaderBounds = new Rectangle(bounds.width / 2 - 210, 60, 420, bounds.height - 100)
        invaderGroup = new InvaderGroup(invaderBounds)
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
               case 0x20:
                   BulletSprite.fireFromShip(shipSprite)
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
        
        InputStream shellImageIS = getClass().getResourceAsStream("SpaceInvaders.png")
        shell.setImage(shellImageIS ? new Image(display, shellImageIS) : new Image(display, "gfx/SpaceInvaders.png"))
        
        canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED)
        canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK))
        canvas.addPaintListener(this)
        
        shell.addDisposeListener(this)
        shell.setLayout(new FillLayout())
        shell.layout()
        shell.setSize(576,440)
    }
    
    void paintControl(PaintEvent pe) {
        if(shell && !shell.isDisposed())
            draw()
    }
    
    void updateModel() {
        if(lastTime) {
            long timePassed = System.nanoTime() - lastTime
                
            invaderGroup.move(timePassed, shipSprite.isStarting() || shipSprite.isExploding())
            shipSprite.move(timePassed)
            BulletSprite.moveAll(timePassed)
            
            redraw = true

            // Detect collisions
            def time = System.nanoTime()
            
            List sprites = [ shipSprite, invaderGroup.invaders, baseSprite1, baseSprite2, baseSprite3 ]
            for(Sprite sprite: BulletSprite.detectCollisions(sprites)) {
                sprite.explode()
            }
            
            if(BulletSprite.bullets.size()) {
                long test = TimeUnit.MILLISECONDS.convert(System.nanoTime() - time, TimeUnit.NANOSECONDS)
                if(test > 10 )
                    println( "${test.toString()} ms" )
            }
            
        }
        
        lastTime = System.nanoTime()
    }
    
    void draw() {
        if(!shell || shell.isDisposed()) return
        GC gc = new GC(canvas)
        gc.setInterpolation(SWT.NONE)
        gc.fillRectangle(canvas.getClientArea())
        
        playerOneScoreSprite.draw(spriteSheet, gc)
        playerTwoScoreSprite.draw(spriteSheet, gc)
        earthSprite.draw(spriteSheet, gc)
        baseSprite1.draw(spriteSheet, gc)
        baseSprite2.draw(spriteSheet, gc)
        baseSprite3.draw(spriteSheet, gc)
        invaderGroup.draw(spriteSheet, gc)
        shipSprite.draw(spriteSheet, gc)
        BulletSprite.drawAll(spriteSheet, gc)
        
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
        
         shell.open()
         while (!shell.isDisposed()) {
             long startTime = System.nanoTime()
             display.readAndDispatch()
             updateModel()
             draw()
             
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
        } finally {
            
            // Seems to be a bug in JavaSound/AWT when used with SWT.
            // So AWT thread never ends - we force it here
            System.exit(0)
        }
    }

    void widgetDisposed(DisposeEvent de) {
        InvaderSprite.invaderhit.close()
        BulletSprite.shipfire.close()
        ShipSprite.shiphit.close()
        InvaderGroup.invaderSound.close()
    }

}
