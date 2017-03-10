package mfw.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import mfw.asm.transpack.TransPack;
import net.minecraft.launchwrapper.IClassTransformer;

public class classTransformer implements IClassTransformer {

	public void log(String str)
	{
		FMLRelaunchLog.info(str);
	}
	
	// ���ϑΏۂ̃N���X�̊��S�C����
	private static final String TARGET_CLASS_NAME = "net.minecraft.client.renderer.EntityRenderer";
	static int counter = 0;
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) 
	{
		try{
			
			////////// �e�N���X�����ԂɌĂ΂�Ă��� //////////
			//if(transformedName.contains("net.minecraft.world.World")) 
			//	log("TransformLog : transform : name:"+name+" :: Tname:"+transformedName+":side:"+Thread.currentThread().getName());
			
			// ������Ă��Ă���N���X���ϊ��ΏۂƂ��ēo�^����Ă����炻��pack�������Ă���
			TransPack pack = transformerMap.getInstance().getTransPack(transformedName);
			
			// pack��������΂��̂܂܋A��
			if( pack == null ) return bytes;

			pack.nowTarget = transformedName;
			
			ClassReader cr = new ClassReader(bytes); 			// byte�z���ǂݍ��݁A���p���₷���`�ɂ���B
			ClassWriter cw = new ClassWriter(cr, 2); 			// �����visit���ĂԂ��Ƃɂ���ď�񂪗��܂��Ă����B
			ClassVisitor ca = new _2_classAdapter(cw, pack); 	// Adapter��ʂ��ď��������o����悤�ɂ���B
			
			// ClassReader���A�󂯎����ClassAdapter�̊eVisit���\�b�h�����̃N���X�Ɠ��l�̏��ԂŌĂ�
			cr.accept(ca, 0); 							
			
			pack.addMember(cw);
			
			// Writer���̏���byte�z��ɂ��ĕԂ��B
			return cw.toByteArray();
			
		}
		catch(ClassCircularityError e)
		{
			return bytes;
		}
		catch(NullPointerException e)
		{
			return bytes;
		}
	}

