/**
 *
 */
package com.taobao.tair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Tair鐨勬帴鍙ｏ紝鏀寔鎸佷箙鍖栧瓨鍌ㄥ拰闈炴寔涔呭寲锛堝嵆cache锛夊瓨鍌�
 *
 * @author ruohai
 *
 */
public interface TairManager {

	/**
	 * 鑾峰彇鏁版嵁
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param key
	 *            瑕佽幏鍙栫殑鏁版嵁鐨刱ey
	 * @return
	 */
	Result<DataEntry> get(int namespace, Serializable key);

	/**
	 * 鎵归噺鑾峰彇鏁版嵁
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param keys
	 *            瑕佽幏鍙栫殑鏁版嵁鐨刱ey鍒楄〃
	 * @return 濡傛灉鎴愬姛锛岃繑鍥炵殑鏁版嵁瀵硅薄涓轰竴涓狹ap<Key, Value>
	 */
	Result<List<DataEntry>> mget(int namespace, List<? extends Object> keys);

	/**
	 * 璁剧疆鏁版嵁锛屽鏋滄暟鎹凡缁忓瓨鍦紝鍒欒鐩栵紝濡傛灉涓嶅瓨鍦紝鍒欐柊澧�濡傛灉鏄柊澧烇紝鍒欐湁鏁堟椂闂翠负0锛屽嵆涓嶅け鏁�濡傛灉鏄洿鏂帮紝鍒欎笉妫�煡鐗堟湰锛屽己鍒舵洿鏂�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param key
	 * @param value
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value);

	/**
	 * 璁剧疆鏁版嵁锛屽鏋滄暟鎹凡缁忓瓨鍦紝鍒欒鐩栵紝濡傛灉涓嶅瓨鍦紝鍒欐柊澧�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @param value
	 *            鏁版嵁鐨剉alue
	 * @param version
	 *            鏁版嵁鐨勭増鏈紝濡傛灉鍜岀郴缁熶腑鏁版嵁鐨勭増鏈笉涓�嚧锛屽垯鏇存柊澶辫触
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value,
			int version);

	/**
	 * 璁剧疆鏁版嵁锛屽鏋滄暟鎹凡缁忓瓨鍦紝鍒欒鐩栵紝濡傛灉涓嶅瓨鍦紝鍒欐柊澧�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @param value
	 *            鏁版嵁鐨剉alue
	 * @param version
	 *            鏁版嵁鐨勭増鏈紝濡傛灉鍜岀郴缁熶腑鏁版嵁鐨勭増鏈笉涓�嚧锛屽垯鏇存柊澶辫触
	 * @param expireTime
	 *            鏁版嵁鐨勬湁鏁堟椂闂达紝鍗曚綅涓虹
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value,
			int version, int expireTime);

	/**
	 * 鍒犻櫎key瀵瑰簲鐨勬暟鎹�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @return
	 */
	ResultCode delete(int namespace, Serializable key);

	/**
	 * 澶辨晥鏁版嵁锛岃鏂规硶灏嗗け鏁堢敱澶辨晥鏈嶅姟鍣ㄩ厤缃殑澶氫釜瀹炰緥涓綋鍓峠roup涓嬬殑鏁版嵁
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param key
	 *            瑕佸け鏁堢殑key
	 * @deprecated 璇风洿鎺ヤ娇鐢╠elete鎺ュ彛
	 * @return
	 */
	ResultCode invalid(int namespace, Serializable key);

	/**
	 * 鎵归噺澶辨晥鏁版嵁锛岃鏂规硶灏嗗け鏁堢敱澶辨晥鏈嶅姟鍣ㄩ厤缃殑澶氫釜瀹炰緥涓綋鍓峠roup涓嬬殑鏁版嵁
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param keys
	 *            瑕佸け鏁堢殑key鍒楄〃
	 * @deprecated 璇蜂娇鐢╩delete鎺ュ彛
	 * @return
	 */
	ResultCode minvalid(int namespace, List<? extends Object> keys);

