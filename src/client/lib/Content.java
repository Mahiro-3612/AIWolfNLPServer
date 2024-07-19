/**
 * Content.java
 * 
 * Copyright (c) 2016 人狼知能プロジェクト
 */
package client.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.data.Agent;
import common.data.Role;
import common.data.Species;
import common.data.Talk;

/**
 * <div lang="ja">発話内容クラス。ContentBuilderあるいは発話テキストから生成</div>
 *
 * <div lang="en">Class for the content of a utterance. Constructed by giving a
 * ContentBuilder or the uttered text.</div>
 */
public class Content implements Cloneable {

	/**
	 * <div lang="ja">SKIPを表す定数</div>
	 * 
	 * <div lang="en">Constant representing SKIP.</div>
	 */
	public static final Content SKIP = new Content(new SkipContentBuilder());

	/**
	 * <div lang="ja">OVERを表す定数</div>
	 * 
	 * <div lang="en">Constant representing OVER.</div>
	 */
	public static final Content OVER = new Content(new OverContentBuilder());

	/**
	 * <div lang="ja">不特定のエージェントを表す定数</div>
	 * 
	 * <div lang="en">Constant representing an arbitrary agent.</div>
	 */
	public static final Agent ANY = Agent.getAgent(0);

	/**
	 * <div lang="ja">エージェント未特定であることを表す定数</div>
	 * 
	 * <div lang="en">Constant representing an unspecified agent.</div>
	 */
	public static final Agent UNSPEC = null;

	private String text = null;
	private Operator operator = null;
	private Topic topic = null;
	private Agent subject = UNSPEC;
	private Agent target = ANY;
	private Role role = null;
	private Species result = null;
	private TalkType talkType = null;
	private int talkDay = -1;
	private int talkID = -1;
	private List<Content> contentList = null;
	private int day = -1;

	// かっこで囲んだContent文字列の並びをContentのリストに変換する
	private static List<Content> getContents(String input, boolean isForValidation) {
		List<Content> contents = new ArrayList<>();
		for (String s : getContentStrings(input)) {
			contents.add(new Content(s, isForValidation));
		}
		return contents;
	}

	// かっこで囲んだContent文字列の並びをContent文字列のリストに変換する
	private static List<String> getContentStrings(String input) {
		List<String> strings = new ArrayList<>();
		int length = input.length();
		int stackPtr = 0;
		int start = 0;
		for (int i = 0; i < length; i++) {
			if (input.charAt(i) == '(') {
				if (stackPtr == 0) {
					start = i;
				}
				stackPtr++;
			} else if (input.charAt(i) == ')') {
				stackPtr--;
				if (stackPtr == 0) {
					strings.add(input.substring(start + 1, i));
				}
			}
		}
		return strings;
	}

	// 内側の文のsubjectを補完する
	private void completeInnerSubject() {
		if (contentList == null) {
			return;
		}
		contentList = contentList.stream().map(c -> {
			if (c.subject == UNSPEC) {
				// INQUIREとREQUESTでsubjectが省略された場合は外の文のtarget
				if (operator == Operator.INQUIRE || operator == Operator.REQUEST) {
					Content cl = c.cloneAndReplaceSubject(target);
					return cl;
				}
				// それ以外は外の文のsubject
				if (UNSPEC != subject) { // 未指定の場合は何もしない
					Content cl = c.cloneAndReplaceSubject(subject);
					return cl;
				}
			}
			c.completeInnerSubject();
			return c;
		}).collect(Collectors.toList());
	}

	// 複製したContentのsubjectを入れ替えて返す
	// Clone this and replace subject with given subject.
	private Content cloneAndReplaceSubject(Agent newSubject) {
		Content c = clone();
		c.subject = newSubject;
		c.completeInnerSubject();
		c.normalizeText(); // subjectを入れ替えると簡潔にできる場合がある
		return c;
	}

