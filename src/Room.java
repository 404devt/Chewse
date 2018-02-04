import java.util.ArrayList;

public class Room
{
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<String> suggestions = new ArrayList<String>();


    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }
}