	/**
	 * 鎵归噺鍒犻櫎锛屽鏋滃叏閮ㄥ垹闄ゆ垚鍔燂紝杩斿洖鎴愬姛锛屽惁鍒欒繑鍥炲け璐�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amespace
	 * @param keys
	 *            瑕佸垹闄ゆ暟鎹殑key鍒楄〃
	 * @return
	 */
	ResultCode mdelete(int namespace, List<? extends Object> keys);

	/**
	 * 灏唊ey瀵瑰簲鐨勬暟鎹姞涓妚alue锛屽鏋渒ey瀵瑰簲鐨勬暟鎹笉瀛樺湪锛屽垯鏂板锛屽苟灏嗗�璁剧疆涓篸efaultValue
	 * 濡傛灉key瀵瑰簲鐨勬暟鎹笉鏄痠nt鍨嬶紝鍒欒繑鍥炲け璐�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amspace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @param value
	 *            瑕佸姞鐨勫�
	 * @param defaultValue
	 *            涓嶅瓨鍦ㄦ椂鐨勯粯璁ゅ�
	 * @return 鏇存柊鍚庣殑鍊�
	 */
	Result<Integer> incr(int namespace, Serializable key, int value,
			int defaultValue, int expireTime);

	/**
	 * 灏唊ey瀵瑰簲鐨勬暟鎹噺鍘籿alue锛屽鏋渒ey瀵瑰簲鐨勬暟鎹笉瀛樺湪锛屽垯鏂板锛屽苟灏嗗�璁剧疆涓篸efaultValue
	 * 濡傛灉key瀵瑰簲鐨勬暟鎹笉鏄痠nt鍨嬶紝鍒欒繑鍥炲け璐�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amspace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @param value
	 *            瑕佸噺鍘荤殑鍊�
	 * @param defaultValue
	 *            涓嶅瓨鍦ㄦ椂鐨勯粯璁ゅ�
	 * @return 鏇存柊鍚庣殑鍊�
	 */
	Result<Integer> decr(int namespace, Serializable key, int value,
			int defaultValue, int expireTime);

	/**
	 * 灏唊ey瀵瑰簲鐨勮鏁拌缃垚count锛屽拷鐣ey鍘熸潵鏄惁瀛樺湪浠ュ強鏄惁鏄鏁扮被鍨嬨�
	 * 鍥犱负Tair涓鏁扮殑鏁版嵁鏈夌壒鍒爣蹇楋紝鎵�互涓嶈兘鐩存帴浣跨敤put璁剧疆璁℃暟鍊笺�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amspace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @param count
	 *            瑕佽缃殑鍊�
	 */
	ResultCode setCount(int namespace, Serializable key, int count);

	/**
	 * 灏唊ey瀵瑰簲鐨勮鏁拌缃垚count锛屽拷鐣ey鍘熸潵鏄惁瀛樺湪浠ュ強鏄惁鏄鏁扮被鍨嬨�
	 * 鍥犱负Tair涓鏁扮殑鏁版嵁鏈夌壒鍒爣蹇楋紝鎵�互涓嶈兘鐩存帴浣跨敤put璁剧疆璁℃暟鍊笺�
	 *
	 * @param namespace
	 *            鏁版嵁鎵�湪鐨刵amspace
	 * @param key
	 *            鏁版嵁鐨刱ey
	 * @param count
	 *            瑕佽缃殑鍊�
	 * @param version
	 *            鐗堟湰锛屼笉鍏冲績骞跺彂锛屼紶鍏�
	 * @param expireTime
	 *            杩囨湡鏃堕棿锛屼笉浣跨敤浼犲叆0
	 */
	ResultCode setCount(int namespace, Serializable key, int count, int version, int expireTime);

