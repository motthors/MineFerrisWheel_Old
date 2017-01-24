package mfw._core;

public class connectPos{
	public float x,y,z;
	public float angle;
	public float len;
	
	public void copy(connectPos cp)
	{
		cp.x = x;
		cp.y = y;
		cp.z = z;
		cp.angle = angle;
		cp.len = len;
	}
}