package mfw.asm.methodadaptor;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class MethodAdapter_Logger extends MethodVisitor{

	public MethodAdapter_Logger(MethodVisitor mv) 
	{
		super(ASM5, mv);
	}
	
	public void visitCode()
	{
		log("visitCode");
		super.visitCode();
	}
	
	public void log(String tag)
	{
		FMLRelaunchLog.info("TransformLog : MethodLogger : "+tag);
	}
	
//	visitAnnotationDefault()
//	visitAnnotation(String, boolean)
//	visitTypeAnnotation(int, TypePath, String, boolean)
//	visitParameterAnnotation(int, String, boolean)
	
	public void visitParameter(String name, int access) {
		log("visitParameter:"+name+":"+access);super.visitParameter(name, access);
	}
	public void visitAttribute(Attribute attr) {
		log("visitAttribute:");super.visitAttribute(attr);
	}
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		log("visitFrame:"+type+":"+nLocal);super.visitFrame(type, nLocal, local, nStack, stack);
	}
	public void visitInsn(int opcode) {
		log("visitInsn:"+opcode);super.visitInsn(opcode);
	}	//xxxInsn()の前に必ずくるの？
	public void visitIntInsn(int opcode, int operand) {
		log("visitIntInsn op:"+opcode+" oprnd:"+operand);super.visitIntInsn(opcode, operand);
	}	//じか打ち整数
	public void visitVarInsn(int opcode, int var) {
		log("visitVarInsn"+opcode+":"+var);super.visitVarInsn(opcode, var);
	}	//変数
	public void visitTypeInsn(int opcode, String type) {
		log("visitTypeInsn:op:"+opcode+":"+type);super.visitTypeInsn(opcode, type);
	}	//newやinstanceofとか　　either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		log("visitFieldInsn:"+opcode+":"+owner+"."+name+":"+desc);super.visitFieldInsn(opcode, owner, name, desc);
	}	//either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
	@SuppressWarnings("deprecation")
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		log("visitMethodInsn:"+name);super.visitMethodInsn(opcode, owner, name, desc);
	}
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){
		log("visitMethodInsn2:"+opcode+":"+name+desc);super.visitMethodInsn(opcode, owner, name, desc, itf);
	}
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		log("visitInvokeDynamicInsn:"+name);super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	}
	public void visitJumpInsn(int opcode, Label label) {
		log("visitJumpInsn:"+opcode+":"+label);super.visitJumpInsn(opcode, label);
	}
	public void visitLabel(Label label) {
		log("visitLabel:"+label);super.visitLabel(label);
    }
	public void visitLdcInsn(Object cst) {
		log("visitLdcInsn:"+cst.toString());super.visitLdcInsn(cst);
	}
	public void visitIincInsn(int var, int increment) {
		log("visitIincInsn:");super.visitIincInsn(var, increment);
	}
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		log("visitTableSwitchInsn:");super.visitTableSwitchInsn(min, max, dflt, labels);
	}
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		log("visitLookupSwitchInsn:");super.visitLookupSwitchInsn(dflt, keys, labels);
	}
	public void visitMultiANewArrayInsn(String desc, int dims) {
		log("visitMultiANewArrayInsn:");super.visitMultiANewArrayInsn(desc, dims);
	}
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		log("visitInsnAnnotation:");return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
	}
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		log("visitTryCatchBlock:");super.visitTryCatchBlock(start, end, handler, type);
	}
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		log("visitTryCatchAnnotation:"); return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
	}
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		log("visitLocalVariable:"+name+":"+signature);super.visitLocalVariable(name, desc, signature, start, end, index);
	}
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible){
		log("visitLocalVariableAnnotation:");return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
	}
	public void visitLineNumber(int line, Label start) {
		log("visitLineNumber:"+line+" : "+start);super.visitLineNumber(line, start);
	}
	public void visitMaxs(int maxStack, int maxLocals) {
		log("visitMaxs:"+maxStack+":"+maxLocals);super.visitMaxs(maxStack, maxLocals);
	}
	public void visitEnd() {
		log("visitEnd:");super.visitEnd();
	}
}
