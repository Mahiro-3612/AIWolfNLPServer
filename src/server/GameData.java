package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import common.AIWolfRuntimeException;
import common.data.Agent;
import common.data.Guard;
import common.data.Judge;
import common.data.Role;
import common.data.Species;
import common.data.Status;
import common.data.Talk;
import common.data.Team;
import common.data.Vote;
import common.net.GameInfo;
import common.net.GameInfoToSend;
import common.net.GameSetting;
import common.net.JudgeToSend;
import common.net.TalkToSend;
import common.net.VoteToSend;

/**
 * Record game information of a day
 * 
 * @author tori
 *
 */
public class GameData {
	static final int firstDay = 1;

	/**
	 * The day of the data
	 */
	protected int day;

	/**
	 * status of each agents
	 */
	protected Map<Agent, Status> agentStatusMap;

	/**
	 * roles of each agents
	 */
	protected Map<Agent, Role> agentRoleMap;

	/**
	 *
	 */
	protected List<Talk> talkList;

	/**
	 *
	 */
	protected List<Talk> whisperList;

	/**
	 *
	 */
	protected List<Vote> voteList;

	/**
	 * <div lang="ja">直近の投票リスト</div>
	 *
	 * <div lang="en">The latest list of votes.</div>
	 */
	protected List<Vote> latestVoteList;

	/**
	 *
	 */
	protected List<Vote> attackVoteList;

	/**
	 * <div lang="ja">直近の襲撃投票リスト</div>
	 *
	 * <div lang="en">The latest list of votes for attack.</div>
	 */
	protected List<Vote> latestAttackVoteList;

	/**
	 *
	 */
	protected Map<Agent, Integer> remainTalkMap;

	/**
	 *
	 */
	protected Map<Agent, Integer> remainWhisperMap;

	/**
	 * Result of divination
	 */
	protected Judge divine;

	/**
	 * Guard
	 */
	protected Guard guard;

	/**
	 * executed agent
	 */
	protected Agent executed;

	/**
	 * <div lang="ja">昨夜人狼に襲われ死亡したエージェント</div>
	 *
	 * <div lang="en">the agent who died last night because of the attack by
	 * werewolf.</div>
	 */
	protected Agent attackedDead;

	/**
	 * <div lang="ja">昨夜人狼が襲ったエージェント（成否は問わない）</div>
	 *
	 * <div lang="en">the agent werewolves attacked last night (no matter whether or
	 * not the attack succeeded)</div>
	 */
	protected Agent attacked;

	/**
	 * <div lang="ja">呪殺された妖狐</div>
	 *
	 * <div lang="en">the fox killed by curse</div>
	 */
	protected Agent cursedFox;

	/**
	 * <div lang="ja">昨夜死亡したエージェントのリスト</div>
	 *
	 * <div lang="en">the list of agents who died last night</div>
	 */
	protected List<Agent> lastDeadAgentList;

	/**
	 * agents who sudden death
	 */
	protected List<Agent> suddendeathList;

	/**
	 * game data of one day before
	 */
	protected GameData dayBefore;

	protected int talkIdx;

	protected int wisperIdx;

	/**
	 * ゲームの設定
	 */
	protected GameSetting gameSetting;

	public GameData(GameSetting gameSetting) {
		agentStatusMap = new LinkedHashMap<>();
		agentRoleMap = new HashMap<>();
		remainTalkMap = new HashMap<>();
		remainWhisperMap = new HashMap<>();
		talkList = new ArrayList<>();
		whisperList = new ArrayList<>();
		voteList = new ArrayList<>();
		latestVoteList = new ArrayList<>();
		attackVoteList = new ArrayList<>();
		latestAttackVoteList = new ArrayList<>();
		lastDeadAgentList = new ArrayList<>();
		suddendeathList = new ArrayList<>();

		this.gameSetting = gameSetting;
	}

