package com.hornmicro.archive

import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.internal.cocoa.NSColor
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Shell
import org.pushingpixels.trident.Timeline
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState
import org.pushingpixels.trident.callback.TimelineCallback
import org.pushingpixels.trident.callback.TimelineCallbackAdapter
import org.pushingpixels.trident.interpolator.KeyFrames
import org.pushingpixels.trident.interpolator.KeyTimes
import org.pushingpixels.trident.interpolator.KeyValues

class FirstSpaceInvaders implements PaintListener, DisposeListener, Listener {
    Image spacecraft, vader1Left, vader1Right
    Shell shell
    Timeline timeline, vaderTimeline
    int vaderToggle = 0
    int startingLeft = -1
    int sizeMultiply = 3
    
    int left = 1, vaderLeft = 0
    Point bullet = new Point(0, -20)
    
    void setLeft(int left) {
        this.left = left
        if(!shell.isDisposed())
            shell.display.asyncExec { !shell.isDisposed() && shell.redraw() }
    }
    
    void paintControl(PaintEvent pe) {
        GC gc = pe.gc
        gc.setAdvanced(true)
        gc.setAntialias(SWT.OFF)
        gc.setInterpolation(SWT.NONE)
//        gc.setAlpha(200)
//        gc.setBackground(shell.display.getSystemColor(SWT.COLOR_BLACK))
//        gc.fillRectangle(shell.getClientArea())
        
        if(!spacecraft) {
            spacecraft = new Image(shell.display, "spacecraft.png")
            vader1Left = new Image(shell.display, "vader1_left.png")
            vader1Right = new Image(shell.display, "vader1_right.png")
        }
        if(bullet.y != -20) {
            gc.setBackground(shell.display.getSystemColor(SWT.COLOR_GRAY))
            gc.fillRectangle(bullet.x, bullet.y-(20*sizeMultiply), 4*sizeMultiply, 10*sizeMultiply)
        }
         
        //gc.setAlpha(255)
        if(startingLeft == -1) {
            startingLeft = shell.display.clientArea.width / 2 - 3 * vader1Left.width*sizeMultiply*2
        }
        (0..5).each {
            gc.drawImage(vaderToggle ? vader1Left : vader1Right,
                0, 0, vader1Left.width, vader1Left.height,
                startingLeft + (it*vader1Left.width*sizeMultiply*2), 10, vader1Left.width * sizeMultiply, vader1Left.height * sizeMultiply)
        }
        
        gc.drawImage(spacecraft, 
            0, 0, spacecraft.width, spacecraft.height, 
            left, shell.display.clientArea.height - (spacecraft.height * sizeMultiply) , spacecraft.width * sizeMultiply, spacecraft.height * sizeMultiply)
    }
    
    void widgetDisposed(DisposeEvent e) {
        spacecraft?.dispose()
    }
    
    int lastkey
    void handleEvent(Event event) {
        if(event.type == SWT.KeyDown && lastkey != event.keyCode) {
            Rectangle screen = shell.display.getBounds()
            int newLoc
            Point newBullet 
            switch(event.keyCode) {
               case 16777220:
                   newLoc = screen.width - spacecraft.width * 2
                   break
               case 16777219:
                   newLoc = 1
                   break
               case 32:
                   if(bullet.y == -20) {
                       newBullet = new Point((left + spacecraft.width*sizeMultiply/2 - 2*sizeMultiply) as int, screen.height - spacecraft.height*sizeMultiply)
                       Timeline bulletTimeLine = new Timeline(this)
                       bulletTimeLine.addPropertyToInterpolate("bullet", newBullet, new Point(newBullet.x, -20))
                       bulletTimeLine.setDuration(screen.height)
                       bulletTimeLine.addCallback(new TimelineCallback() {
                           void onTimelinePulse(float arg0, float arg1) {
                               if(!shell.isDisposed())
                                   shell.display.asyncExec { !shell.isDisposed() && shell.redraw() }
                           }
                           void onTimelineStateChanged(TimelineState arg0, TimelineState arg1, float arg2, float arg3) {
                           }
                       })
                       bulletTimeLine.play()
                   }
                   return
                   break;
               default:
                   println event.keyCode
                   return
            }
            timeline?.cancel()
            timeline = new Timeline(this)
            
            timeline.setDuration( (Math.abs(newLoc - left) / 2) as int)
            timeline.addPropertyToInterpolate("left", left, newLoc)
            timeline.play()
            
            
            lastkey = event.keyCode
        } else if (event.type == SWT.KeyUp) {
            timeline?.cancel()
            lastkey = 0
        }
    }
    
    void run() {
        Display.appName = "Mac Vaders" 
        Display display = new Display()
        
        display.addFilter(SWT.KeyDown, this)
        display.addFilter(SWT.KeyUp, this)
        Rectangle screen = display.getBounds()
        
        shell = new Shell(display, SWT.NO_TRIM | SWT.NO_BACKGROUND | SWT.ON_TOP | SWT.DOUBLE_BUFFERED)
        shell.setBounds(1,25, screen.width, screen.height-25)
        shell.addPaintListener(this)
        shell.addDisposeListener(this)
        shell.window.setOpaque(false);
        shell.window.setBackgroundColor(NSColor.clearColor())

        KeyValues vaderValues = KeyValues.create(1, 1, 0, 0)
        KeyTimes vaderTimes = new KeyTimes(0.0f, 0.49f, 0.5f,  1.0f)        
        vaderTimeline = new Timeline(this)
        vaderTimeline.addPropertyToInterpolate("vaderToggle", new KeyFrames(vaderValues, vaderTimes))
        vaderTimeline.setDuration(1000)
        vaderTimeline.addCallback(new TimelineCallbackAdapter() {
            @Override
            public void onTimelinePulse(float durationFraction,
                    float timelinePosition) {
                if(!shell.isDisposed())
                    shell.display.asyncExec { !shell.isDisposed() && shell.redraw() }
            }
        })
        vaderTimeline.playLoop(RepeatBehavior.REVERSE)
        
        
        
        shell.open()
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep()
        }
        vaderTimeline.abort()
        display.dispose()
    }
    
    
    static main(args) {
        new FirstSpaceInvaders().run()
    }

}
