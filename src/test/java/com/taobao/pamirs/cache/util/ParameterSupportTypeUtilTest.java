package com.taobao.pamirs.cache.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static com.taobao.pamirs.cache.util.ParameterSupportTypeUtil.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * 框架支持的参数类型--测试类
 * 
 * @author xiaocheng 2012-11-22
 */
public class ParameterSupportTypeUtilTest {

	@Test
	public void testIsSupportParameterTypes() throws Exception {
		// 18种
		assertThat(isSupportParameterTypes(boolean.class), is(true));
		assertThat(isSupportParameterTypes(Boolean.class), is(true));
		assertThat(isSupportParameterTypes(char.class), is(true));
		assertThat(isSupportParameterTypes(Character.class), is(true));
		assertThat(isSupportParameterTypes(byte.class), is(true));
		assertThat(isSupportParameterTypes(Byte.class), is(true));
		assertThat(isSupportParameterTypes(short.class), is(true));
		assertThat(isSupportParameterTypes(Short.class), is(true));
		assertThat(isSupportParameterTypes(int.class), is(true));
		assertThat(isSupportParameterTypes(Integer.class), is(true));
		assertThat(isSupportParameterTypes(long.class), is(true));
		assertThat(isSupportParameterTypes(Long.class), is(true));
		assertThat(isSupportParameterTypes(float.class), is(true));
		assertThat(isSupportParameterTypes(Float.class), is(true));
		assertThat(isSupportParameterTypes(double.class), is(true));
		assertThat(isSupportParameterTypes(Double.class), is(true));
		assertThat(isSupportParameterTypes(Date.class), is(true));
		assertThat(isSupportParameterTypes(String.class), is(true));

		//
		assertThat(isSupportParameterTypes(ParameterSupportTypeUtilTest.class),
				is(false));
	}

	@Test
	public void testValueConvertToType() throws Exception {
		assertThat(true, equalTo(valueConvertToType("true", boolean.class)));
		assertThat(Boolean.TRUE,
				equalTo(valueConvertToType("true", Boolean.class)));

		assertThat('a', equalTo(valueConvertToType("a", char.class)));
		assertThat(Character.valueOf('a'),
				equalTo(valueConvertToType("a", Character.class)));

		assertThat((byte) 16, equalTo(valueConvertToType("16", byte.class)));
		assertThat(Byte.valueOf((byte) 16),
				equalTo(valueConvertToType("16", Byte.class)));

		assertThat((short) 16, equalTo(valueConvertToType("16", short.class)));
		assertThat(Short.valueOf((short) 16),
				equalTo(valueConvertToType("16", Short.class)));

		assertThat(16, equalTo(valueConvertToType("16", int.class)));
		assertThat(Integer.valueOf(16),
				equalTo(valueConvertToType("16", Integer.class)));

		assertThat(16L, equalTo(valueConvertToType("16", long.class)));
		assertThat(Long.valueOf(16L),
				equalTo(valueConvertToType("16", Long.class)));

		assertThat(1.23F, equalTo(valueConvertToType("1.23", float.class)));
		assertThat(Float.valueOf(1.23F),
				equalTo(valueConvertToType("1.23", Float.class)));

		assertThat(1.23D, equalTo(valueConvertToType("1.23", double.class)));
		assertThat(Double.valueOf(1.23D),
				equalTo(valueConvertToType("1.23", Double.class)));

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = format.parse("2012-11-22 10:57:00");
		assertThat(date,
				equalTo(valueConvertToType("2012-11-22 10:57:00", Date.class)));

		assertThat("abc", equalTo(valueConvertToType("abc", String.class)));

	}

}
