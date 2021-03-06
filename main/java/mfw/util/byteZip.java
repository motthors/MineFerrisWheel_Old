package mfw.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import mfw._core.MFW_Logger;

public class byteZip {

//	ArrayDeque<Byte> orgByteDeque;
//	byte[] byteArray;
	int index;
	int outSize;
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ByteArrayOutputStream orgByteArray = new ByteArrayOutputStream();
	
	public byteZip()
	{
//		byteArray = new ArrayDeque<Byte>();
//		byteArray = new byte[size];
		orgByteArray.reset();
		index = 0;
	}
	
	public void clear()
	{
		bos.reset();
		index = 0;
	}
	
//	public void setSize(int size)
//	{
//		byteArray = new byte[size];
//		index = 0;
//		bos.reset();
//	}
	
	public void setByte(byte data)
	{
//		byteArray[index++] = (data);
		orgByteArray.write(data);
	}
	
	public void setByteArray(byte[] ba)
	{
		orgByteArray.write(ba,0,ba.length);
//		for(byte b : ba)byteArray[index++] = (b);
	}
	
	public void setShort(short value)
	{
        int arraySize = Short.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        setByteArray(buffer.putShort(value).array() );
    }
	
	public void setInt(int value)
	{
		int arraySize = Integer.SIZE / Byte.SIZE;
		ByteBuffer buffer = ByteBuffer.allocate(arraySize);
		setByteArray( buffer.putInt(value).array() );
	}
	
	public void setFloat(float value)
	{
		int arraySize = Float.SIZE / Byte.SIZE;
		ByteBuffer buffer = ByteBuffer.allocate(arraySize);
		setByteArray( buffer.putFloat(value).array() );
	}
	
	public void compress()
	{
		Deflater compresser = new Deflater();
//		compresser.setInput(byteArray);
		compresser.setInput(orgByteArray.toByteArray());
		compresser.finish();
		byte[] outBuf = new byte[orgByteArray.size()];
		outSize = compresser.deflate(outBuf);
		bos.write(outBuf, 0, outSize);
		compresser.end();
//		MFW_Logger.debugInfo("compress! : "+orgByteArray.size()+"byte -> "+outSize+"byte");
	}
	
	public static int decompress(byte[] out, byte[] in) throws DataFormatException
	{
		int outnum = -1;
		final Inflater decompresser = new Inflater();
		try
		{
			decompresser.setInput(in);
			outnum = decompresser.inflate(out);
		}
		catch(DataFormatException e)
		{
			MFW_Logger.warn("failed to decompress data. : "+e.toString());
		}
		finally
		{
            decompresser.end();
        }
		return outnum;
	}
	
	public byte[] getOutput()
	{
		return bos.toByteArray();
	}
	public int getOutputLength()
	{
		return outSize;
	}
	public int getOrgSize()
	{
		return orgByteArray.size();
	}
}
