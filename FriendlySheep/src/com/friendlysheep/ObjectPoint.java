package com.friendlysheep;

public class ObjectPoint {

    float x, y;
    float dx, dy;
    
    public ObjectPoint(float x, float y, float dx, float dy) {
		super();
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
    
    public ObjectPoint(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
    public String toString() {
        return x + ", " + y;
    }
    
}
