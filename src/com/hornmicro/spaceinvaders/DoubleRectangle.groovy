package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class DoubleRectangle {
    public double left
    public double top
    public double width
    public double height
    Rectangle rect
    Rectangle otherRect
    
    DoubleRectangle(double left, double top, double width, double height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
    
    static DoubleRectangle fromRectangle(Rectangle rect) {
        return new DoubleRectangle(rect.x as double, rect.y as double,
            rect.width as double, rect.height as double)
    }
    
    boolean intersects(DoubleRectangle other) {
        rect.x = left as int 
        rect.y = top as int
        rect.width = width as int
        rect.height = height as int
        
        otherRect.x = other.left as int
        otherRect.y = other.top as int
        otherRect.width = other.width as int
        otherRect.height = other.height as int
        return rect.intersects(otherRect)
    }
    
    boolean intersects(Rectangle other) {
        rect.x = left as int
        rect.y = top as int
        rect.width = width as int
        rect.height = height as int
        
        return rect.intersects(other)
    } 
    
    Rectangle getRectangle() {
        rect.x = left as int
        rect.y = top as int
        rect.width = width as int
        rect.height = height as int
        return rect
    }
    
    double getRight() {
        return left + width
    }
    
    double getBottom() {
        return top + height    
    }
    
    double setRight(double right) {
        this.width = right - this.left
    }
    
    double setBottom(double bottom) {
        this.height = bottom - this.top
    }
    
    /*
    void setLeft(Number left) {
        this.left = left as double
    }
    
    void setTop(Number top) {
        this.top = top as double
    }
    */
}
