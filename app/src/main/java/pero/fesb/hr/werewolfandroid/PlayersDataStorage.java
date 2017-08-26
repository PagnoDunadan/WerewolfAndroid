package pero.fesb.hr.werewolfandroid;


import java.util.HashMap;

public class PlayersDataStorage {
    public static HashMap<Integer, Player> playersListViewData = new HashMap<Integer, Player>();

    public static Player[] players;


    public static void fillData() {
        playersListViewData.clear();
        for(int i = 0; i < players.length; i++){
            Player aPlayer = new Player(i + 1, players[i].getRoomId(), players[i].getPlayerName(), players[i].getRole());
            playersListViewData.put(i, aPlayer);
        }
    }
}
