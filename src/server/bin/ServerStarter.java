package server.bin;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Random;

import common.net.GameSetting;
import server.AIWolfGame;
import server.net.TcpipServer;

/**
 * Main Class to start server application
 * 
 * @author tori
 *
 */
public class ServerStarter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SocketTimeoutException
	 */
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		int port = 10000;
		int playerNum = 12;
		String logFileName = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-p")) {
					i++;
					port = Integer.parseInt(args[i]);
				} else if (args[i].equals("-n")) {
					i++;
					playerNum = Integer.parseInt(args[i]);
				} else if (args[i].equals("-l")) {
					i++;
					logFileName = args[i];
				}
			}
		}

		System.out.printf("Start AiWolf Server port:%d playerNum:%d\n", port, playerNum);
		GameSetting gameSetting = GameSetting.getDefaultGame(playerNum);

		TcpipServer gameServer = new TcpipServer(port, playerNum, gameSetting);
		gameServer.waitForConnection();

		AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
		game.setRand(new Random());
		if (logFileName != null) {
			game.setLogFile(new File(logFileName));
		}
		game.start();

	}

}