//	public static class ClassAdapter extends ClassVisitor 
//	{
//		public ClassAdapter(ClassVisitor cv)
//		{
//			super(ASM5, cv);
//		}
//
//		/**
//		 * ���\�b�h�ɂ��ČĂ΂��B
//		 * 
//		 * @param access  {@link Opcodes}�ɍڂ��Ă��Bpublic�Ƃ�static�Ƃ��̏�Ԃ��킩��B
//		 * @param name	���\�b�h�̖��O�B
//		 * @param desc ���\�b�h��(�����ƕԂ�l�����킹��)�^�B
//		 * @param signature   �W�F�l���b�N�������܂ރ��\�b�h��(�����ƕԂ�l�����킹��)�^�B�W�F�l���b�N�t���łȂ���΂����炭null�B
//		 * @param exceptions  throws��ɂ�����Ă���N���X���񋓂����BL��;�ň͂��Ă��Ȃ��̂�  {@link String#replace(char, char)}��'/'��'.'��u�����Ă���OK�B
//		 * @return �����ŕԂ���MethodVisitor�̃��\�b�h�Q���K�������B  ClassWriter���Z�b�g����Ă����MethodWriter��super����~��Ă���B
//		 */
//		private static final String TARGET_TRANSFORMED_NAME = "func_78471_a";
//		private static final String TARGET_Original_NAME = "renderWorld";
//		private static final String TARGET_DESC = "(FJ)V";
//		
//		private static final String TARGET_TRANSFORMED_NAME_renderdist = "func_78479_a";
//		private static final String TARGET_Original_NAME_renderdist = "setupCameraTransform";
//		private static final String TARGET_DESC_renderdist = "(FI)V";
//		@Override
//		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
//		{
//			
//			// �^�[�Q�b�g�֐����ǂ����𔻒肵�A
////			if (TARGET_TRANSFORMED_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc))
////					&& toDesc(int.class, "net.minecraft.item.ItemStack").equals(desc)) 
//			FMLRelaunchLog.info("MFWTransformLog : visitMethod : name'%s%s'", name, desc);
//			boolean flag = false;
//			flag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
//			flag |= TARGET_Original_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
//			if(flag && TARGET_DESC.equals(desc))
//			{
//				return new MethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
//			}
//			
//			flag = false;
//			flag |= TARGET_TRANSFORMED_NAME_renderdist.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
//			flag |= TARGET_Original_NAME_renderdist.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
//			if(flag && TARGET_DESC_renderdist.equals(desc))
//			{
//				return new MethodAdapter_setupCameraTransform(super.visitMethod(access, name, desc, signature, exceptions));
//			}
//			
////			flag = false;
////			flag |= "a".equals(name);
////			flag |= "setOptionFloatValue".equals(mapMethodName("net.minecraft.client.settings.GameSettings", name, desc));
////			if(flag &&
////					("(Lnet/minecraft/client/settings/GameSettings$Options;F)V".equals(desc)
////							|| "(Lbbm;F)V".equals(desc)))
////			{
////				return new MethodAdapter_DoubleChunk(super.visitMethod(access, name, desc, signature, exceptions));
////			}
//			
//			return super.visitMethod(access, name, desc, signature, exceptions);
//		}
//	}

	
	public static class MethodAdapter extends MethodVisitor {
		public MethodAdapter(MethodVisitor mv) 
		{
			super(ASM5, mv);
		}

		/**
		 * int�^�ϐ����̑��쎞�ɌĂ΂��B
		 * 
		 * @param opcode   byte�͈̔͂ň�����Ȃ�BIPUSH�Ashort�͈̔͂ň�����Ȃ�SIPUSH�������Ă���B
		 * @param operand    short�͈̔͂Ɏ��܂�l�������Ă���B
		 */
		public static int MethodCount = 0;
		private static final String TARGET_CLASS_NAME = "net/minecraft/client/renderer/RenderGlobal";//bma
//		private static final String TARGET_CLASS_NAME = "bma";
		private static final String TARGET_TRANSFORMED_NAME = "renderAllSortedRenderers"; 
		private static final String TARGET_Orginal_NAME = "sortAndRender";
		private static final String TARGET_DESC = "(Lnet/minecraft/entity/EntityLivingBase;ID)I";
		private static final String TARGET_DESC_T = "(ID)I";
//		private static final String TARGET_TRANSFORMED_NAME = "renderEntities"; 
//		private static final String TARGET_Orginal_NAME = "sortAndRender";
//		private static final String TARGET_DESC = "(Lnet/minecraft/entity/EntityLivingBase;ID)I"; //name'bma.a(Lsv;ID)I'
//		private static final String TARGET_DESC_T = "(ID)I";
		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
	    {
//			FMLRelaunchLog.info("MFWTransformLog : visitMethodInsn : name'%s.%s%s'", owner, name, desc);
			boolean flag = false;
			flag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(owner, name, desc));
			flag |= TARGET_Orginal_NAME.equals(mapMethodName(owner, name, desc));
			if (TARGET_CLASS_NAME.equals(owner) 
					&& flag 
					&& (TARGET_DESC.equals(desc) || (TARGET_DESC_T.equals(desc))))
			{
				MethodCount += 1;
				switch(MethodCount)
				{
				case 2: 
				case 3:
				case 4:
					this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
//					if(Loader.isModLoaded("shadersmod"))
					//	this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "shadersmodcore/client/Shaders", "beginWater", "()V", false);
					this.mv.visitVarInsn(Opcodes.FLOAD, 1);
					this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mfw/asm/renderPass1Hook", "draw", "(F)V", false);
//					if(Loader.isModLoaded("shadersmod"))
					//	this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "shadersmodcore/client/Shaders", "endWater", "()V", false); 
					FMLRelaunchLog.info("MFWTransformLog : succeed transforming");
					return;
				}
			}
			super.visitMethodInsn(opcode, owner, name, desc, itf);
	    }
	}
	
	public static class MethodAdapter_setupCameraTransform extends MethodVisitor {
		public MethodAdapter_setupCameraTransform(MethodVisitor mv) 
		{
			super(ASM5, mv);
		}

		public static int MethodCount = 0;

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//			FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : "+name +".."+mapFieldName(owner, name, desc)+desc);
			boolean flag = false;
			flag |= "farPlaneDistance".equals(mapFieldName(owner, name, desc));
			flag |= "field_78530_s".equals(mapFieldName(owner, name, desc));
			if(flag && opcode == Opcodes.PUTFIELD && MethodCount==0)
			{
				MethodCount += 1;
				mv.visitFieldInsn(Opcodes.GETSTATIC, "mfw/_core/MFW_Command", "renderDistRatio", "F");
				mv.visitInsn(Opcodes.FMUL);
//				FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : transform renderdist");
			}
			super.visitFieldInsn(opcode, owner, name, desc);
		}
		
	}
	