	/**
	 * <div lang="ja">指定したContentBuilderによりContentを構築する</div>
	 *
	 * <div lang="en">Constructs a Content by the given ContentBuilder.</div>
	 * 
	 * @param builder
	 *            <div lang="ja">発話内容に応じたContentBuilder</div>
	 *
	 *            <div lang="en">ContentBuilder for the content.</div>
	 */
	public Content(ContentBuilder builder) {
		operator = builder.getOperator();
		topic = builder.getTopic();
		subject = builder.getSubject();
		target = builder.getTarget();
		role = builder.getRole();
		result = builder.getResult();
		talkType = builder.getTalkType();
		talkDay = builder.getTalkDay();
		talkID = builder.getTalkID();
		contentList = builder.getContentList();
		day = builder.getDay();
		completeInnerSubject();
		normalizeText();
	}

	private static final String regAgent = "\\s+(Agent\\[\\d+\\]|ANY)";
	private static final String regSubject = "^(Agent\\[\\d+\\]|ANY|)\\s*";
	private static final String regTalk = "\\s+(\\p{Upper}+)\\s+day(\\d+)\\s+ID:(\\d+)";
	private static final String regRoleResult = "\\s+(\\p{Upper}+)";
	private static final String regParen = "(\\(.*\\))";
	private static final String regDigit = "(\\d+)";
	private static final String TERM = "$";
	private static final String SP = "\\s+";
	private static final Pattern agreePattern = Pattern.compile(regSubject + "(AGREE|DISAGREE)" + regTalk + TERM);
	private static final Pattern estimatePattern = Pattern
			.compile(regSubject + "(ESTIMATE|COMINGOUT)" + regAgent + regRoleResult + TERM);
	private static final Pattern divinedPattern = Pattern
			.compile(regSubject + "(DIVINED|IDENTIFIED)" + regAgent + regRoleResult + TERM);
	private static final Pattern attackPattern = Pattern
			.compile(regSubject + "(ATTACK|ATTACKED|DIVINATION|GUARD|GUARDED|VOTE|VOTED)" + regAgent + TERM);
	private static final Pattern requestPattern = Pattern
			.compile(regSubject + "(REQUEST|INQUIRE)" + regAgent + SP + regParen + TERM);
	private static final Pattern becausePattern = Pattern
			.compile(regSubject + "(BECAUSE|AND|OR|XOR|NOT|REQUEST)" + SP + regParen + TERM);
	private static final Pattern dayPattern = Pattern
			.compile(regSubject + "DAY" + SP + regDigit + SP + regParen + TERM);

	/**
	 * <div lang="ja">発話テキストによりContentを構築する</div>
	 *
	 * <div lang="en">Constructs a Content from the uttered text.</div>
	 * 
	 * @param input
	 *            <div lang="ja">発話テキスト</div>
	 *
	 *            <div lang="en">The uttered text.</div>
	 */
	public Content(String input) {
		this(input, false);
	}

