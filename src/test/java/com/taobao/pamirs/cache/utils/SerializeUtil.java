package com.taobao.pamirs.cache.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * 序列化 反序列化工具，用于测试基于jdk
 *
 */

public class SerializeUtil {

	/**
	 * 序列化
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object value) {
		return javaSerialize(value);
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {
		return javaUnserialize(bytes);
	}

	/**
	 * JAVA序列化
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] javaSerialize(Object value) {
		if (value == null) {
			throw new NullPointerException("Can't serialize null");
		}
		byte[] rv = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(value);
			os.close();
			bos.close();
			rv = bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		} finally {
			try {
				if (os != null)
					os.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				throw new IllegalArgumentException("Non-serializable object", e);
			}
		}
		return rv;
	}

	/**
	 * JAVA反序列化
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object javaUnserialize(byte[] bytes) {
		Object rv = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream is = null;
		try {
			if (bytes != null) {
				bis = new ByteArrayInputStream(bytes);
				is = new ObjectInputStream(bis);
				rv = is.readObject();
				is.close();
				bis.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Non-unserialize object", e);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Non-unserialize object", e);
		} finally {
			try {
				if (is != null)
					is.close();
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				throw new IllegalArgumentException("Non-unserialize object", e);
			}
		}
		return rv;
	}
}