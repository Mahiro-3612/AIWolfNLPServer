package common;

import java.io.Serial;

/**
 * <div lang="ja">
 *
 * オブジェクトが返ってこないときにスローされる例外です。<br>
 * Tcp/IP通信時に使用されます。
 *
 * </div>
 *
 * <div lang="en">
 *
 * Throws when the object is not returned.
 *
 * </div>
 */
public class NoReturnObjectException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 6672785062337881971L;

	/**
	 * <div lang="ja">新規例外を構築します。</div>
	 *
	 * <div lang="en">Constructs a new exception.</div>
	 */
	public NoReturnObjectException() {
		super();
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定された詳細メッセージを持つ、新規例外を構築します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Constructs a new exception with the specified detail message.
	 *
	 * </div>
	 *
	 * @param message
	 *
	 *            <div lang="ja">詳細メッセージ</div>
	 *
	 *            <div lang="en">Detail message</div>
	 */
	public NoReturnObjectException(String message) {
		super(message);
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定された詳細メッセージおよび原因を使用して
	 *
	 * 新規例外を構築します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Constructs a new exception
	 *
	 * with the specified detail message and cause.
	 *
	 * </div>
	 *
	 * @param message
	 *            <div lang="ja">詳細メッセージ</div>
	 *
	 *            <div lang="en">Detail message</div>
	 * @param cause
	 *            <div lang="ja">原因</div>
	 *
	 *            <div lang="en">Cause</div>
	 */
	public NoReturnObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定された詳細メッセージ、原因、抑制の有効化または
	 *
	 * 無効化、書込み可能スタック・トレースの有効化または
	 *
	 * 無効化に基づいて、新しい例外を構築します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Constructs a new exception with the specified
	 *
	 * detail message, cause, suppression enabled or disabled,
	 *
	 * and writable stack trace enabled or disabled.
	 *
	 * </div>
	 *
	 * @param message
	 *            <div lang="ja">詳細メッセージ</div>
	 *
	 *            <div lang="en">Detail message</div>
	 * @param cause
	 *            <div lang="ja">原因</div>
	 *
	 *            <div lang="en">Cause</div>
	 * @param enableSuppression
	 *            <div lang="ja">抑制の有効化または無効化</div>
	 *
	 *            <div lang="en">
	 *
	 *            Whether or not suppression is enabled or disabled
	 *
	 *            </div>
	 * @param writableStackTrace
	 *            <div lang="ja">書込み可能スタック・トレースの有効化または 無効化</div>
	 *
	 *            <div lang="en">
	 *
	 *            Whether or not the stack trace should be writable
	 *
	 *            </div>
	 */
	public NoReturnObjectException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定された原因と詳細メッセージ
	 *
	 * {@code (cause==null ? null : cause.toString())}
	 *
	 * を持つ新しい例外を構築します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Constructs a new exception
	 *
	 * with the specified cause and a detail message of
	 *
	 * {@code (cause==null ? null : cause.toString())}.
	 *
	 * </div>
	 *
	 * @param cause
	 *            <div lang="ja">原因</div>
	 *
	 *            <div lang="en">Cause</div>
	 */
	public NoReturnObjectException(Throwable cause) {
		super(cause);
	}
}
