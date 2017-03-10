package mfw.asm.methodadaptor;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapter_ReplaceNEW extends MethodVisitor implements Opcodes{

	Map<String, String> map = new HashMap<String,String>();

	public MethodAdapter_ReplaceNEW(MethodVisitor mv) 
	{
		super(ASM5, mv);
	}
	
	public void add(String old, String New)
	{
		map.put(old, New);
	}
	
	// new constractor change
	public void visitTypeInsn(int opcode, String type)
	{
		String rep =  map.get(type);
		if( opcode == NEW && rep!=null )type = rep;
		super.visitTypeInsn(opcode, type);
	}
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
	{
		String rep =  map.get(owner);
		if( opcode == INVOKESPECIAL 
				&& rep!=null
				&& name.equals("<init>"))
			owner = rep;
		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}
}
