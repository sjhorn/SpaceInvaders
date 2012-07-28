package com.hornmicro

import java.util.concurrent.atomic.AtomicLong

import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image

import org.eclipse.swt.internal.cocoa.NSColor
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

class GameLoop implements DisposeListener, PaintListener {
    Shell shell
    Image vader1Left, vader1Right
    AtomicLong delta = new AtomicLong(0)
    boolean vaderToggle = false
    
    void paintControl(PaintEvent pe) {
        GC gc = pe.gc
        gc.setAdvanced(true)
        gc.setAntialias(SWT.OFF)
        gc.setInterpolation(SWT.NONE)
        
        if(!vader1Left) {
            vader1Left = new Image(shell.display, "vader1_left.png")
            vader1Right = new Image(shell.display, "vader1_right.png")
        }
        
        if(delta.get() > 1000) {
            vaderToggle = !vaderToggle
            delta.set(0)
        }
        
        gc.drawImage(vaderToggle ? vader1Left : vader1Right,
            0, 0, vader1Left.width, vader1Left.height,
            400, 400, vader1Left.width * 10, vader1Left.height * 10)
    }
    
    void widgetDisposed(DisposeEvent e) {
        
    }
    
    void run() {
        Display display = new Display()
        shell = new Shell(display, SWT.NO_TRIM | SWT.NO_BACKGROUND | SWT.ON_TOP | SWT.DOUBLE_BUFFERED)
        shell.addPaintListener(this)
        shell.addDisposeListener(this)
        shell.window.setOpaque(false);
        shell.window.setBackgroundColor(NSColor.clearColor())
        shell.setLayout(new GridLayout(1, false))
        shell.pack()
        shell.setSize(display.clientArea.width, display.clientArea.height)
        shell.open()
        
        // Gameloop ??
        final Long lastLoopTime = System.currentTimeMillis();
//        Thread.start {
//            while(!shell.isDisposed()) {
//                delta.addAndGet(System.currentTimeMillis() - lastLoopTime)
//                lastLoopTime = System.currentTimeMillis()
//                shell.display.syncExec { 
//                    shell.redraw()
//                }
//                Thread.sleep(10)
//            }
//        }
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
//                display.sleep()
                delta.addAndGet(System.currentTimeMillis() - lastLoopTime)
                lastLoopTime = System.currentTimeMillis()
                shell.redraw()
                Thread.sleep(100)
            }
        }
        display.dispose()
    }
    
    static main(args) {
        new GameLoop().run()
    }

}
