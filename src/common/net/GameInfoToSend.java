package common.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.data.Agent;
import common.data.Role;
import common.data.Status;
import common.data.Talk;
import common.data.Vote;

public class GameInfoToSend {
	int day;
	int agent;

	JudgeToSend mediumResult;
	JudgeToSend divineResult;
	int executedAgent = -1;
	int latestExecutedAgent = -1;
	int attackedAgent = -1;
	int cursedFox = -1;
	int guardedAgent = -1;
	List<Vote> voteList;
	List<Vote> latestVoteList;
	List<Vote> attackVoteList;
	List<Vote> latestAttackVoteList;

	List<Talk> talkList;
	List<Talk> whisperList;

	Map<Integer, String> statusMap;
	LinkedHashMap<Integer, String> roleMap;
	LinkedHashMap<Integer, Integer> remainTalkMap;
	LinkedHashMap<Integer, Integer> remainWhisperMap;

	List<Integer> lastDeadAgentList;
	List<String> existingRoleList;

	public GameInfoToSend() {
		voteList = new ArrayList<>();
		latestVoteList = new ArrayList<>();
		attackVoteList = new ArrayList<>();
		latestAttackVoteList = new ArrayList<>();
		statusMap = new HashMap<>();
		roleMap = new LinkedHashMap<>();
		remainTalkMap = new LinkedHashMap<>();
		remainWhisperMap = new LinkedHashMap<>();
		talkList = new ArrayList<>();
		whisperList = new ArrayList<>();
		lastDeadAgentList = new ArrayList<>();
		existingRoleList = new ArrayList<>();
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setAgent(int agent) {
		this.agent = agent;
	}

	public void setMediumResult(JudgeToSend mediumResult) {
		this.mediumResult = mediumResult;
	}

	public void setDivineResult(JudgeToSend divineResult) {
		this.divineResult = divineResult;
	}

	public void setExecutedAgent(int executedAgent) {
		this.executedAgent = executedAgent;
	}

	public void setAttackedAgent(int attackedAgent) {
		this.attackedAgent = attackedAgent;
	}

	public void setGuardedAgent(int guardedAgent) {
		this.guardedAgent = guardedAgent;
	}

	public void setVoteList(List<Vote> voteList) {
		this.voteList = voteList;
	}

	public void setAttackVoteList(List<Vote> attackVoteList) {
		this.attackVoteList = attackVoteList;
	}

	public List<Talk> getTalkList() {
		return talkList;
	}

	public void setTalkList(List<Talk> talkList) {
		this.talkList = talkList;
	}

	public List<Talk> getWhisperList() {
		return whisperList;
	}

	public void setWhisperList(List<Talk> whisperList) {
		this.whisperList = whisperList;
	}

	public void setStatusMap(LinkedHashMap<Integer, String> statusMap) {
		for (int i : statusMap.keySet()) {
			this.statusMap.put(i, statusMap.get(i));
		}
	}

	public void setRoleMap(LinkedHashMap<Integer, String> roleMap) {
		this.roleMap = roleMap;
	}

	public void setRemainTalkMap(LinkedHashMap<Integer, Integer> remainTalkMap) {
		this.remainTalkMap = remainTalkMap;
	}

	public void setRemainWhisperMap(LinkedHashMap<Integer, Integer> remainWhisperMap) {
		this.remainWhisperMap = remainWhisperMap;
	}

	public void setLastDeadAgentList(List<Integer> lastDeadAgentList) {
		this.lastDeadAgentList = lastDeadAgentList;
	}

	public void setExistingRoleList(List<String> existingRoleList) {
		this.existingRoleList = existingRoleList;
	}

	public GameInfo toGameInfo() {
		GameInfo gi = new GameInfo();
		gi.day = day;
		gi.agent = Agent.getAgent(agent);

		if (mediumResult != null) {
			gi.mediumResult = mediumResult.toJudge();
		}
		if (divineResult != null) {
			gi.divineResult = divineResult.toJudge();
		}
		gi.executedAgent = Agent.getAgent(executedAgent);
		gi.latestExecutedAgent = Agent.getAgent(latestExecutedAgent);
		gi.attackedAgent = Agent.getAgent(attackedAgent);
		gi.cursedFox = Agent.getAgent(cursedFox);
		gi.guardedAgent = Agent.getAgent(guardedAgent);

		gi.voteList = new ArrayList<>();
		for (Vote vote : voteList) {
			gi.voteList.add(vote);
		}
		gi.latestVoteList = new ArrayList<>();
		for (Vote vote : latestVoteList) {
			gi.latestVoteList.add(vote);
		}
		gi.attackVoteList = new ArrayList<>();
		for (Vote vote : attackVoteList) {
			gi.attackVoteList.add(vote);
		}
		gi.latestAttackVoteList = new ArrayList<>();
		for (Vote vote : latestAttackVoteList) {
			gi.latestAttackVoteList.add(vote);
		}

		gi.talkList = new ArrayList<>();
		for (Talk talk : this.getTalkList()) {
			gi.talkList.add(talk);
		}
		gi.whisperList = new ArrayList<>();
		for (Talk whisper : this.getWhisperList()) {
			gi.whisperList.add(whisper);
		}

		gi.lastDeadAgentList = new ArrayList<>();
		for (int agent : lastDeadAgentList) {
			gi.lastDeadAgentList.add(Agent.getAgent(agent));
		}

		gi.statusMap = new HashMap<>();
		for (int agent : statusMap.keySet()) {
			gi.statusMap.put(Agent.getAgent(agent), Status.valueOf(statusMap.get(agent)));
		}
		gi.roleMap = new HashMap<>();
		for (int agent : roleMap.keySet()) {
			gi.roleMap.put(Agent.getAgent(agent), Role.valueOf(roleMap.get(agent)));
		}
		gi.remainTalkMap = new HashMap<>();
		for (int agent : remainTalkMap.keySet()) {
			gi.remainTalkMap.put(Agent.getAgent(agent), remainTalkMap.get(agent));
		}
		gi.remainWhisperMap = new HashMap<>();
		for (int agent : remainWhisperMap.keySet()) {
			gi.remainWhisperMap.put(Agent.getAgent(agent), remainWhisperMap.get(agent));
		}

		gi.existingRoleList = new ArrayList<>();
		for (String roleText : existingRoleList) {
			gi.existingRoleList.add(Role.valueOf(roleText));
		}

		return gi;
	}

	public void setLatestVoteList(List<Vote> latestVoteList) {
		this.latestVoteList = latestVoteList;
	}

	public void setLatestAttackVoteList(List<Vote> latestAttackVoteList) {
		this.latestAttackVoteList = latestAttackVoteList;
	}

	public void setLatestExecutedAgent(int latestExecutedAgent) {
		this.latestExecutedAgent = latestExecutedAgent;
	}

	public void setCursedFox(int cursedFox) {
		this.cursedFox = cursedFox;
	}
}