	private Content(String input, boolean isForValidation) {
		String trimmed = input.trim();
		Matcher m;
		try {
			// SKIP
			if (trimmed.equals(Talk.SKIP)) {
				topic = Topic.SKIP;
			}
			// OVER
			else if (trimmed.equals(Talk.OVER)) {
				topic = Topic.OVER;
			}
			// AGREE,DISAGREE
			else if ((m = agreePattern.matcher(trimmed)).find()) {
				subject = toAgent(m.group(1));
				topic = Topic.valueOf(m.group(2));
				talkType = TalkType.valueOf(m.group(3));
				talkDay = Integer.parseInt(m.group(4));
				talkID = Integer.parseInt(m.group(5));
			}
			// ESTIMATE,COMINGOUT
			else if ((m = estimatePattern.matcher(trimmed)).find()) {
				subject = toAgent(m.group(1));
				topic = Topic.valueOf(m.group(2));
				target = toAgent(m.group(3));
				role = Role.valueOf(m.group(4));
			}
			// DIVINED,IDENTIFIED
			else if ((m = divinedPattern.matcher(trimmed)).find()) {
				subject = toAgent(m.group(1));
				topic = Topic.valueOf(m.group(2));
				target = toAgent(m.group(3));
				result = Species.valueOf(m.group(4));
			}
			// ATTACK,ATTACKED,DIVINATION,GUARD,GUARDED,VOTE,VOTED
			else if ((m = attackPattern.matcher(trimmed)).find()) {
				subject = toAgent(m.group(1));
				topic = Topic.valueOf(m.group(2));
				target = toAgent(m.group(3));
			}
			// REQUEST,INQUIRE
			else if ((m = requestPattern.matcher(trimmed)).find()) {
				topic = Topic.OPERATOR;
				subject = toAgent(m.group(1));
				operator = Operator.valueOf(m.group(2));
				target = toAgent(m.group(3));
				contentList = getContents(m.group(4), true);
			}
			// BECAUSE,AND,OR,XOR,NOT,REQUEST(ver.2)
			else if ((m = becausePattern.matcher(trimmed)).find()) {
				topic = Topic.OPERATOR;
				subject = toAgent(m.group(1));
				operator = Operator.valueOf(m.group(2));
				contentList = getContents(m.group(3), true);
				if (operator == Operator.REQUEST) {
					target = contentList.get(0).subject == UNSPEC ? ANY : contentList.get(0).subject;
				}
			}
			// DAY
			else if ((m = dayPattern.matcher(trimmed)).find()) {
				topic = Topic.OPERATOR;
				operator = Operator.DAY;
				subject = toAgent(m.group(1));
				day = Integer.parseInt(m.group(2));
				contentList = getContents(m.group(3), true);
			}
			// Unknown string pattern.
			else {
				throw new IllegalContentStringException();
			}
		} catch (IllegalArgumentException e) {
			if (isForValidation) {
				throw new IllegalContentStringException(input);
			} else {
				topic = Topic.SKIP;
			}
		}
		completeInnerSubject();
		normalizeText();
	}

	/**
	 * <div lang="ja">発話テキストを返す</div>
	 *
	 * <div lang="en">Returns the uttered text.</div>
	 * 
	 * @return <div lang="ja">発話テキスト</div>
	 *
	 *         <div lang="en">The uttered text.</div>
	 */
	public String getText() {
		return text;
	}

	/**
	 * <div lang="ja">発話内容の演算子を返す</div>
	 *
	 * <div lang="en">Returns the operator of this content.</div>
	 * 
	 * @return <div lang="ja">演算子。単文の場合は{@code null}</div>
	 *
	 *         <div lang="en">The operator, or {@code null} when it is a simple
	 *         sentence.</div>
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * <div lang="ja">発話内容の主語を返す</div>
	 *
	 * <div lang="en">Returns the subject of this content.</div>
	 * 
	 * @return <div lang="ja">主語</div>
	 *
	 *         <div lang="en">The subject.</div>
	 */
	public Agent getSubject() {
		return subject;
	}

	/**
	 * <div lang="ja">発話内容のトピックを返す</div>
	 *
	 * <div lang="en">Returns the topic of this content.</div>
	 * 
	 * @return <div lang="ja">トピック</div>
	 *
	 *         <div lang="en">The topic.</div>
	 */
	public Topic getTopic() {
		return topic;
	}

	/**
	 * <div lang="ja">発話内容中の目的エージェントを返す。発話が単文で，かつTopicが(DIS)AGREE以外で有効</div>
	 *
	 * <div lang="en">Returns the objective agent of this content. Valid when it is
	 * a simple sentence and the topic is other than (DIS)AGREE.</div>
	 * 
	 * @return <div lang="ja">目的エージェント。無効の場合は{@code null}</div>
	 *
	 *         <div lang="en">The objective agent, or {@code null} when it is
	 *         invalid.</div>
	 */
	public Agent getTarget() {
		return target;
	}

