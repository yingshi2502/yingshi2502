package util.exception;



public class DepositAccountExistException extends Exception
{
    public DepositAccountExistException()
    {
    }
    
    
    
    public DepositAccountExistException(String msg)
    {
        super(msg);
    }
}