//	public static class MethodAdapter_DoubleChunk extends MethodVisitor {
//		public MethodAdapter_DoubleChunk(MethodVisitor mv) 
//		{
//			super(ASM5, mv);
//		}
//
//		public static int MethodCount = 0;
//
//		@Override
//		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
////			FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : "+name +".."+mapFieldName(owner, name, desc)+desc);
//			boolean flag = false;
//			flag |= "renderDistanceChunks".equals(mapFieldName(owner, name, desc));
//			flag |= "field_151451_c".equals(mapFieldName(owner, name, desc));
//			if(flag && opcode == Opcodes.PUTFIELD && MethodCount==0)
//			{
//				MethodCount += 1;
//				mv.visitFieldInsn(Opcodes.GETSTATIC, "mfw/_core/MFW_Command", "chunkRatio", "I");
//				mv.visitInsn(Opcodes.IMUL);
////				FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : transform renderdist");
//			}
//			super.visitFieldInsn(opcode, owner, name, desc);
//		}
//	}
	
	
//	public static class ClassAdapter2 extends ClassVisitor 
//	{
//		public ClassAdapter2(ClassVisitor cv)
//		{
//			super(ASM5, cv);
//		}
//
//		@Override
//		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
//		{
//			boolean flag = false;
//			flag |= "<init>".equals(mapMethodName("net.minecraft.client.renderer.RenderGlobal", name, desc));
//			flag |= "bma".equals(mapMethodName("net.minecraft.client.renderer.RenderGlobal", name, desc));
//			flag |= "bma".equals(name);
//			if(flag && ("(Lnet/minecraft/client/Minecraft;)V".equals(desc) || "bao".equals(desc)))
//			{
//				return new MethodAdapter_DoubleChunk2(super.visitMethod(access, name, desc, signature, exceptions));
//			}
//			
//			return super.visitMethod(access, name, desc, signature, exceptions);
//		}
//	}
//	public static class MethodAdapter_DoubleChunk2 extends MethodVisitor {
//		public MethodAdapter_DoubleChunk2(MethodVisitor mv) 
//		{
//			super(ASM5, mv);
//		}
//
//		public static int MethodCount = 0;
//		 
//		@Override
//		public void visitIntInsn(int opcode, int data) {
////			FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : "+name +".."+mapFieldName(owner, name, desc)+desc);
//			if(data == 34 || data == 65/* && MethodCount==0*/)
//			{
//				MethodCount += 1;
//				super.visitIntInsn(opcode, data*MFW_Command.chunkRatio);
//			}
//			else 
//				super.visitIntInsn(opcode, data);
//		}
//		
//	}
	
	
	/**
	 * ���������Ώۂ��ǂ����𔻒肷��B�����Class���݂̂ŁB
	 */
//	private boolean accept(String className) {
//		return TARGET_CLASS_NAME.equals(className);
//	}

	/**
	 * �N���X�̖��O���ǉ�(obfuscation)����B
	 */
	public static String unmapClassName(String name) {
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
	}

	/**
	 * ���\�b�h�̖��O���Փǉ�(deobfuscation)����B
	 */
	public static String mapMethodName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(owner), methodName, desc);
	}

	/**
	 * �t�B�[���h�̖��O���Փǉ�(deobfuscation)����B
	 */
	public static String mapFieldName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(unmapClassName(owner), methodName, desc);
	}

	/**
	 * ����{@link #toDesc(Object)}��Method��Descriptor�p�Ɏg����悤�ɂ������́B
	 * ����ȃN���X�������ɓ���悤�Ƃ���Ƃ܂����̂Ŋm�M���Ȃ�����String�œ����ׂ��B
	 * 
	 * @param returnType
	 *            {@link String}�^���A{@link Class}�^�ŖړI��Method�̕Ԃ�l�̌^���w�肷��B
	 * @param rawDesc
	 *            {@link String}�^���A{@link Class}�^��Method�̈��������̌^���w�肷��B
	 * @throws IllegalArgumentException
	 *             ������{@link String}�^���A{@link Class}�^�ȊO���������瓊������B
	 * @return Java�o�C�g�R�[�h�ň�����`�̕�����ɕϊ����ꂽDescriptor�B
	 */
	public static String toDesc(Object returnType, Object... rawDesc) {
		StringBuilder sb = new StringBuilder("(");
		for (Object o : rawDesc) {
			sb.append(toDesc(o));
		}
		sb.append(')');
		sb.append(toDesc(returnType));
		return sb.toString();
	}

	/**
	 * {@link Class#forName}�Ƃ�{@link Class#getCanonicalName()}
	 * �����肷��Ƃ܂��ǂݍ��܂�ĂȂ������肵�Ă܂����̂ň��S��B
	 * ����ȃN���X�������ɓ���悤�Ƃ���Ƃ܂����̂Ŋm�M���Ȃ�����String�œ����ׂ��B
	 * 
	 * @param raw
	 *            {@link String}�^���A{@link Class}�^��ASM�p�̕�����ɕϊ��������N���X���w�肷��B
	 * @throws IllegalArgumentException
	 *             {@param raw}��{@link String}�^���A{@link Class}�^�ȊO���������瓊������B
	 * @return Java�o�C�g�R�[�h�ň�����`�̕�����ɕϊ����ꂽ�N���X�B
	 */
	public static String toDesc(Object raw) {
		if (raw instanceof Class) {
			Class<?> clazz = (Class<?>) raw;
			return Type.getDescriptor(clazz);
		} else if (raw instanceof String) {
			String desc = (String) raw;
			desc = desc.replace('.', '/');
			desc = desc.matches("L.+;") ? desc : "L" + desc + ";";
			return desc;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