	/**
	 * get specific game information
	 * 
	 * @param agent
	 * @return
	 */
	public GameInfo getGameInfo(Agent agent) {
		return getGameInfoToSend(agent).toGameInfo();
	}

	/**
	 * get final game information
	 * 
	 * @param agent
	 * @return
	 */
	public GameInfo getFinalGameInfo(Agent agent) {
		return getFinalGameInfoToSend(agent).toGameInfo();
	}

	/**
	 * get game info with all information
	 * 
	 * @return
	 */
	public GameInfo getGameInfo() {
		return getFinalGameInfo(null);
	}

	/**
	 *
	 * @param agent
	 *            - if null, get all information
	 * @return
	 */
	public GameInfoToSend getGameInfoToSend(Agent agent) {
		GameData today = this;
		GameInfoToSend gi = new GameInfoToSend();

		int day = today.getDay();
		if (agent != null) {
			gi.setAgent(agent.getAgentIdx());
		}
		if (gameSetting.isVoteVisible()) {
			List<VoteToSend> latestVoteList = new ArrayList<>();
			for (Vote vote : getLatestVoteList()) {
				latestVoteList.add(new VoteToSend(vote));
			}
			gi.setLatestVoteList(latestVoteList);
		}
		if (getExecuted() != null) {
			gi.setLatestExecutedAgent(getExecuted().getAgentIdx());
		}
		if (agent == null || getRole(agent) == Role.WEREWOLF) {
			List<VoteToSend> latestAttackVoteList = new ArrayList<>();
			for (Vote vote : getLatestAttackVoteList()) {
				latestAttackVoteList.add(new VoteToSend(vote));
			}
			gi.setLatestAttackVoteList(latestAttackVoteList);
		}

		GameData yesterday = today.getDayBefore();

		if (yesterday != null) {
			Agent executed = yesterday.getExecuted();
			if (executed != null) {
				gi.setExecutedAgent(executed.getAgentIdx());
			}

			ArrayList<Integer> lastDeadAgentList = new ArrayList<>();
			for (Agent a : yesterday.getLastDeadAgentList()) {
				lastDeadAgentList.add(a.getAgentIdx());
			}
			gi.setLastDeadAgentList(lastDeadAgentList);

			if (gameSetting.isVoteVisible()) {
				List<VoteToSend> voteList = new ArrayList<>();
				for (Vote vote : yesterday.getVoteList()) {
					voteList.add(new VoteToSend(vote));
				}
				gi.setVoteList(voteList);
			}

			if (agent != null && today.getRole(agent) == Role.MEDIUM && executed != null) {
				Species result = yesterday.getRole(executed).getSpecies();
				gi.setMediumResult(new JudgeToSend(new Judge(day, agent, executed, result)));
			}

			if (agent == null || today.getRole(agent) == Role.SEER) {
				Judge divine = yesterday.getDivine();
				if (divine != null && divine.getTarget() != null) {
					Species result = yesterday.getRole(divine.getTarget()).getSpecies();
					gi.setDivineResult(new JudgeToSend(new Judge(day, divine.getAgent(), divine.getTarget(), result)));
				}
			}

			if (agent == null || today.getRole(agent) == Role.WEREWOLF) {
				Agent attacked = yesterday.getAttacked();
				if (attacked != null) {
					gi.setAttackedAgent(attacked.getAgentIdx());
				}

				List<VoteToSend> attackVoteList = new ArrayList<VoteToSend>();
				for (Vote vote : yesterday.getAttackVoteList()) {
					attackVoteList.add(new VoteToSend(vote));
				}
				gi.setAttackVoteList(attackVoteList);
			}
			if (agent == null || today.getRole(agent) == Role.BODYGUARD) {
				Guard guard = yesterday.getGuard();
				if (guard != null) {
					gi.setGuardedAgent(guard.getTarget().getAgentIdx());
				}
			}
			if (agent == null) {
				if (yesterday.cursedFox != null) {
					gi.setCursedFox(yesterday.cursedFox.getAgentIdx());
				}
			}
		}
		List<TalkToSend> talkList = new ArrayList<TalkToSend>();
		for (Talk talk : today.getTalkList()) {
			talkList.add(new TalkToSend(talk));
		}
		gi.setTalkList(talkList);

		LinkedHashMap<Integer, String> statusMap = new LinkedHashMap<Integer, String>();
		for (Agent a : agentStatusMap.keySet()) {
			statusMap.put(a.getAgentIdx(), agentStatusMap.get(a).toString());
		}
		gi.setStatusMap(statusMap);

		LinkedHashMap<Integer, String> roleMap = new LinkedHashMap<Integer, String>();
		Role role = agentRoleMap.get(agent);

		Set<String> existingRoleSet = new TreeSet<>();
		for (Role r : agentRoleMap.values()) {
			existingRoleSet.add(r.toString());
		}
		gi.setExistingRoleList(new ArrayList<>(existingRoleSet));

		LinkedHashMap<Integer, Integer> remainTalkMap = new LinkedHashMap<Integer, Integer>();
		for (Agent a : this.remainTalkMap.keySet()) {
			remainTalkMap.put(a.getAgentIdx(), this.remainTalkMap.get(a));
		}
		gi.setRemainTalkMap(remainTalkMap);

		LinkedHashMap<Integer, Integer> remainWhisperMap = new LinkedHashMap<Integer, Integer>();
		if (role == Role.WEREWOLF) {
			for (Agent a : this.remainWhisperMap.keySet()) {
				remainWhisperMap.put(a.getAgentIdx(), this.remainWhisperMap.get(a));
			}
		}
		gi.setRemainWhisperMap(remainWhisperMap);

		if (role == Role.WEREWOLF || agent == null) {
			List<TalkToSend> whisperList = new ArrayList<>();
			for (Talk talk : today.getWhisperList()) {
				whisperList.add(new TalkToSend(talk));
			}
			gi.setWhisperList(whisperList);
		}

		if (role != null) {
			roleMap.put(agent.getAgentIdx(), role.toString());
			if (today.getRole(agent) == Role.WEREWOLF) {
				// List<TalkToSend> whisperList = new ArrayList<TalkToSend>();
				// for(Talk talk:today.getWhisperList()){
				// whisperList.add(new TalkToSend(talk));
				// }
				// gi.setWhisperList(whisperList);

				for (Agent target : today.getAgentList()) {
					if (today.getRole(target) == Role.WEREWOLF) {
						// wolfList.add(target);
						roleMap.put(target.getAgentIdx(), Role.WEREWOLF.toString());
					}
				}
			}
			if (today.getRole(agent) == Role.FREEMASON) {
				for (Agent target : today.getAgentList()) {
					if (today.getRole(target) == Role.FREEMASON) {
						roleMap.put(target.getAgentIdx(), Role.FREEMASON.toString());
					}
				}
			}
		}
		gi.setRoleMap(roleMap);
		gi.setRemainTalkMap(remainTalkMap);
		gi.setDay(day);

		return gi;
	}

