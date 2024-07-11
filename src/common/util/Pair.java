package common.util;

import java.io.Serializable;

/**
 * <div lang="ja">二組のデータを組み合わせるクラス</div>
 *
 * <div lang="en">Pair is two sets of data.</div>
 *
 * @author tori
 *
 * @param <K>
 *            <div lang="ja">キー</div>
 *
 *            <div lang="en">Key</div>
 * @param <V>
 *            <div lang="ja">値</div>
 *
 *            <div lang="en">Value</div>
 */
public class Pair<K, V> implements Serializable {

	private static final long serialVersionUID = -7137802873518091301L;

	K key;
	V value;

	/**
	 * <div lang="ja">指定されたキーと値を持つペアを構築します。</div>
	 *
	 * <div lang="en">Create new key-value pair.</div>
	 *
	 * @param key
	 *              <div lang="ja">キー</div>
	 *
	 *              <div lang="en">Key</div>
	 * @param value
	 *              <div lang="ja">値</div>
	 *
	 *              <div lang="en">Value</div>
	 */
	public Pair(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * <div lang="ja">キーを返します。</div>
	 *
	 * <div lang="en">Get the key.</div>
	 *
	 * @return
	 *
	 *         <div lang="ja">キー</div>
	 *
	 *         <div lang="en">key</div>
	 */
	public K getKey() {
		return key;
	}

	/**
	 * <div lang="ja">キーを設定します。</div>
	 *
	 * <div lang="en">Set the key.</div>
	 *
	 * @param key
	 *            <div lang="ja">設定するキー</div>
	 *
	 *            <div lang="en">Key</div>
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * <div lang="ja">値を返します。</div>
	 *
	 * <div lang="en">Get the value.</div>
	 *
	 * @return
	 *
	 *         <div lang="ja">値</div>
	 *
	 *         <div lang="en">value</div>
	 */
	public V getValue() {
		return value;
	}

	/**
	 * <div lang="ja">値を設定します。</div>
	 *
	 * <div lang="en">Set the value.</div>
	 *
	 * @param value
	 *              <div lang="ja">設定する値</div>
	 *
	 *              <div lang="en">Value</div>
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * <div lang="ja">
	 *
	 * 同一性をチェックします。<br>
	 * キーおよび値の両方の一致性を調べます。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Checks the identity.<br>
	 * Examines the consistency both the keys and values.
	 *
	 * </div>
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<K, V> pair = (Pair<K, V>) obj;
			// return (pair.key.equals(key) && pair.value.equals(value)) ||
			// (pair.key.equals(value) && pair.value.equals(key));
			return (pair.key.equals(key) && pair.value.equals(value));
		}
		return false;
	}

	public int hashCode() {
		return key.hashCode() * 13 + value.hashCode();
	}

	public String toString() {
		return key.toString() + "," + value.toString();
	}
}
