package mfw.math;

import net.minecraft.util.Vec3;

public class MFW_Math {
	
	public static class Vec4{
		float x; float y; float z; float w;
		public Vec4(){x=0;y=0;z=0;w=0;}
		public Vec4(float x,float y,float z,float w){this.x = x; this.y = y; this.z = z; this.w = w;}
		public Vec4(double x,double y,double z,double w){this.x = (float) x; this.y = (float) y; this.z = (float) z; this.w = (float) w;}
	}

	public static Vec3 Lerp(float t, Vec3 base, Vec3 next)
	{
		return Vec3.createVectorHelper(
				base.xCoord*(1-t)+next.xCoord*t, 
				base.yCoord*(1-t)+next.yCoord*t, 
				base.zCoord*(1-t)+next.zCoord*t
				);
	}

	
	public static double angleTwoVec3(Vec3 a, Vec3 b)
	{
//		@SuppressWarnings("unused")
//		double temp = a.normalize().dotProduct(b.normalize());
		return Math.acos( clamp(a.normalize().dotProduct(b.normalize())) );
	}
	
	public static double clamp(double a)
	{
		return a>1d?1d:(a<-1d?-1d:a);
	}
	
	public static float wrap(float a)
	{
		if(a >  Math.PI)a -= Math.PI*2;
		if(a < -Math.PI)a += Math.PI*2;
		return a;
	}
	
	public static float Lerp(float t, float a1, float a2)
	{
		return a1*(1-t) + a2*t;
	}
	
	public static Vec3 rotateAroundVector(double xCoord, double yCoord, double zCoord, 
			double axisx, double axisy, double axisz, double radian)
	{
		radian *= 0.5;
		Vec4 Qsrc = new Vec4(0,xCoord,yCoord,zCoord);
		Vec4 Q1 = new Vec4(Math.cos(radian), axisx*Math.sin(radian), axisy*Math.sin(radian), axisz*Math.sin(radian));
		Vec4 Q2 = new Vec4(Math.cos(radian),-axisx*Math.sin(radian),-axisy*Math.sin(radian),-axisz*Math.sin(radian));
		
		Vec4 ans = MulQuaternion(MulQuaternion(Q2, Qsrc), Q1);
		return Vec3.createVectorHelper(ans.y, ans.z, ans.w);
	}
	public static void rotateAroundVector(Vec3 vecInOut, double xCoord, double yCoord, double zCoord, double radian)
	{
		radian *= 0.5;
		Vec4 Qsrc = new Vec4(0,vecInOut.xCoord,vecInOut.yCoord,vecInOut.zCoord);
		Vec4 Q1 = new Vec4(Math.cos(radian), xCoord*Math.sin(radian), yCoord*Math.sin(radian), zCoord*Math.sin(radian));
		Vec4 Q2 = new Vec4(Math.cos(radian),-xCoord*Math.sin(radian),-yCoord*Math.sin(radian),-zCoord*Math.sin(radian));
		
		Vec4 ans = MulQuaternion(MulQuaternion(Q2, Qsrc), Q1);
		vecInOut.xCoord = ans.y;
		vecInOut.yCoord = ans.z;
		vecInOut.zCoord = ans.w;
	}
	public static Vec3 rotateAroundVector(Vec3 rotpos, Vec3 axis, double radian)
	{
		radian *= 0.5;
		Vec4 Qsrc = new Vec4(0,rotpos.xCoord,rotpos.yCoord,rotpos.zCoord);
		Vec4 Q1 = new Vec4(Math.cos(radian), axis.xCoord*Math.sin(radian), axis.yCoord*Math.sin(radian), axis.zCoord*Math.sin(radian));
		Vec4 Q2 = new Vec4(Math.cos(radian),-axis.xCoord*Math.sin(radian),-axis.yCoord*Math.sin(radian),-axis.zCoord*Math.sin(radian));
	
		Vec4 ans = MulQuaternion(MulQuaternion(Q2, Qsrc), Q1);
		return Vec3.createVectorHelper(ans.y, ans.z, ans.w);
	}
	private static Vec4 MulQuaternion(Vec4 q1, Vec4 q2)
	{
		return new Vec4(
				q1.x*q2.x - (q1.y*q2.y+q1.z*q2.z+q1.w*q2.w),
				q1.x*q2.y + q2.x*q1.y + (q1.z*q2.w - q1.w*q2.z),
				q1.x*q2.z + q2.x*q1.z + (q1.w*q2.y - q1.y*q2.w),
				q1.x*q2.w + q2.x*q1.w + (q1.y*q2.z - q1.z*q2.y)
				);
	}
	
	// ‹…–ÊüŒ`•âŠÔ
	public static Vec3 Slerp(float t, Vec3 Base, Vec3 Goal)
	{
		double theta = Math.acos(clamp(Base.dotProduct(Goal)));
		if(theta == 0 || theta == 1d)return Base;
		double sinTh = Math.sin(theta);
		double Pb = Math.sin(theta*(1-t));
		double Pg = Math.sin(theta*t);
		return Vec3.createVectorHelper(
				(Base.xCoord*Pb + Goal.xCoord*Pg)/sinTh, 
				(Base.yCoord*Pb + Goal.yCoord*Pg)/sinTh, 
				(Base.zCoord*Pb + Goal.zCoord*Pg)/sinTh);
	}
	
	public static float fixrot(float rot, float prevrot)
    {
    	if(rot - prevrot>180f)prevrot += 360f;
        else if(rot - prevrot<-180f)prevrot -= 360f;
    	return prevrot;
    }
	
	
}
