package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import java.util.Random;
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
    boolean gameOver = false
    boolean aIEnabled = false
    long gameOverTime = 0
    long aiTime = 0
    long lastTime = 0
    
    SpaceInvaders() {
        Display.setAppName("Space Invaders")
        display = new Display()
        configureShell()
        
        InputStream imageInputStream = getClass().getResourceAsStream("SpriteSheet.png")
        spriteSheet = (imageInputStream != null ? new Image(display, imageInputStream) : new Image(display, "gfx/SpriteSheet.png"))  
        bounds = shell.getClientArea()
        
        initSprites()
    }
    
    void initSprites() {
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
        Rectangle shipBounds = new Rectangle(131, 0, 312, bounds.height)
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
                    shipSprite.moveRight = false
                    break
            }
            
            if(event.character == 'a') {
                // Toggle AI
                aIEnabled = !aIEnabled
                println "AI is $aIEnabled"
                if(!aIEnabled) {
                    shipSprite.moveRight = false
                    shipSprite.moveLeft = false
                }
            }
            
        }
    }
    
    void configureShell() {
        display.addFilter(SWT.KeyDown, this)
        display.addFilter(SWT.KeyUp, this)
        
        shell = new Shell(display)
        
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
        if(shell && !shell.isDisposed()) {
            GC gc = pe.gc
            playerOneScoreSprite.draw(spriteSheet, gc)
            playerTwoScoreSprite.draw(spriteSheet, gc)
            earthSprite.draw(spriteSheet, gc)
            baseSprite1.draw(spriteSheet, gc)
            baseSprite2.draw(spriteSheet, gc)
            baseSprite3.draw(spriteSheet, gc)
            invaderGroup.draw(spriteSheet, gc)
            shipSprite.draw(spriteSheet, gc)
            BulletSprite.drawAll(spriteSheet, gc)
        }
    }
    
    long nanoTime() {
        //return (System.currentTimeMillis() * 1_000_000) 
        return System.nanoTime()
    }
    
    void updateModel() {
        if(lastTime) {
            long timePassed = nanoTime() - lastTime
            
            // Check for game over otherwise update model
            if(gameOver) {
                gameOverTime += timePassed
                if(gameOverTime > 3_000_000_000) {
                    gameOver = false
                    gameOverTime = 0
                    initSprites()
                }
            } else {
                invaderGroup.move(timePassed, shipSprite.isStarting() || shipSprite.isExploding())
                
                // If we are down to 0 lives or the vaders have reached earth its game over.
                if(invaderGroup.location.bottom >= shipSprite.location.top || (shipSprite.lives == 0 && !shipSprite.isStarting()) ) {
                    gameOver = true
                    shipSprite.hide()
                    
                    shipSprite.shiphit.play()
                } 
                
                if(invaderGroup.location.bottom >= baseSprite1.location.top) {
                    baseSprite1.hide()
                    baseSprite2.hide()
                    baseSprite3.hide()
                }
                shipSprite.move(timePassed)
                BulletSprite.moveAll(timePassed)
                
                // Detect collisions
                List sprites = [ shipSprite, invaderGroup.invaders, baseSprite1, baseSprite2, baseSprite3 ]
                for(Sprite sprite: BulletSprite.detectCollisions(sprites)) {
                    sprite.explode()
                }
            }
            
            if(aIEnabled && !shipSprite.isStarting()) {
                aiTime += timePassed
                if(aiTime > 250_000_000) {
                    Random r = new Random()
                    int choice = r.nextInt(7)
                    switch(choice) {
                        case 1..2:
                            shipSprite.moveRight = true
                            break
                        case 3:
                            shipSprite.moveLeft = true
                            break
                        default:
                            shipSprite.moveRight = false
                            shipSprite.moveLeft = false
                            BulletSprite.fireFromShip(shipSprite)
                            break 
                    }
                    aiTime = 0
                }
            }
            
        }
        
        lastTime = nanoTime()
    }
    
    void draw() {
        if(!shell.isDisposed()) {
            canvas.redraw()
            canvas.update()
        }
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
             long startTime = nanoTime()
             display.readAndDispatch()
             
             updateModel()
             draw()
             updateModel()
             
             // Aim for 50fps (1s/100 frames = 10,000,000 ns / frame)
             long sleepTime = startTime + 20_000_000 - nanoTime()
             TimeUnit.NANOSECONDS.sleep(sleepTime)
         }
         display.dispose()
    }
    
    void widgetDisposed(DisposeEvent de) {
        InvaderSprite.invaderhit.close()
        BulletSprite.shipfire.close()
        ShipSprite.shiphit.close()
        InvaderGroup.invaderSound.close()
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
}
