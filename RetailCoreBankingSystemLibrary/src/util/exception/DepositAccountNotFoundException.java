package util.exception;



public class DepositAccountNotFoundException extends Exception
{
    public DepositAccountNotFoundException()
    {
    }
    
    
    
    public DepositAccountNotFoundException(String msg)
    {
        super(msg);
    }
}