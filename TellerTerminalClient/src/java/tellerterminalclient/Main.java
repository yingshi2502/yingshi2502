package tellerterminalclient;

import ejb.session.stateless.AtmCardControllerRemote;
import ejb.session.stateless.CustomerControllerRemote;
import ejb.session.stateless.DepositAccountControllerRemote;
import javax.ejb.EJB;



public class Main
{
    @EJB
    private static CustomerControllerRemote customerControllerRemote;
    @EJB
    private static DepositAccountControllerRemote depositAccountControllerRemote;
    @EJB
    private static AtmCardControllerRemote atmCardControllerRemote;
        
    
    
    public static void main(String[] args)
    {
        MainApp mainApp = new MainApp(customerControllerRemote, depositAccountControllerRemote, atmCardControllerRemote);
        mainApp.runApp();
    }
}