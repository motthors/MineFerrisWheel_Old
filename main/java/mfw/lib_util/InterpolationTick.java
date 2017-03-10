package mfw.lib_util;

public class InterpolationTick {

	float now;
	float prev;
	
	public InterpolationTick()
	{
		Init(0.0f);
	}
	public InterpolationTick(float init)
	{
		Init(init);
	}
	public void Init(float init)
	{
		now = init;
		prev = init;
	}
	
	public void update()
	{
		prev = now;
	}
	
	public void set(float set)
	{
		now = set;
	}
	
	public void add(float add)
	{
		now += add;
	}
	
	public void addAll(float add)
	{
		now += add;
		prev += add;
	}
	
	public void round()
	{
		if(now>180f)addAll(-360f);
		else if(now<-180f)addAll(360f);
	}
	
	public void clamp(float min, float max)
	{
		if(now > max)now = max;
		else if(now < min)now = min;
	}
	
	public float get()
	{
		return now;
	}
	
	public float getPrev()
	{
		return prev;
	}
	
	public float getFix(float partialtick)
	{
		return prev + (now - prev)*partialtick;
	}
}