	/**
	 * <div lang=
	 * "ja">発話内容中で言及されている役職を返す。発話が単文で，かつTopicがCOMINGOUTとESTIMATEのとき有効</div>
	 *
	 * <div lang="en">Returns the role referred in this content. Valid when it is a
	 * simple sentence and the topic is COMINGOUT or ESTIMATE.</div>
	 * 
	 * @return <div lang="ja">言及されている役職。無効の場合は{@code null}</div>
	 *
	 *         <div lang="en">The referred role, or {@code null} when it is
	 *         invalid.</div>
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * <div lang=
	 * "ja">発話内容中で言及されている判定結果を返す。発話が単文で，かつTopicがDIVINEDとINQUESTEDのとき有効</div>
	 *
	 * <div lang="en">Returns the result of the judgment referred in this content.
	 * Valid when it is a simple sentence and the topic is DIVINED or
	 * INQUESTED.</div>
	 * 
	 * @return <div lang="ja">言及されている判定結果。無効の場合は{@code null}</div>
	 *
	 *         <div lang="en">The referred result, or {@code null} when it is
	 *         invalid.</div>
	 */
	public Species getResult() {
		return result;
	}

	/**
	 * <div lang="ja">発話内容中で言及されている発言のタイプを返す。発話が単文で，かつTopicが(DIS)AGREEのとき有効</div>
	 *
	 * <div lang="en">Returns the type of the utterance referred in this content.
	 * Valid when it is a simple sentence and the topic is (DIS)AGREE.</div>
	 * 
	 * @return <div lang="ja">言及されている発言のタイプ。無効の場合は{@code null}</div>
	 *
	 *         <div lang="en">The type of utterance, or {@code null} when it is
	 *         invalid.</div>
	 */
	public TalkType getTalkType() {
		return talkType;
	}

	/**
	 * <div lang="ja">発話内容中で言及されている発言の日を返す。発話が単文で，かつTopicが(DIS)AGREEのとき有効</div>
	 *
	 * <div lang="en">Returns the day of the utterance referred in this content.
	 * Valid when it is a simple sentence and the topic is (DIS)AGREE.</div>
	 * 
	 * @return <div lang="ja">言及されている発言の日。無効の場合は-1</div>
	 *
	 *         <div lang="en">The day of the referred utterance, or -1 when it is
	 *         invalid.</div>
	 */
	public int getTalkDay() {
		return talkDay;
	}

	/**
	 * <div lang="ja">発話内容中で言及されている発言のIDを返す。発話が単文で，かつTopicが(DIS)AGREEのとき有効</div>
	 *
	 * <div lang="en">Returns the ID of the utterance referred in this content.
	 * Valid when it is a simple sentence and the topic is (DIS)AGREE.</div>
	 * 
	 * @return <div lang="ja">言及されている発言のID。無効の場合は-1</div>
	 *
	 *         <div lang="en">The ID of the referred utterance, or -1 when it is
	 *         invalid.</div>
	 */
	public int getTalkID() {
		return talkID;
	}

	/**
	 * <div lang="ja">発話内容が複文・重文の場合，節のリストを返す</div>
	 *
	 * <div lang="en">Returns the list of clauses in case of complex or compound
	 * sentence.</div>
	 * 
	 * @return <div lang="ja">節のリスト。単文の場合は{@code null}</div>
	 *
	 *         <div lang="en">The list of clauses, or {@code null} in case of simple
	 *         sentence.</div>
	 */
	public List<Content> getContentList() {
		return contentList;
	}

	/**
	 * <div lang="ja">発話の日付を返す</div>
	 *
	 * <div lang="en">Returns the date of content.</div>
	 * 
	 * @return <div lang="ja">日付</div>
	 *
	 *         <div lang="en">Date.</div>
	 */
	public int getDay() {
		return day;
	}

