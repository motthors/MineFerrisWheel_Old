package mfw.asm.methodadaptor;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapter_Test extends MethodVisitor {
	
	public MethodAdapter_Test(MethodVisitor mv) 
	{
		super(ASM5, mv);
	}
	public static int MethodCount = 0;
	public void visitVarInsn(int opcode, int var) 
	{
		if(MethodCount++==0)
		{
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "limitbreak/asm/_1_classTransformer", "test", "()V", false);
		}
		super.visitVarInsn(opcode, var);
	}
}