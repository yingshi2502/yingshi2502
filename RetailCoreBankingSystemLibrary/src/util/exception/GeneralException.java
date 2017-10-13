package util.exception;


//An unexpected error has occurred;
public class GeneralException extends Exception
{
    public GeneralException()
    {
    }
    
    
    
    public GeneralException(String msg)
    {
        super(msg);
    }
}