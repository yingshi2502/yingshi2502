package ejb.session.stateless;

import entity.AtmCard;
import entity.DepositAccount;
import java.util.List;
import util.exception.AtmCardExistException;
import util.exception.AtmCardLinkingException;
import util.exception.AtmCardNotFoundException;
import util.exception.AtmCardPinChangeException;
import util.exception.CustomerNotFoundException;
import util.exception.DepositAccountNotFoundException;
import util.exception.GeneralException;
import util.exception.InvalidLoginCredentialException;



public interface AtmCardControllerLocal
{
    public AtmCard createNewAtmCard(AtmCard atmCard, Long customerId, List<Long> depositAccountIds) throws CustomerNotFoundException, DepositAccountNotFoundException, AtmCardExistException, AtmCardLinkingException, GeneralException;
    
    public AtmCard retrieveAtmCardByAtmCardId(Long atmCardId, Boolean fetchCustomer, Boolean fetchDepositAccounts) throws AtmCardNotFoundException;
    
    public void linkDepositAccountToAtmCard(Long atmCardId, Long depositAccountId) throws AtmCardLinkingException;
    
    public void removeAtmCard(Long atmCardId) throws AtmCardNotFoundException;

    public List<DepositAccount> retrieveLinkedDepositAccountsByAtmCardId(Long atmCardId) throws AtmCardNotFoundException;

    public void changePin(Long atmCardId, String currentPin, String newPin) throws AtmCardNotFoundException, AtmCardPinChangeException;

    public AtmCard retrieveAtmCardByAtmCardNumber(String atmCardNumber, Boolean fetchCustomer, Boolean fetchDepositAccounts) throws AtmCardNotFoundException;

    public void updateAtmCard(AtmCard atmCard);
   
    public AtmCard atmCardVerify(String cardNumber, String pin) throws InvalidLoginCredentialException;

}