	public GameInfoToSend getFinalGameInfoToSend(Agent agent) {
		GameInfoToSend gi = getGameInfoToSend(agent);

		LinkedHashMap<Integer, String> roleMap = new LinkedHashMap<Integer, String>();
		for (Agent a : agentRoleMap.keySet()) {
			roleMap.put(a.getAgentIdx(), agentRoleMap.get(a).toString());
		}
		gi.setRoleMap(roleMap);

		return gi;
	}

	/**
	 * Add new agent with their role
	 *
	 * @param agent
	 * @param status
	 * @param role
	 */
	public void addAgent(Agent agent, Status status, Role role) {
		agentRoleMap.put(agent, role);
		agentStatusMap.put(agent, status);
		remainTalkMap.put(agent, gameSetting.getMaxTalk());
		if (getRole(agent) == Role.WEREWOLF) {
			remainWhisperMap.put(agent, gameSetting.getMaxWhisper());
		}
	}

	/**
	 * get agents
	 * 
	 * @return
	 */
	public List<Agent> getAgentList() {
		return new ArrayList<Agent>(agentRoleMap.keySet());
	}

	/**
	 * get status of agent
	 * 
	 * @param agent
	 */
	public Status getStatus(Agent agent) {
		return agentStatusMap.get(agent);
	}

