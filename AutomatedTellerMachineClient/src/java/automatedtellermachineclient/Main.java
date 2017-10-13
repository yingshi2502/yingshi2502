package automatedtellermachineclient;

import ejb.session.stateless.AtmCardControllerRemote;
import javax.ejb.EJB;



public class Main
{
    @EJB
    private static AtmCardControllerRemote atmCardControllerRemote;
    
    public static void main(String[] args)
    {
        MainApp mainApp = new MainApp(atmCardControllerRemote);
        mainApp.runApp();
    }
}