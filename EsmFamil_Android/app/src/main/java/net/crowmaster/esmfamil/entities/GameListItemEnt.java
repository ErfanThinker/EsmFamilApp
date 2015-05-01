package net.crowmaster.esmfamil.entities;

/**
 * Created by root on 4/28/15.
 */
public class GameListItemEnt {
    //public int icon;
    public long gid;
    public long id;
    public String gname;//game name
    public String cname;//creator name
    public int capacity;
    public int joined;
    public int rounds;
    public String color;
    public GameListItemEnt(long GID, long ID, String GameName, String CreatorName,
                           int Capacity, int Joined, int Rounds, String Color){
        //icon = Icon;
        gid = GID;
        id = ID;
        gname = GameName;
        cname = CreatorName;
        capacity = Capacity;
        joined = Joined;
        rounds = Rounds;
        color = Color;
    }




}