	/**
	 *
	 * @param agent
	 * @return
	 */
	public Role getRole(Agent agent) {
		return agentRoleMap.get(agent);
	}

	/**
	 *
	 * @param agent
	 * @param talk
	 */
	public void addTalk(Agent agent, Talk talk) {
		int remainTalk = remainTalkMap.get(agent);
		if (!talk.isOver() && !talk.isSkip()) {
			if (remainTalk == 0) {
				throw new AIWolfRuntimeException(
						"No remain talk but try to talk. #Contact to AIWolf Platform Developer");
			}
			remainTalkMap.put(agent, remainTalk - 1);
		}
		talkList.add(talk);
	}

	public void addWhisper(Agent agent, Talk whisper) {
		int remainWhisper = remainWhisperMap.get(agent);
		if (!whisper.isOver() && !whisper.isSkip()) {
			if (remainWhisper == 0) {
				throw new AIWolfRuntimeException(
						"No remain whisper but try to whisper. #Contact to AIWolf Platform Developer");
			}
			remainWhisperMap.put(agent, remainWhisper - 1);
		}
		whisperList.add(whisper);
	}

	/**
	 * Add vote data
	 *
	 * @param vote
	 */
	public void addVote(Vote vote) {
		voteList.add(vote);
	}

	/**
	 * Add divine
	 *
	 * @param divine
	 */
	public void addDivine(Judge divine) {
		this.divine = divine;
	}

	public void addGuard(Guard guard) {
		this.guard = guard;
	}

	public void addAttack(Vote attack) {
		attackVoteList.add(attack);
	}

	public List<Vote> getVoteList() {
		return voteList;
	}

	/**
	 * set executed
	 *
	 * @param target
	 */
	public void setExecutedTarget(Agent executed) {
		this.executed = executed;
		if (executed != null) {
			agentStatusMap.put(executed, Status.DEAD);
		}
	}

	/**
	 *
	 * @param attacked
	 */
	public void setAttackedTarget(Agent attacked) {
		this.attacked = attacked;
	}

	/**
	 *
	 * @return
	 */
	public List<Vote> getAttackVoteList() {
		return attackVoteList;
	}

	/**
	 *
	 * @return
	 */
	public Guard getGuard() {
		return guard;
	}

	/**
	 * @return day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @return talkList
	 */
	public List<Talk> getTalkList() {
		return talkList;
	}

	/**
	 * @return wisperList
	 */
	public List<Talk> getWhisperList() {
		return whisperList;
	}

	/**
	 * @return divine
	 */
	public Judge getDivine() {
		return divine;
	}

	/**
	 * @return executed
	 */
	public Agent getExecuted() {
		return executed;
	}

	/**
	 * <div lang="ja">昨夜人狼が襲ったエージェント（成否は問わない）を返す</div>
	 *
	 * <div lang="en">Returns the agent werewolves attacked last night (no
	 * matter whether or not the attack succeeded).</div>
	 *
	 * @return attackedAgent - <div lang="ja">昨夜人狼が襲ったエージェント</div>
	 *
	 *         <div lang="en">the agent werewolves attacked last night</div>
	 */
	public Agent getAttacked() {
		return attacked;
	}

	/**
	 * <div lang="ja">昨夜死亡したエージェントを追加する
	 *
	 * </div> <div lang="en">Adds the agent who died last night.</div>
	 *
	 * @param agent
	 *            <div lang="ja">追加するエージェント</div>
	 *
	 *            <div lang="en">the agent to be added</div>
	 */
	public void addLastDeadAgent(Agent agent) {
		if (!lastDeadAgentList.contains(agent)) {
			lastDeadAgentList.add(agent);
		}
	}

