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

//@CompileStatic
class SpaceInvaders implements PaintListener, DisposeListener, Listener {
    Canvas canvas
    Display display
    Shell shell
    Image spriteSheet
    BaseSprite baseSprite1
    BaseSprite baseSprite2
    BaseSprite baseSprite3
    Ship1Sprite ship1Sprite
    Ship2Sprite ship2Sprite
    EarthSprite earthSprite
    PlayerOneScoreSprite playerOneScoreSprite
    PlayerTwoScoreSprite playerTwoScoreSprite
    
    InvaderGroup invaderGroup
    CommandShipSprite commandShipSprite
    
    Rectangle bounds
    boolean paused = false
    boolean twoPlayer = false
    boolean gameOver = false
    boolean aIEnabled = false
    
    long gameOverTime = 0
    long aiTime = 0
    long lastTime = 0
    long commandShipTime = 0
    
    int level = 0
    
    SpaceInvaders() {
        Display.setAppName("Space Invaders")
        display = new Display()
        configureShell()
        
        InputStream imageInputStream = getClass().getResourceAsStream("SpriteSheet.png")
        spriteSheet = (imageInputStream != null ? new Image(display, imageInputStream) : new Image(display, "gfx/SpriteSheet.png"))  
        bounds = shell.getClientArea()
        
        startLevel()
    }
    
    void startLevel() {
        
        // Add Players
        if(level == 0) {
            
            // Scores
            Rectangle scoreBounds = new Rectangle((bounds.width / 2 - 288) as int, (bounds.height / 2 - 220) as int, 576, 440)
            playerOneScoreSprite = new PlayerOneScoreSprite(scoreBounds)
            playerTwoScoreSprite = new PlayerTwoScoreSprite(scoreBounds)
            
            // Add Earth
            earthSprite = new EarthSprite(bounds)
            
            Rectangle shipBounds = new Rectangle(bounds.width/2 - 312/2 as int, 0, 312, bounds.height)
            ship1Sprite = new Ship1Sprite(shipBounds)
            if(twoPlayer) {
                ship2Sprite = new Ship2Sprite(shipBounds)
            }
        }

        // Add bases
        def baseY = bounds.height/2 + 110
        baseSprite1 = new BaseSprite(bounds, new DoubleRectangle(bounds.width / 2 - 130, baseY, 31,36))
        baseSprite2 = new BaseSprite(bounds, new DoubleRectangle(bounds.width / 2 - 15, baseY, 31,36))
        baseSprite3 = new BaseSprite(bounds, new DoubleRectangle(bounds.width / 2 + 100, baseY, 31,36))
        
        // Add space invaders
        def vaderOffset = (level % 6) * 22  
        Rectangle invaderBounds = new Rectangle(bounds.width / 2 - 210, bounds.height/2 - 160 + vaderOffset, 420, 396 - vaderOffset)
        def vaderSpeed = 700_000_000 - (level % 12) * 45_000_000
        invaderGroup = new InvaderGroup(invaderBounds, vaderSpeed)
        
        BulletSprite.bullets.clear()
        commandShipSprite?.hide()
    }
    
    boolean isFrozen() {
        return  (ship1Sprite.isStarting() || ship1Sprite.isExploding()) ||
            (twoPlayer && (ship2Sprite.isStarting() || ship2Sprite.isExploding()))
    } 
    