	/**
	 * <div lang="ja">発話テキストが有効かどうかを返す．</div>
	 * 
	 * <div lang="en">Returns whether or not the uttered text is valid.</div>
	 * 
	 * @param input
	 *            <div lang="ja">被チェックテキスト</div>
	 *
	 *            <div lang="en">The text to be checked.</div>
	 * 
	 * @return <div lang="ja">有効である場合{@code true}，そうでなければ{@code false}</div>
	 *
	 *         <div lang="en">{@code true} if the text is valid, otherwise
	 *         {@code false}.</div>
	 */
	public static boolean validate(String input) {
		try {
			new Content(input, true);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	// textを正規化する
	private void normalizeText() {
		switch (topic) {
			case SKIP:
				text = Talk.SKIP;
				break;
			case OVER:
				text = Talk.OVER;
				break;
			case AGREE:
			case DISAGREE:
				text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
						+ topic
						+ " " + talkType.toString() + " day" + talkDay + " ID:" + talkID;
				break;
			case ESTIMATE:
			case COMINGOUT:
				text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
						+ topic
						+ " " + (target == ANY ? "ANY" : target.toString())
						+ " " + role.toString();
				break;
			case DIVINED:
			case IDENTIFIED:
				text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
						+ topic
						+ " " + (target == ANY ? "ANY" : target.toString())
						+ " " + result.toString();
				break;
			case ATTACK:
			case ATTACKED:
			case DIVINATION:
			case GUARD:
			case GUARDED:
			case VOTE:
			case VOTED:
				text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
						+ topic
						+ " " + (target == ANY ? "ANY" : target.toString());
				break;
			case OPERATOR:
				switch (operator) {
					case REQUEST:
					case INQUIRE:
						text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
								+ operator
								+ " " + (target == ANY ? "ANY" : target.toString())
								+ " ("
								+ (contentList.get(0).getSubject() == target
										? stripSubject(contentList.get(0).getText())
										: contentList.get(0).getText())
								+ ")";
						break;
					case BECAUSE:
					case XOR:
						text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
								+ operator
								+ " ("
								+ (contentList.get(0).getSubject() == subject
										? stripSubject(contentList.get(0).getText())
										: contentList.get(0).getText())
								+ ") ("
								+ (contentList.get(1).getSubject() == subject
										? stripSubject(contentList.get(1).getText())
										: contentList.get(1).getText())
								+ ")";
						break;
					case AND:
					case OR:
						text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
								+ operator
								+ " " + contentList.stream().map(c -> "(" +
										(c.getSubject() == subject ? stripSubject(c.getText()) : c.getText())
										+ ")").collect(Collectors.joining(" "));
						break;
					case NOT:
						text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
								+ operator
								+ " ("
								+ (contentList.get(0).getSubject() == subject
										? stripSubject(contentList.get(0).getText())
										: contentList.get(0).getText())
								+ ")";
						break;
					case DAY:
						text = (subject == UNSPEC ? "" : subject == ANY ? "ANY " : subject + " ")
								+ operator
								+ " " + day
								+ " ("
								+ (contentList.get(0).getSubject() == subject
										? stripSubject(contentList.get(0).getText())
										: contentList.get(0).getText())
								+ ")";
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	private static final Pattern agentPattern = Pattern.compile("\\s?(Agent\\[(\\d+)\\]|ANY)\\s?");

	private static Agent toAgent(String s) {
		if (s.isEmpty()) {
			return UNSPEC;
		}
		Matcher m = agentPattern.matcher(s);
		if (m.find()) {
			if (m.group(1).equals("ANY")) {
				return ANY;
			} else {
				return Agent.getAgent(Integer.parseInt(m.group(2)));
			}
		}
		throw new IllegalContentStringException();
	}

	@Override
	public Content clone() {
		Content clone = null;
		try {
			clone = (Content) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

	@Override
	public boolean equals(Object content) {
		if (content instanceof Content && text != null) {
			return text.equals(((Content) content).getText());
		}
		return false;
	}

	private static final Pattern stripPattern = Pattern.compile("^(Agent\\[\\d+\\]|ANY|)\\s*(\\p{Upper}+)(.*)$");

	/**
	 * <div lang="ja">発話文字列からsubjectの部分を除いた文字列を返す</div>
	 *
	 * <div lang="en">Strips subject off the given string and returns it.</div>
	 * 
	 * @param input
	 *            <div lang="ja">入力文字列</div>
	 *
	 *            <div lang="en">Input string.</div>
	 * @return <div lang="ja">発話文字列からsubjectの部分を除いた文字列</div>
	 *
	 *         <div lang="en">String with no subject prefix.</div>
	 * 
	 */
	public static String stripSubject(String input) {
		Matcher m = stripPattern.matcher(input);
		if (m.find()) {
			return m.group(2) + m.group(3);
		}
		return input;
	}

	@Override
	public String toString() {
		return text;
	}

}