	/**
	 * @return <div lang="ja">昨夜死亡したエージェントのリスト</div>
	 *
	 *         <div lang="en">the list of agents who died last night</div>
	 */
	public List<Agent> getLastDeadAgentList() {
		return lastDeadAgentList;
	}

	/**
	 * @return suddendeathList
	 */
	public List<Agent> getSuddendeathList() {
		return suddendeathList;
	}

	/**
	 * @return remainTalkMap
	 */
	public Map<Agent, Integer> getRemainTalkMap() {
		return remainTalkMap;
	}

	/**
	 * @return remainTalkMap
	 */
	public Map<Agent, Integer> getRemainWhisperMap() {
		return remainWhisperMap;
	}

	/**
	 * Create GameData of next day
	 * 
	 * @return
	 */
	public GameData nextDay() {
		GameData gameData = new GameData(gameSetting);

		gameData.day = this.day + 1;
		gameData.agentStatusMap = new HashMap<Agent, Status>(agentStatusMap);

		for (Agent a : lastDeadAgentList) {
			gameData.agentStatusMap.put(a, Status.DEAD);
		}
		gameData.agentRoleMap = new HashMap<Agent, Role>(agentRoleMap);

		for (Agent a : gameData.getAgentList()) {
			if (gameData.getStatus(a) == Status.ALIVE) {
				gameData.remainTalkMap.put(a, gameSetting.getMaxTalk());
				if (gameData.getRole(a) == Role.WEREWOLF) {
					gameData.remainWhisperMap.put(a, gameSetting.getMaxWhisper());
				}
			}
		}

		gameData.dayBefore = this;

		return gameData;
	}

	/**
	 * get game data of one day before
	 * 
	 * @return
	 */
	public GameData getDayBefore() {
		return dayBefore;
	}

	// /**
	// * get wolf agents
	// * @return
	// */
	// public List<Agent> getWolfList(){
	// List<Agent> wolfList = new ArrayList<>();
	// for(Agent agent:getAgentList()){
	// if(getRole(agent).getSpecies() == Species.Werewolf){
	// wolfList.add(agent);
	// }
	// }
	// return wolfList;
	// }
	//
	// /**
	// * get human agents
	// * @return
	// */
	// public List<Agent> getHumanList(){
	// List<Agent> humanList = new ArrayList<>(getAgentList());
	// humanList.removeAll(getWolfList());
	// return humanList;
	// }

	protected List<Agent> getFilteredAgentList(List<Agent> agentList, Species species) {
		List<Agent> resultList = new ArrayList<Agent>();
		for (Agent agent : agentList) {
			if (getRole(agent).getSpecies() == species) {
				resultList.add(agent);
			}
		}
		return resultList;
	}

	protected List<Agent> getFilteredAgentList(List<Agent> agentList, Status status) {
		List<Agent> resultList = new ArrayList<Agent>();
		for (Agent agent : agentList) {
			if (getStatus(agent) == status) {
				resultList.add(agent);
			}
		}
		return resultList;
	}

	protected List<Agent> getFilteredAgentList(List<Agent> agentList, Role role) {
		List<Agent> resultList = new ArrayList<Agent>();
		for (Agent agent : agentList) {
			if (getRole(agent) == role) {
				resultList.add(agent);
			}
		}
		return resultList;
	}

	protected List<Agent> getFilteredAgentList(List<Agent> agentList, Team team) {
		List<Agent> resultList = new ArrayList<Agent>();
		for (Agent agent : agentList) {
			if (getRole(agent).getTeam() == team) {
				resultList.add(agent);
			}
		}
		return resultList;
	}

	public int nextTalkIdx() {
		return talkIdx++;
	}

	public int nextWhisperIdx() {
		return wisperIdx++;
	}

