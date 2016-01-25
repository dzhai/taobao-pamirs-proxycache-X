package com.taobao.pamirs.cache.load.verify;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.taobao.pamirs.cache.store.StoreType;

/**
 * 静态校验test
 * 
 * @author xiaocheng 2012-11-29
 */
public class StaticCheckTest {

	@Test
	public void testLegal() throws Exception {
		StaticCheck.check(getLegalObject());
	}

	@Test
	public void testIllegal() throws Exception {
		// notNull
		try {
			A a = getLegalObject();
			a.setAge(null);
			StaticCheck.check(a);

			assertThat(true, is(false));// must not here
		} catch (Exception e) {
		}

		// notEmptyList
		try {
			A a = getLegalObject();
			a.setMoneys(new ArrayList<String>());
			StaticCheck.check(a);

			assertThat(true, is(false));// must not here
		} catch (Exception e) {
		}

		// notEmpty
		try {
			A a = getLegalObject();
			a.setName("");
			StaticCheck.check(a);

			assertThat(true, is(false));// must not here
		} catch (Exception e) {
		}

		// isStoreType
		try {
			A a = getLegalObject();
			a.setStoreType("im tair");
			StaticCheck.check(a);

			assertThat(true, is(false));// must not here
		} catch (Exception e) {
		}

		// isStoreType
		try {
			A a = getLegalObject();
			a.setStoreType(StoreType.MAP.getName());
			a.setStoreTairNameSpace(null);
			StaticCheck.check(a);
		} catch (Exception e) {
			assertThat(true, is(false));// must not here
		}

		try {
			A a = getLegalObject();
			a.setStoreType(StoreType.TAIR.getName());
			a.setStoreTairNameSpace(null);
			StaticCheck.check(a);

			assertThat(true, is(false));// must not here
		} catch (Exception e) {
		}

	}

	private A getLegalObject() {
		A a = new A();
		a.setAge(29L);
		ArrayList<String> list = new ArrayList<String>();
		list.add("有钱");
		a.setMoneys(list);
		a.setName("jeck");
		a.setStoreType(StoreType.TAIR.getName());
		a.setStoreTairNameSpace(218);

		return a;
	}

	static class A {

		@Verfication(name = "名称", notEmpty = true, minlength = 2, maxlength = 30, regx = {
				"^[.a-zA-Z0-9_\u4e00-\u9fa5]+$", "只能由中文,英文,数字,点号,下划线组成" })
		private String name;

		@Verfication(name = "年龄", notNull = true)
		private Long age;

		@Verfication(name = "钱", notEmptyList = true)
		private List<String> moneys;

		@Verfication(name = "存储类型", isStoreType = true)
		private String storeType;

		@Verfication(name = "Tair命名空间", notNull = true, when = { StoreType.TAIR })
		private Integer storeTairNameSpace;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getAge() {
			return age;
		}

		public void setAge(Long age) {
			this.age = age;
		}

		public List<String> getMoneys() {
			return moneys;
		}

		public void setMoneys(List<String> moneys) {
			this.moneys = moneys;
		}

		public String getStoreType() {
			return storeType;
		}

		public void setStoreType(String storeType) {
			this.storeType = storeType;
		}

		public Integer getStoreTairNameSpace() {
			return storeTairNameSpace;
		}

		public void setStoreTairNameSpace(Integer storeTairNameSpace) {
			this.storeTairNameSpace = storeTairNameSpace;
		}

	}
}
