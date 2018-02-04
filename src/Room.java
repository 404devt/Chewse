import java.util.ArrayList;

public class Room
{

    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<String> suggestions = new ArrayList<String>();
    private boolean allowNominations = true;
    private boolean allowVotes = false;
    private boolean hasPrintedNominations = false;
    private boolean hasPrintedFinal = false;
    private long startTime = 0;

    public Room()
    {
        startTime = System.currentTimeMillis();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }

    public boolean isAllowNominations() {
        return allowNominations;
    }

    public void setAllowNominations(boolean allowNominations) {
        this.allowNominations = allowNominations;
    }

    public boolean isAllowVotes() {
        return allowVotes;
    }

    public void setAllowVotes(boolean allowVotes) {
        this.allowVotes = allowVotes;
    }

    public boolean isHasPrintedNominations() {
        return hasPrintedNominations;
    }

    public void setHasPrintedNominations(boolean hasPrintedNominations) {
        this.hasPrintedNominations = hasPrintedNominations;
    }

    public boolean isHasPrintedFinal() {
        return hasPrintedFinal;
    }

    public void setHasPrintedFinal(boolean hasPrintedFinal) {
        this.hasPrintedFinal = hasPrintedFinal;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void printNominations()
    {
        String nominationString = generateNominationString();
        for (User u : users)
        {
            u.getWriter().print(nominationString);
            u.getWriter().flush();
        }
        startTime = System.currentTimeMillis();
    }

    public String generateNominationString()
    {
        String ret = "";
        ret += "Here are the nominations to choose from: \n";
        for (int i = 0; i < suggestions.size(); i++)
        {
        	ret += String.format("\t%d - %s\n", (i+1), suggestions.get(i));
        }
        return ret;
    }



}
