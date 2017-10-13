package util.exception;


//Atm card with same card number already exist
public class AtmCardExistException extends Exception
{
    public AtmCardExistException()
    {
    }
    
    
    
    public AtmCardExistException(String msg)
    {
        super(msg);
    }
}