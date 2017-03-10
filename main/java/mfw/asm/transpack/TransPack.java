package mfw.asm.transpack;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import mfw.asm.classTransformer;

public abstract class TransPack implements Opcodes{
	
	protected String TARGET_CLASS = "";
	
	public abstract String[] getTargetClassName();
	//public 
	public FieldVisitor FieldAdapt(FieldVisitor fv, int access, String name , String desc, Object value){return fv;}
	public MethodVisitor MethodAdapt(MethodVisitor mv, String name, String desc){return null;}
	public void addMember(ClassWriter cw){}
	
	
	protected String MethodName;
	protected String MethodDesc;
	public String nowTarget;

	protected boolean check(String MethodName, String MethodForgeName, String Desc) {
		boolean flag = false;
		String target = classTransformer.mapMethodName(nowTarget, this.MethodName, MethodDesc);
		flag |= MethodName.equals(target);
		flag |= MethodForgeName.equals(target);
		flag &= Desc.equals("*") ? true : Desc.equals(MethodDesc);
		return flag;
	}
}