	/**
	 * 澧炲姞闆嗗悎鏁版嵁绫诲瀷锛屽鏋滃師闆嗗悎鏁版嵁涓嶅瓨鍦紝鍒欐墽琛宨nsert鎿嶄綔
	 * @param namespace 鏁版嵁鎵�湪鐨刵amespace
	 * @param key 鏁版嵁鐨刱ey
	 * @param items 瑕佸鍔犵殑value锛屽綋鍓嶅�鎺ュ彈鍩烘湰绫诲瀷锛岃鎯呭弬瑙丣son.checkType
	 * @param maxCount 闆嗗悎鍏佽鐨勬渶澶ф潯鐩暟閲忥紝瓒呰繃杩欎釜鏁伴噺锛岀郴缁熷皢鐩存帴鍒犻櫎鐩稿簲鏁伴噺鐨勬渶鏃╂斁鍏ョ殑鏉＄洰
	 * @param version 鐗堟湰鍙凤紝濡傛灉闈�锛屽綋浼犲叆鐨勭増鏈彿鍜岀郴缁熶腑鐨勭増鏈彿涓嶅悓鏃讹紝杩斿洖鐗堟湰閿欒
	 * @param expireTime 瓒呮椂鏃堕棿
	 * @return 杩斿洖浠ｇ爜
	 */
	ResultCode addItems(int namespace, Serializable key,
			List<? extends Object> items, int maxCount, int version,
			int expireTime);

	/**
	 * 鑾峰彇闆嗗悎鏁版嵁
	 * @param namespace 鏁版嵁鎵�湪鐨刵amespace
	 * @param key 鏁版嵁鐨刱ey
	 * @param offset 瑕佽幏鍙栫殑鏁版嵁鐨勫亸绉婚噺
	 * @param count 瑕佽幏鍙栫殑鏁版嵁鐨勬潯鏁�
	 * @return 濡傛灉鏁版嵁涓嶅瓨鍦紝杩斿洖DATANOTEXIST锛屽惁鍒欐垚鍔熻繑鍥炵浉搴旂殑鏉℃暟锛屽け璐ヨ繑鍥炵浉搴旂殑閿欒浠ｇ爜
	 */
	Result<DataEntry> getItems(int namespace, Serializable key,
			int offset, int count);

	/**
	 * 鍒犻櫎闆嗗悎涓殑鏁版嵁
	 * @param namespace 鏁版嵁鎵�湪鐨刵amespace
	 * @param key 鏁版嵁鐨刱ey
	 * @param offset 瑕佸垹闄ょ殑鏁版嵁鐨勫亸绉婚噺
	 * @param count 瑕佸垹闄ょ殑鏁版嵁鐨勬潯鏁�
	 * @return 鍒犻櫎鏄惁鎴愬姛
	 */
	ResultCode removeItems(int namespace, Serializable key, int offset,
			int count);

	/**
	 * 鍒犻櫎骞惰繑鍥為泦鍚堜腑鐨勬暟鎹�
	 * @param namespace 鏁版嵁鎵�湪鐨刵amespace
	 * @param key 鏁版嵁鐨刱ey
	 * @param offset 瑕佸垹闄ょ殑鏁版嵁鐨勫亸绉婚噺
	 * @param count 瑕佸垹闄ょ殑鏁版嵁鐨勬潯鏁�
	 * @return 濡傛灉鍒犻櫎鎴愬姛锛岃繑鍥炴湰娆″垹闄ゆ垚鍔熷垹闄ょ殑鏁版嵁
	 */
	Result<DataEntry> getAndRemove(int namespace,
			Serializable key, int offset, int count);

	/**
	 * 鑾峰彇key瀵瑰簲鐨勯泦鍚堜腑鐨勬潯鐩暟閲�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param key  鏁版嵁鐨刱ey
	 * @return 濡傛灉鏁版嵁涓嶅瓨鍦紝杩斿洖涓嶅瓨鍦紱鍚﹀垯鎴愬姛杩斿洖闆嗗悎鐨勬潯鐩暟閲忥紝澶辫触杩斿洖鐩稿簲鐨勯敊璇唬鐮�
	 */
	Result<Integer> getItemCount(int namespace, Serializable key);


