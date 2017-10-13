package util.exception;



public class InvalidAtmCardCredentialException extends Exception
{
    public InvalidAtmCardCredentialException()
    {
    }
    
    
    
    public InvalidAtmCardCredentialException(String msg)
    {
        super(msg);
    }
}