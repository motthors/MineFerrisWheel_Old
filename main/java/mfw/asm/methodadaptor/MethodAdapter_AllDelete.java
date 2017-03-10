package mfw.asm.methodadaptor;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

public class MethodAdapter_AllDelete extends MethodVisitor {
	
	private boolean endflag = false;
	
	public MethodAdapter_AllDelete(MethodVisitor mv) 
	{
		super(ASM5, mv);
	}

	public void visitParameter(String name, int access) {
		if(!endflag)super.visitParameter(name, access);
	}
	public void visitAttribute(Attribute attr) {
		if(!endflag)super.visitAttribute(attr);
	}
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		if(!endflag)super.visitFrame(type, nLocal, local, nStack, stack);
	}
	public void visitInsn(int opcode) {
		if(!endflag)super.visitInsn(opcode);
	}	//xxxInsn()の前に必ずくるの？
	public void visitIntInsn(int opcode, int operand) {
		if(!endflag)super.visitIntInsn(opcode, operand);
	}	//じか打ち整数
	public void visitVarInsn(int opcode, int var) {
		if(!endflag)super.visitVarInsn(opcode, var);
		}	//変数
	public void visitTypeInsn(int opcode, String type) {
		if(!endflag)super.visitTypeInsn(opcode, type);
	}	//newやinstanceofとか　　either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if(!endflag)super.visitFieldInsn(opcode, owner, name, desc);
	}	//either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
	@SuppressWarnings("deprecation")
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if(!endflag)super.visitMethodInsn(opcode, owner, name, desc);
	}
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){
		if(!endflag)super.visitMethodInsn(opcode, owner, name, desc, itf);
	}
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		if(!endflag)super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	}
	public void visitJumpInsn(int opcode, Label label) {
		if(!endflag)super.visitJumpInsn(opcode, label);
	}
	public void visitLabel(Label label) {
		if(!endflag)super.visitLabel(label);
    }
	public void visitLdcInsn(Object cst) {
		if(!endflag)super.visitLdcInsn(cst);
	}
	public void visitIincInsn(int var, int increment) {
		if(!endflag)super.visitIincInsn(var, increment);
	}
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		if(!endflag)super.visitTableSwitchInsn(min, max, dflt, labels);
	}
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		if(!endflag)super.visitLookupSwitchInsn(dflt, keys, labels);
	}
	public void visitMultiANewArrayInsn(String desc, int dims) {
		if(!endflag)super.visitMultiANewArrayInsn(desc, dims);
	}
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if(!endflag)return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
		return null;
	}
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		if(!endflag)super.visitTryCatchBlock(start, end, handler, type);
	}
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if(!endflag)return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
		return null;
	}
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if(!endflag)super.visitLocalVariable(name, desc, signature, start, end, index);
	}
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible){
		if(!endflag)return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
		return null;
	}
	public void visitLineNumber(int line, Label start) {
		if(!endflag)super.visitLineNumber(line, start);
	}
	public void visitMaxs(int maxStack, int maxLocals) {
		if(!endflag)super.visitMaxs(maxStack, maxLocals);
	}
	public void visitEnd() {
		super.visitEnd();
		endflag = true;
	}
}