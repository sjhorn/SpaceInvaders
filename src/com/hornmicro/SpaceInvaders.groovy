package com.hornmicro

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
import org.pushingpixels.trident.Timeline.TimelineState
import org.pushingpixels.trident.callback.TimelineCallback
import org.pushingpixels.trident.swt.SWTRepaintTimeline;

class SpaceInvaders implements PaintListener, DisposeListener, Listener {
    Image spacecraft
    Shell shell
    Timeline timeline
    SWTRepaintTimeline paintTimeline
    int left = 1
    Point bullet = new Point(0,-20)
    
    void setLeft(int left) {
        this.left = left
        shell.display.asyncExec { shell.redraw() }
    }
    
    void paintControl(PaintEvent pe) {
        GC gc = pe.gc
        gc.setAdvanced(true)
        gc.setAntialias(SWT.ON)
        
//        gc.setAlpha(200)
//        gc.setBackground(shell.display.getSystemColor(SWT.COLOR_BLACK))
//        gc.fillRectangle(shell.getClientArea())
        
        if(!spacecraft) {
            spacecraft = new Image(Display.current, "spacecraft.png")
        }
        if(bullet.y != -20) {
            gc.setBackground(shell.display.getSystemColor(SWT.COLOR_GRAY))
            gc.fillRectangle(bullet.x, bullet.y-20, 10, 20)
        }
         
        gc.setAlpha(255)
        gc.drawImage(spacecraft, 
            0, 0, spacecraft.width, spacecraft.height, 
            left, shell.display.bounds.height - 60, spacecraft.width * 2, spacecraft.height * 2)
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
                   newLoc = screen.width - spacecraft.width*2
                   break
               case 16777219:
                   newLoc = 1
                   break
               case 32:
                   if(bullet.y == -20) {
                       newBullet = new Point(left + spacecraft.width - 5, screen.height - spacecraft.height*2)
                       Timeline bulletTimeLine = new Timeline(this)
                       bulletTimeLine.addPropertyToInterpolate("bullet", newBullet, new Point(newBullet.x, -20))
                       bulletTimeLine.setDuration(screen.height)
                       bulletTimeLine.addCallback(new TimelineCallback() {
                           void onTimelinePulse(float arg0, float arg1) {
                               shell.display.asyncExec { shell.redraw() }
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
        //shell.setBounds(100, screen.height - 60, 56, 40)
        shell.setBounds(1,25, screen.width, screen.height-25)
        shell.addPaintListener(this)
        shell.addDisposeListener(this)
        shell.window.setOpaque(false);
        shell.window.setBackgroundColor(NSColor.clearColor())
//        paintTimeline = new SWTRepaintTimeline(shell)
//        paintTimeline.setAutoRepaintMode(false);
        shell.open()
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep()
        }
        display.dispose()
    }
    
    
    static main(args) {
        new SpaceInvaders().run()
    }

}
