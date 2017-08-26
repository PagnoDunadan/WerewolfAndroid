package pero.fesb.hr.werewolfandroid;


public class Player {
    private int ID;
    private int roomId;
    private String playerName;
    private String role;

    public Player(int ID, int roomId, String playerName, String role) {
        this.ID = ID;
        this.roomId = roomId;
        this.playerName = playerName;
        this.role = role;
    }

    public int getID() {
        return ID;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