    void handleEvent(Event event) {
        if(event.type == SWT.KeyDown) {
            switch(event.keyCode) {
               case SWT.ARROW_RIGHT:
                   ship1Sprite.moveRight = true
                   break
               case SWT.ARROW_LEFT:
                   ship1Sprite.moveLeft = true
                   break
               case SWT.SPACE:
               case 0x20:
                   if(!isFrozen()) {
                       BulletSprite.fireFromShip(ship1Sprite)
                   }
                   break
            }
            switch(event.character) {
                case 'j':
                    ship2Sprite.moveRight = true
                    break
                case 'g':
                    ship2Sprite.moveLeft = true
                    break
                case 'f':
                    if(!isFrozen()) {
                        BulletSprite.fireFromShip(ship2Sprite)
                    }
                    break
            }
            
        } else if (event.type == SWT.KeyUp) {
            switch(event.keyCode) {
                case SWT.ARROW_RIGHT:
                    ship1Sprite.moveRight = false
                    break
                case SWT.ARROW_LEFT:
                    ship1Sprite.moveLeft = false
                    break
            }
            
            switch(event.character) {
               case 'j':
                   ship2Sprite.moveRight = false
                   break
               case 'g':
                   ship2Sprite.moveLeft = false
                   break
                
               case '1':
                   level = 0
                   twoPlayer = false
                   startLevel()
                   break
               case '2':
                   level = 0
                   twoPlayer = true
                   startLevel()
                   break
                   
               case '3':
                   // Toggle AI
                   aIEnabled = !aIEnabled
                   if(!aIEnabled) {
                       ship1Sprite.moveRight = false
                       ship1Sprite.moveLeft = false
                   }
                   break
               case '4':
                   BulletSprite.powerMode = !BulletSprite.powerMode 
                   break
                   
               case 'x':
                   for(InvaderSprite invader: invaderGroup.invaders) {
                       playerOneScoreSprite.score += invader.score
                       invader.explode()
                   }
                   if(commandShipSprite && !commandShipSprite.isHidden()) {
                       playerOneScoreSprite.score += commandShipSprite.score
                       commandShipSprite.explode()
                   }
                   break
                   
               case 'p':
                   paused = !paused
                   break
               
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
        
        shell.setSize(576,440)
        //shell.setSize(1200, 900)

        shell.layout()
    }
    
    void paintControl(PaintEvent pe) {
        if(shell && !shell.isDisposed()) {
            GC gc = pe.gc
            
            earthSprite.draw(spriteSheet, gc)
            
            // Hide bases when the invaders reach them
            if(invaderGroup.location.bottom >= baseSprite1.location.top) {
                baseSprite1.hide()
                baseSprite2.hide()
                baseSprite3.hide()
            }
            
            baseSprite1.draw(spriteSheet, gc)
            baseSprite2.draw(spriteSheet, gc)
            baseSprite3.draw(spriteSheet, gc)
            invaderGroup.draw(spriteSheet, gc)
            if(commandShipSprite && !commandShipSprite.isHidden()) {
                commandShipSprite.draw(spriteSheet, gc)
            }
            
            ship1Sprite.draw(spriteSheet, gc)
            if(twoPlayer) {
                ship2Sprite.draw(spriteSheet, gc)
            }
            BulletSprite.drawAll(spriteSheet, gc)
            
            if(!commandShipSprite || commandShipSprite.isHidden()) {
                playerOneScoreSprite.draw(spriteSheet, gc)
                playerTwoScoreSprite.draw(spriteSheet, gc)
            }
        }
    }
    
    long nanoTime() {
        return System.nanoTime()
    }
    
    void doGameOver() {
        
        // If we are down to 0 lives or the vaders have reached earth its game over.
        gameOver = true
        level = 0
        BulletSprite.bullets.clear()
        ship1Sprite.hide()
        if(twoPlayer) {
            ship2Sprite.hide()
        }
    }
    
    void newLevel() {
        level++
        startLevel()
        if(ship1Sprite.lives) {
            ship1Sprite.newLevel()
        }
        if(twoPlayer && ship2Sprite.lives) {
            ship2Sprite.newLevel()
        }
    }
    
    void updateModel() {
        if(lastTime && !paused) {
            long timePassed = nanoTime() - lastTime
            
            // Check for game over otherwise update model
            if(gameOver) {
                gameOverTime += timePassed
                if(gameOverTime > 3_000_000_000) {
                    gameOver = false
                    gameOverTime = 0
                    startLevel()
                }
            } else {
                boolean freeze = isFrozen() 
                
                invaderGroup.move(timePassed, freeze)
                if(!freeze) {
                    commandShipSprite?.move(timePassed)
                }
                ship1Sprite.move(timePassed, freeze)
                if(twoPlayer) {
                    ship2Sprite.move(timePassed, freeze)
                }
                
                if(freeze) {
                    // Do nothing
                } else if(invaderGroup.invaders.size() == 0) {
                    newLevel()
                } else if( invaderGroup.location.bottom >= ship1Sprite.location.top){
                    doGameOver()
                    ship1Sprite.shiphit.play()
                } else if ((!twoPlayer && ship1Sprite.lives < 1) || (ship1Sprite.lives < 1 && ship2Sprite.lives < 1)) {
                    doGameOver()
                } else {
                    
                    // Command Ship
                    commandShipTime += timePassed
                    if(commandShipTime > 10_000_000_000) {
                        commandShipTime = 0
                        if(!commandShipSprite || commandShipSprite.isHidden()) {
                            commandShipSprite = new CommandShipSprite(bounds)
                        }
                    }    
                
                    // Move bullets
                    BulletSprite.moveAll(timePassed)
                    
                    // Detect collisions
                    List sprites = [ invaderGroup.invaders, baseSprite1, baseSprite2, baseSprite3 ]
                    
                    if(ship1Sprite.lives > 0) {
                        sprites.add(ship1Sprite)
                    }
                    if(twoPlayer && ship2Sprite.lives > 0) {
                        sprites.add(ship2Sprite)
                    }
                    if(commandShipSprite && !commandShipSprite.isHidden()) {
                        sprites.add(commandShipSprite)
                    }
                    
                    for(Sprite sprite: BulletSprite.detectCollisions(sprites)) {
                        sprite.explode()
                        if(sprite instanceof ScoringSprite) {
                            BulletSprite bullet = (BulletSprite) sprite.hitBy
                            if(bullet.type == BulletSprite.TYPE.SHIP1) {
                                playerOneScoreSprite.score += ((ScoringSprite)sprite).score
                            } else {
                                playerTwoScoreSprite.score += ((ScoringSprite)sprite).score
                            }
                        } else if(sprite instanceof ShipSprite) {
                            BulletSprite.bullets.clear()
                        }
                        
                    }
                }
            }
            
            if(aIEnabled && !isFrozen()) {
                aiTime += timePassed
                if(aiTime > 250_000_000) {
                    Random r = new Random()
                    int choice = r.nextInt(7)
                    switch(choice) {
                        case 1..2:
                            ship1Sprite.moveRight = true
                            break
                        case 3:
                            ship1Sprite.moveLeft = true
                            break
                        default:
                            ship1Sprite.moveRight = false
                            ship1Sprite.moveLeft = false
                            BulletSprite.fireFromShip(ship1Sprite)
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
        CommandShipSprite.commandshiphit.close()
    }

    
    static main(args) {
        try {
            SpaceInvaders spaceInvaders = new SpaceInvaders()
            if(args?.find{it == '-a' }) {
                spaceInvaders.aIEnabled = true
            }
            spaceInvaders.gameLoop()
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
