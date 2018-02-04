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
    private String key;

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

    public boolean checkSuggestion()
    {
        if (suggestions.size() == 0)
        {
            return false;
        }
        else
            return true;
    }
    public void printNominations()
    {
        String nominationString = generateNominationString();
        for (User u : users)
        {
            u.getWriter().print(nominationString);
            u.getWriter().flush();
        }
        this.allowNominations = false;
        this.allowVotes = true;
        this.hasPrintedNominations = true;
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
    public void noSuggestions()
    {
        for(User u: users) {
            u.getWriter().println("No Suggestions Entered");
            u.getWriter().flush();
        }
    }
    public void resetSuggestionPhase()
    {
        startTime = System.currentTimeMillis();
        allowNominations = true;
        allowVotes = false;
        hasPrintedNominations = false;
        hasPrintedFinal = false;
    }


    public void printVoteResult()
    {

        int[] resutls = new int[suggestions.size()];
        int winScore = -1;
        for (User u : users)
        {
            if (u.getApprovals() != null) {
                for (int i = 0; i < u.getApprovals().length; i++) {
                    if (u.getApprovals()[i])
                        resutls[i]++;
                }
            }
        }


        ///


        ArrayList<Integer> tiedIndecies = new ArrayList<>();

        for (int i = 0; i < resutls.length; i++)
        {
            if (resutls[i] > winScore)
            {
                winScore = resutls[i];
                tiedIndecies.clear();
                tiedIndecies.add(i);


            }
            else if(resutls[i] == winScore)
            {
                tiedIndecies.add(i);
            }



        }

        this.hasPrintedFinal = true;
        this.hasPrintedNominations = true;
        this.allowVotes = false;
        this.allowNominations = false;
        this.startTime = System.currentTimeMillis();

        String resultString;

        if(tiedIndecies.size()==1)
        resultString = String.format("Winner is %s with %d approvals!\n", suggestions.get(tiedIndecies.get(0)), suggestions.get(tiedIndecies.get(0)));
        else
        {
            resultString = String.format("%d options are tied with %d points!\n",tiedIndecies.size(),winScore);
            for (int indx : tiedIndecies)
            {
                resultString += String.format("\t - %s\n", suggestions.get(indx));
            }
        }

        for (User u : users)
        {
            u.getWriter().print(resultString);
            u.getWriter().flush();
        }
    }

    public boolean isFinished()
    {
        return hasPrintedFinal && (System.currentTimeMillis() - startTime > 5000);
    }
    public void closeRoom()
    {
        for (User u : users)
        {
            u.disconnect();
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
