package common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <div lang="ja">
 *
 * カレンダーに関する静的なメソッドを提供するクラスです。
 *
 * </div>
 *
 * <div lang="en">
 *
 * CalendarTools is that provides a static method on the calendar.
 *
 * </div>
 *
 * @author tori
 *
 */
public class CalendarTools {

	/**
	 * <div lang="ja">
	 *
	 * 指定されたCalendarオブジェクトを<b>yyyy/MM/dd HH:mm:ss</b>形式の文字列に直して返します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts to <b>yyyy/MM/dd HH:mm:ss</b> format.
	 *
	 * </div>
	 *
	 * @param cal
	 *            <div lang="ja">対象とするCalendarオブジェクト</div>
	 *
	 *            <div lang="en">Calender object</div>
	 * @return
	 *
	 *         <div lang="ja">文字列フォーマット</div>
	 *
	 *         <div lang="en">String format</div>
	 */
	public static String toDateTime(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定されたDateオブジェクトを<b>yyyy/MM/dd HH:mm:ss</b>形式の文字列に直して返します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts to <b>yyyy/MM/dd HH:mm:ss</b> format.
	 *
	 * </div>
	 *
	 * @param date
	 *            <div lang="ja">対象とするDateオブジェクト</div>
	 *
	 *            <div lang="en">Date object</div>
	 * @return
	 *
	 *         <div lang="ja">文字列フォーマット</div>
	 *
	 *         <div lang="en">String format</div>
	 */
	public static String toDateTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date.getTime());
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定されたlong型の値を<b>yyyy/MM/dd HH:mm:ss</b>形式の文字列に直して返します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts to <b>yyyy/MM/dd HH:mm:ss</b> format.
	 *
	 * </div>
	 *
	 * @param time
	 *            <div lang="ja">対象とするlong型の値</div>
	 *
	 *            <div lang="en">Long value</div>
	 * @return
	 *
	 *         <div lang="ja">文字列フォーマット</div>
	 *
	 *         <div lang="en">String format</div>
	 */
	public static String toDateTime(long time) {
		Date date = new Date(time);
		return toDateTime(date);
	}

	/**
	 * <div lang="ja">
	 *
	 * 年-月-日-時-分-秒の順で並んだ日付をCalendarオブジェクトに変換します。<br>
	 * 区切り文字は数字以外のすべての文字または空白にしてください。
	 *
	 * 足りない分はすべて0で補完されます。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts to Calendar object.
	 *
	 * </div>
	 *
	 * @param dateTimeString
	 *            <div lang="ja">年-月-日-時-分-秒の順で並んだ日付</div>
	 *
	 *            <div lang="en">Date time string</div>
	 * @return
	 *
	 *         <div lang="ja">変換されたCalendarオブジェクト</div>
	 *
	 *         <div lang="en">Converted calendar object</div>
	 */
	public static Calendar toCalendar(String dateTimeString) {
		String[] ary = dateTimeString.split("[\\D\\s]+");
		int[] dateAry = new int[ary.length];
		int idx = 0;
		for (int i = 0; i < ary.length; i++) {
			try {
				if (idx == 1) {
					dateAry[idx++] = Integer.parseInt(ary[i]) - 1;
				} else {
					dateAry[idx++] = Integer.parseInt(ary[i]);
				}
			} catch (NumberFormatException e) {
				// TODO コメントアウトされたソースコード(continue;が適していると思われる。)
				// throw e;
			}
		}

		int[] targetAry = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
				Calendar.SECOND, Calendar.MILLISECOND };

		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < targetAry.length; i++) {
			if (dateAry.length <= i) {
				cal.set(targetAry[i], 0);
			} else {
				cal.set(targetAry[i], dateAry[i]);
			}
		}
		return cal;
	}

	/**
	 * <div lang="ja">
	 *
	 * Date型のオブジェクトをCalendar型のオブジェクトに変換します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts from Date object to Calendar object.
	 *
	 * </div>
	 *
	 * @param date
	 *            <div lang="ja">Date型のオブジェクト</div>
	 *
	 *            <div lang="en">Date object</div>
	 * @return
	 *
	 *         <div lang="ja">Calendar型のオブジェクト</div>
	 *
	 *         <div lang="en">Calendar object</div>
	 */
	static public Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * <div lang="ja">
	 *
	 * long型のミリ秒をCalendar型のオブジェクトに変換します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts from milis to Calendar object.
	 *
	 * </div>
	 *
	 * @param timeInMillis
	 *            <div lang="ja">ミリ秒</div>
	 *
	 *            <div lang="en">Millis</div>
	 * @return
	 *
	 *         <div lang="ja">Calendar型のオブジェクト</div>
	 *
	 *         <div lang="en">Calendar object</div>
	 */
	static public Calendar toCalendar(long timeInMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		return cal;
	}

	/**
	 * <div lang="ja">
	 *
	 * 指定されたCalendarをその日の深夜(00:00:00.000)に時間を設定します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Sets time the midnight of the day.
	 *
	 * </div>
	 *
	 * @param calendar
	 *            <div lang="ja">深夜に時間を設定するCalendar</div>
	 *
	 *            <div lang="en">Calendar</div>
	 * @return
	 *
	 *         <div lang="ja">深夜に時間を設定したカレンダー</div>
	 *
	 *         <div lang="en">Calendar which set the time</div>
	 */
	static public Calendar toMidnight(Calendar calendar) {
		Calendar date = (Calendar) calendar.clone();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}

	/**
	 * <div lang="ja">
	 *
	 * Calendarオブジェクトを<b>yyyy/MM/dd</b>形式の文字列に変換し、返します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts from Calendar object to <b>yyyy/MM/dd</b> format String.
	 *
	 * </div>
	 *
	 * @param cal
	 *            <div lang="ja">対象となるCalendarオブジェクト</div>
	 *
	 *            <div lang="en">Calendar object</div>
	 * @return
	 *
	 *         <div lang="ja">文字列フォーマット</div>
	 *
	 *         <div lang="en">String format</div>
	 */
	static public String toDateString(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(cal.getTime());
	}

	/**
	 * <div lang="ja">
	 *
	 * Dateオブジェクトを<b>yyyy/MM/dd</b>形式の文字列に変換し、返します。
	 *
	 * </div>
	 *
	 * <div lang="en">
	 *
	 * Converts from Date object to <b>yyyy/MM/dd</b> format String.
	 *
	 * </div>
	 *
	 * @param date
	 *            <div lang="ja">対象となるDateオブジェクト</div>
	 *
	 *            <div lang="en">Date object</div>
	 * @return
	 *
	 *         <div lang="ja">文字列フォーマット</div>
	 *
	 *         <div lang="en">String format</div>
	 */
	static public String toDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(date.getTime());
	}

	/**
	 * <div lang="ja">同一日かどうかを返します。</div>
	 *
	 * <div lang="en">Is same day?</div>
	 *
	 * @param date1
	 *            <div lang="ja">date2と比較するCalendarオブジェクト</div>
	 *
	 *            <div lang="en">Calendar object which compares date2</div>
	 * @param date2
	 *            <div lang="ja">date1と比較するCalendarオブジェクト</div>
	 *
	 *            <div lang="en">Calendar object which compares date1</div>
	 * @return
	 *
	 *         <div lang="ja">同一日かどうか</div>
	 *
	 *         <div lang="en">Same day or other day</div>
	 */
	public static boolean isSameDay(Calendar date1, Calendar date2) {
		return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
				&& date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
	}

}
