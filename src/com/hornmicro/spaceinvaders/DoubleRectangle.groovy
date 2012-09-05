package com.hornmicro.spaceinvaders

import groovy.transform.CompileStatic

import org.eclipse.swt.graphics.Rectangle

@CompileStatic
class DoubleRectangle {
    public double left
    public double top
    public double width
    public double height
    public double right
    public double bottom
    
    DoubleRectangle(double left, double top, double width, double height) {
        setLeft(left)
        setTop(top)
        setWidth(width)
        setHeight(height)
    }
    
    static DoubleRectangle fromRectangle(Rectangle rect) {
        return new DoubleRectangle(rect.x as double, rect.y as double,
            rect.width as double, rect.height as double)
    }
    
    boolean intersects(DoubleRectangle other) {
        if (left < other.right && other.left < right && top < other.bottom && other.top < bottom) {
            return true
        }
        return false
    }
    
    boolean contains(double x, double y) {
        if(x >= left && x <= right && y >= top && y <= bottom) {
            return true
        }
        return false
    }
    
    
    void setLeft(double left) {
        this.left = left
        if(this.width) this.right = left + this.width
    }
    
    void setWidth(double width) {
        this.width = width
        if(this.left) this.right = this.left + width
    }
    
    void setTop(double top) {
        this.top = top
        if(this.height) this.bottom = top + this.height
    }
    
    void setHeight(double height) {
        this.height = height
        if(this.top) this.bottom = this.top + height
    }
    
    boolean outside(Rectangle bounds) {
        def res = this.top <= bounds.y || 
            bottom >= (bounds.y + bounds.height) ||
            this.left <= bounds.x ||
            right >= (bounds.x + bounds.width)
        //println "${this.top} <= ${bounds.y} || ${getBottom()} >= ${(bounds.y + bounds.height)} || ${this.left} <= ${bounds.x} || ${getRight()} >= ${(bounds.x + bounds.width)}"
        //println "Outside ? $res"
        return res
    }
    
    String toString() {
        return "$left, $top, $right, $bottom"
    }
    
    Rectangle asRectangle() {
        return new Rectangle(left as int, top as int, width as int, height as int)
    }
}
