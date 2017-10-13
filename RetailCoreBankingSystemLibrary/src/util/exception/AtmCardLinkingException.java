package util.exception;

//ATM card holder is different from deposit account holder, unable to create new ATM card
//The deposit account is already linked to the ATM card
//ATM card holder is different from deposit account holder
public class AtmCardLinkingException extends Exception
{
    public AtmCardLinkingException()
    {
    }
    
    
    
    public AtmCardLinkingException(String msg)
    {
        super(msg);
    }
}