	/**
	 * <div lang="ja">昨夜人狼に襲われ死亡したエージェントを返す．</div>
	 *
	 * <div lang="en">Returns the agent who died last night because of the attack by
	 * werewolf.</div>
	 *
	 * @return the attackedDead
	 */
	public Agent getAttackedDead() {
		return attackedDead;
	}

	/**
	 * <div lang="ja">昨夜人狼に襲われ死亡したエージェントをセットする．</div>
	 *
	 * <div lang="en">Sets the agent who died last night because of the attack by
	 * werewolf.</div>
	 *
	 * @param attackedDead
	 *            the attackedDead to set
	 */
	public void setAttackedDead(Agent attackedDead) {
		this.attackedDead = attackedDead;
	}

	/**
	 * <div lang="ja">呪殺された妖狐を返す．</div>
	 *
	 * <div lang="en">Returns the fox killed by curse.</div>
	 *
	 * @return <div lang="ja">呪殺された妖狐</div>
	 *
	 *         <div lang="en">the fox killed by curse</div>
	 */
	public Agent getCursedFox() {
		return cursedFox;
	}

	/**
	 * <div lang="ja">呪殺された妖狐をセットする．</div>
	 *
	 * <div lang="en">Sets the fox killed by curse.</div>
	 *
	 * @param cursedFox
	 *            <div lang="ja">呪殺された妖狐</div>
	 *
	 *            <div lang="en">the fox killed by curse</div>
	 */
	public void setCursedFox(Agent cursedFox) {
		this.cursedFox = cursedFox;
	}

	/**
	 * <div lang="ja">直近の投票リストを返す</div>
	 *
	 * <div lang="en">Returns the latest list of votes.</div>
	 *
	 * @return <div lang="ja">投票リストを表す{@code List<Vote>}</div>
	 *
	 *         <div lang="en">{@code List<Vote>} representing the list of
	 *         votes.</div>
	 */
	public List<Vote> getLatestVoteList() {
		return latestVoteList;
	}

	/**
	 * <div lang="ja">直近の投票リストをセットする</div>
	 *
	 * <div lang="en">Sets the latest list of votes.</div>
	 *
	 * @param latestVoteList
	 *            <div lang="ja">投票リストを表す{@code List<Vote>}</div>
	 *
	 *            <div lang="en">{@code List<Vote>} representing the list
	 *            of votes.</div>
	 *
	 */
	public void setLatestVoteList(List<Vote> latestVoteList) {
		this.latestVoteList = latestVoteList;
	}

	/**
	 * <div lang="ja">直近の襲撃投票リストを返す</div>
	 *
	 * <div lang="en">Returns the latest list of votes for attack.</div>
	 *
	 * @return <div lang="ja">投票リストを表す{@code List<Vote>}</div>
	 *
	 *         <div lang="en">{@code List<Vote>} representing the list of
	 *         votes.</div>
	 */
	public List<Vote> getLatestAttackVoteList() {
		return latestAttackVoteList;
	}

	/**
	 * <div lang="ja">直近の襲撃投票リストをセットする</div>
	 *
	 * <div lang="en">Sets the latest list of votes for attack.</div>
	 *
	 * @param latestAttackVoteList
	 *            <div lang="ja">投票リストを表す{@code List<Vote>}</div>
	 *
	 *            <div lang="en">{@code List<Vote>} representing
	 *            the list of votes.</div>
	 *
	 */
	public void setLatestAttackVoteList(List<Vote> latestAttackVoteList) {
		this.latestAttackVoteList = latestAttackVoteList;
	}

	/**
	 *
	 * <div lang="ja">指定エージェントがゲームに含まれているかどうかを調べる</div>
	 *
	 * <div lang="en">Check whether the agents is joining in game.</div>
	 *
	 * @param latestAttackVoteList
	 *            <div lang="ja">含まれているかどうか{@code boolean}</div>
	 *
	 *            <div lang="en">{@code boolean} is contains in the
	 *            game.</div>
	 */
	public boolean contains(Agent target) {
		return this.agentRoleMap.containsKey(target);
	}

}
