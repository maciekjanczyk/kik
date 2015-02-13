
public class Player {

    private String nickname = "";
    private boolean token = false;
    private String address = "";
    private boolean turn = false;
    private Table currentTable;

    public boolean hasToken() { return token; }

    public Table getCurrentTable() { return currentTable; }

    public void setCurrentTable(Table table) { this.currentTable = table; }

    public void insertNickname(String nick) {
        nickname = nick;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean hasTurn() {
        return turn;
    }

    public void takeToken() {
        token = true;
    }

    public void setTurn(boolean bool) {
        turn = bool;
    }

    public void getToken() { token = false; }

}
