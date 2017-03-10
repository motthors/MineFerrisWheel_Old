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
	
	// 改変対象のクラスの完全修飾名
	private static final String TARGET_CLASS_NAME = "net.minecraft.client.renderer.EntityRenderer";
	static int counter = 0;
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) 
	{
		try{
			
			////////// 各クラスが順番に呼ばれてくる //////////
			//if(transformedName.contains("net.minecraft.world.World")) 
			//	log("TransformLog : transform : name:"+name+" :: Tname:"+transformedName+":side:"+Thread.currentThread().getName());
			
			// 今流れてきているクラスが変換対象として登録されていたらそのpackを持ってくる
			TransPack pack = transformerMap.getInstance().getTransPack(transformedName);
			
			// packが無ければそのまま帰る
			if( pack == null ) return bytes;

			pack.nowTarget = transformedName;
			
			ClassReader cr = new ClassReader(bytes); 			// byte配列を読み込み、利用しやすい形にする。
			ClassWriter cw = new ClassWriter(cr, 2); 			// これのvisitを呼ぶことによって情報が溜まっていく。
			ClassVisitor ca = new _2_classAdapter(cw, pack); 	// Adapterを通して書き換え出来るようにする。
			
			// ClassReaderが、受け取ったClassAdapterの各Visitメソッドを元のクラスと同様の順番で呼ぶ
			cr.accept(ca, 0); 							
			
			pack.addMember(cw);
			
			// Writer内の情報をbyte配列にして返す。
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
//		 * メソッドについて呼ばれる。
//		 * 
//		 * @param access  {@link Opcodes}に載ってるやつ。publicとかstaticとかの状態がわかる。
//		 * @param name	メソッドの名前。
//		 * @param desc メソッドの(引数と返り値を合わせた)型。
//		 * @param signature   ジェネリック部分を含むメソッドの(引数と返り値を合わせた)型。ジェネリック付きでなければおそらくnull。
//		 * @param exceptions  throws句にかかれているクラスが列挙される。Lと;で囲われていないので  {@link String#replace(char, char)}で'/'と'.'を置換してやればOK。
//		 * @return ここで返したMethodVisitorのメソッド群が適応される。  ClassWriterがセットされていればMethodWriterがsuperから降りてくる。
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
//			// ターゲット関数かどうかを判定し、
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
		 * int型変数等の操作時に呼ばれる。
		 * 
		 * @param opcode   byteの範囲で扱えるならBIPUSH、shortの範囲で扱えるならSIPUSHが入っている。
		 * @param operand    shortの範囲に収まる値が入っている。
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
	 * 書き換え対象かどうかを判定する。今回はClass名のみで。
	 */
//	private boolean accept(String className) {
//		return TARGET_CLASS_NAME.equals(className);
//	}

	/**
	 * クラスの名前を難読化(obfuscation)する。
	 */
	public static String unmapClassName(String name) {
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
	}

	/**
	 * メソッドの名前を易読化(deobfuscation)する。
	 */
	public static String mapMethodName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(owner), methodName, desc);
	}

	/**
	 * フィールドの名前を易読化(deobfuscation)する。
	 */
	public static String mapFieldName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(unmapClassName(owner), methodName, desc);
	}

	/**
	 * 下の{@link #toDesc(Object)}をMethodのDescriptor用に使えるようにしたもの。
	 * 下手なクラスをここに入れようとするとまずいので確信がない限りStringで入れるべき。
	 * 
	 * @param returnType
	 *            {@link String}型か、{@link Class}型で目的のMethodの返り値の型を指定する。
	 * @param rawDesc
	 *            {@link String}型か、{@link Class}型でMethodの引数たちの型を指定する。
	 * @throws IllegalArgumentException
	 *             引数に{@link String}型か、{@link Class}型以外が入ったら投げられる。
	 * @return Javaバイトコードで扱われる形の文字列に変換されたDescriptor。
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
	 * {@link Class#forName}とか{@link Class#getCanonicalName()}
	 * したりするとまだ読み込まれてなかったりしてまずいので安全策。
	 * 下手なクラスをここに入れようとするとまずいので確信がない限りStringで入れるべき。
	 * 
	 * @param raw
	 *            {@link String}型か、{@link Class}型でASM用の文字列に変換したいクラスを指定する。
	 * @throws IllegalArgumentException
	 *             {@param raw}に{@link String}型か、{@link Class}型以外が入ったら投げられる。
	 * @return Javaバイトコードで扱われる形の文字列に変換されたクラス。
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
