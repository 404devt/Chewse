import java.io.IOException;

public class Main
{
    public static void main (String[] args) throws IOException {
//        try {
//            ServerSocket ss = null;
//
//            ss = new ServerSocket(20000);
//
//            Socket s = null;
//
//            s= ss.accept();
//
//            PrintWriter writer;
//
//            writer = new PrintWriter(s.getOutputStream());
//
//            writer.println("HI BABE");
//
//            writer.flush();
//
//            System.out.println("?");
//
//
//            Thread.sleep(5000);
//
//            System.out.println("??");
//
//            s.close();
//
//            ss.close();
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }

        Server s = new Server(20000);
        while (true)
        {
            for (String key : s.getRooms().keySet())
            {
                Room room = s.getRooms().get(key);
                if(!room.isHasPrintedNominations() && System.currentTimeMillis() - room.getStartTime() > 45000)
                {

                    if (room.checkSuggestion())
                        room.printNominations();
                    else {
                        room.noSuggestions();
                        room.resetSuggestionPhase();
                    }
                }

                if(room.isHasPrintedNominations() && !room.isHasPrintedFinal() && System.currentTimeMillis() - room.getStartTime() > 45000)
                {
                    room.printVoteResult();
                }
                if (room.isFinished())
                {
                    room.closeRoom();
                    s.getRooms().remove(room.getKey());
                }
            }   
        }



    }


}
