package launcher;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import core.Config;
import core.GameBuilder;

public class GameStarter extends Thread {
	private static final Logger logger = LogManager.getLogger(GameStarter.class);

	private final List<GameBuilder> gameBuilders = new ArrayList<>();
	private final Queue<List<Socket>> socketQueue;
	private final Config config;

	public GameStarter(Queue<List<Socket>> socketQueue, Config config) {
		this.socketQueue = socketQueue;
		this.config = config;
	}

	@Override
	public void run() {
		while (true) {
			// 実行が終了しているサーバの削除
			gameBuilders.removeIf(server -> !server.isAlive());

			// 同時起動数未満なら待機Listから1グループ取得してゲームを開始する
			synchronized (socketQueue) {
				if (!socketQueue.isEmpty() && gameBuilders.size() < config.getMaxParallelExec()) {
					GameBuilder builder = new GameBuilder(socketQueue.poll(), config);
					gameBuilders.add(builder);
					builder.start();
				}
			}

			// CPU使用率上昇対策
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				logger.error("Exception", e);
			}
		}
	}

	public boolean isWaitingGame() {
		return !socketQueue.isEmpty();
	}

	public boolean isGameRunning() {
		return !gameBuilders.isEmpty();
	}
}