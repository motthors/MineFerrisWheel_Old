package mfw.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import mfw.asm.transpack.TransPack;

public class _2_classAdapter extends ClassVisitor {
	
	private TransPack pack;
	
	public _2_classAdapter(ClassVisitor cv, TransPack pack)
	{
		super(ASM5, cv);
		this.pack = pack;
	}

	@Override
	public void visit(int version, int access, String name, String signature, 
			String superName, String[] interfaces)
	{
		//if(name.contains("Minecraft"))
//		{
//			FMLRelaunchLog.info("visit : "+version+" : "+access
//					+" : "+name+" : "+signature
//					+" : "+superName+" : "+interfaces);
//		}
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		FieldVisitor fv = super.visitField(access, name, desc, signature, value);
		return pack.FieldAdapt(fv, access, name, desc, value);
	}
	
	/**
	 * ���\�b�h�ɂ��ČĂ΂��B
	 * 
	 * @param access  {@link Opcodes}�ɍڂ��Ă��Bpublic�Ƃ�static�Ƃ��̏�Ԃ��킩��B
	 * @param name	���\�b�h�̖��O�B
	 * @param desc ���\�b�h��(�����ƕԂ�l�����킹��)�^�B
	 * @param signature   �W�F�l���b�N�������܂ރ��\�b�h��(�����ƕԂ�l�����킹��)�^�B�W�F�l���b�N�t���łȂ���΂����炭null�B
	 * @param exceptions  throws��ɂ�����Ă���N���X���񋓂����BL��;�ň͂��Ă��Ȃ��̂�  {@link String#replace(char, char)}��'/'��'.'��u�����Ă���OK�B
	 * @return �����ŕԂ���MethodVisitor�̃��\�b�h�Q���K�������B  ClassWriter���Z�b�g����Ă����MethodWriter��super����~��Ă���B
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		//FMLRelaunchLog.info("MFWTransformLog : visitMethod : name'%s%s'", name, desc);
		
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		return pack.MethodAdapt(mv, name, desc);
	}
	
	public void visitEnd()
	{
		//if(cv != null && cv instanceof ClassWriter)pack.addMethod((ClassWriter)cv);
		super.visitEnd();
	}
}