	/**
	 * 閿佷綇涓�釜key锛屼笉鍐嶅厑璁告洿鏂� 鍏佽璇诲拰鍒犻櫎銆�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param key  鏁版嵁鐨刱ey
	 * @return 濡傛灉鏁版嵁涓嶅瓨鍦紝杩斿洖涓嶅瓨鍦紱濡傛灉鏁版嵁瀛樺湪浣嗗凡缁忚lock锛岃繑鍥瀕ock宸茬粡瀛樺湪鐨勯敊璇爜锛�
	 *         鍚﹀垯鎴愬姛銆�
	 */
	ResultCode lock(int namespace, Serializable key);


	/**
	 * 瑙ｉ攣涓�釜key銆�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param key  鏁版嵁鐨刱ey
	 * @return 濡傛灉鏁版嵁涓嶅瓨鍦紝杩斿洖涓嶅瓨鍦紱濡傛灉鏁版嵁瀛樺湪浣嗘湭琚玪ock锛岃繑鍥瀕ock涓嶅瓨鍦ㄧ殑閿欒鐮侊紱
	 *         鍚﹀垯鎴愬姛銆�
	 */
	ResultCode unlock(int namespace, Serializable key);

	/**
	 * 鎵归噺閿乲ey銆�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param keys  鏁版嵁鐨刱ey
	 * @return Result.getRc()鏄繑鍥炵殑ResultCode, 濡傛灉閮芥垚鍔� 杩斿洖鎴愬姛锛�
	 *         濡傛灉杩斿洖PARTSUCC, 鍒橰esult.getValue()涓烘垚鍔熺殑key.
	 */
	Result<List<Object>> mlock(int namespace, List<? extends Object> keys);

	/**
	 * 鎵归噺閿乲ey銆�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param keys  鏁版嵁鐨刱ey
	 * @param failKeysMap 浼犲叆淇濆瓨澶辫触鐨刱ey
	 * @return Result.getRc()鏄繑鍥炵殑ResultCode, 濡傛灉閮芥垚鍔� 杩斿洖鎴愬姛锛�
	 *         濡傛灉杩斿洖PARTSUCC, 鍒橰esult.getValue()涓烘垚鍔熺殑key,骞朵笖濡傛灉浼犲叆failKeysMap涓嶄负null锛�
	 *         failKeysMap涓哄け璐ョ殑key浠ュ強瀵瑰簲鐨勯敊璇爜銆�
	 */
	Result<List<Object>> mlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap);

	/**
	 * 鎵归噺瑙ｉ攣key銆�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param keys  鏁版嵁鐨刱ey
	 * @return Result.getRc()鏄繑鍥炵殑ResultCode, 濡傛灉閮芥垚鍔� 杩斿洖鎴愬姛锛�
	 *         濡傛灉杩斿洖PARTSUCC, 鍒橰esult.getValue()涓烘垚鍔熺殑key.
	 */
	Result<List<Object>> munlock(int namespace, List<? extends Object> keys);

	/**
	 * 鎵归噺瑙ｉ攣key銆�
	 * @param namespace  鏁版嵁鎵�湪鐨刵amespace
	 * @param keys  鏁版嵁鐨刱ey
	 * @param failKeysMap 浼犲叆淇濆瓨澶辫触鐨刱ey
	 * @return Result.getRc()鏄繑鍥炵殑ResultCode, 濡傛灉閮芥垚鍔� 杩斿洖鎴愬姛锛�
	 *         濡傛灉杩斿洖PARTSUCC, 鍒橰esult.getValue()涓烘垚鍔熺殑key,骞朵笖濡傛灉浼犲叆failKeysMap涓嶄负null锛�
	 *         failKeysMap涓哄け璐ョ殑key浠ュ強瀵瑰簲鐨勯敊璇爜銆�
	 */
	Result<List<Object>> munlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap);

	/**
	 * 寰楀埌缁熻淇℃伅
	 * @param qtype 缁熻绫诲瀷
	 * @param groupName 缁熻鐨刧roup name
	 * @param serverId 缁熻鐨勬湇鍔″櫒
	 * @return 缁熻鐨�缁撴灉:缁熻椤瑰拰缁熻鍊煎
	 */
	Map<String,String> getStat(int qtype, String groupName, long serverId);


	/**
	 * 鑾峰彇瀹㈡埛绔殑鐗堟湰
	 */
	String getVersion();
}
