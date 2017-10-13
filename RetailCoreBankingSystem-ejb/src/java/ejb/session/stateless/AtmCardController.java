package ejb.session.stateless;

import entity.AtmCard;
import entity.Customer;
import entity.DepositAccount;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AtmCardExistException;
import util.exception.AtmCardLinkingException;
import util.exception.AtmCardNotFoundException;
import util.exception.AtmCardPinChangeException;
import util.exception.CustomerNotFoundException;
import util.exception.DepositAccountNotFoundException;
import util.exception.GeneralException;
import util.exception.InvalidLoginCredentialException;


/*comment*/
@Stateless
@Local(AtmCardControllerLocal.class)
@Remote(AtmCardControllerRemote.class)

public class AtmCardController implements AtmCardControllerLocal, AtmCardControllerRemote
{
    @PersistenceContext(unitName = "RetailCoreBankingSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private DepositAccountControllerLocal depositAccountControllerLocal;
    @EJB
    private CustomerControllerLocal customerControllerLocal;
    
    
    
    @Override
    public AtmCard createNewAtmCard(AtmCard atmCard, Long customerId, List<Long> depositAccountIds) throws CustomerNotFoundException, DepositAccountNotFoundException, AtmCardExistException, AtmCardLinkingException, GeneralException
    {
        try
        {
            Customer customer = customerControllerLocal.retrieveCustomerByCustomerId(customerId, false, false);
            
            em.persist(atmCard);
            
            atmCard.setCustomer(customer);
            customer.setAtmCard(atmCard);
            
            for(Long depositAccountId:depositAccountIds)
            {
                DepositAccount depositAccount = depositAccountControllerLocal.retrieveDepositAccountByDepositAccountId(depositAccountId, false, false, false);
                
                if(depositAccount.getCustomer().equals(customer))
                {
                    depositAccount.setAtmCard(atmCard);
                    atmCard.getDepositAccounts().add(depositAccount);
                }
                else
                {
                    throw new AtmCardLinkingException("ATM card holder is different from deposit account holder, unable to create new ATM card");
                }
            }
            
            em.flush();
            em.refresh(atmCard);

            return atmCard;
        }
        catch(CustomerNotFoundException ex)
        {
            throw new CustomerNotFoundException("Unable to create new ATM card as the customer record does not exist");
        }
        catch(DepositAccountNotFoundException ex)
        {
            throw new DepositAccountNotFoundException("Unable to create new ATM card as the deposit account record does not exist");
        }
        catch(AtmCardLinkingException ex)
        {
            throw new AtmCardLinkingException(ex.getMessage());
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && 
                    ex.getCause().getCause() != null &&
                    ex.getCause().getCause().getClass().getSimpleName().equals("MySQLIntegrityConstraintViolationException"))
            {
                throw new AtmCardExistException("Atm card with same card number already exist");
            }
            else
            {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
    
    
    
    @Override
    public AtmCard retrieveAtmCardByAtmCardId(Long atmCardId, Boolean fetchCustomer, Boolean fetchDepositAccounts) throws AtmCardNotFoundException
    {
        AtmCard atmCard = em.find(AtmCard.class, atmCardId);
        
        if(atmCard != null)
        {
            if(fetchCustomer)
            {
                atmCard.getCustomer();
            }
            
            if(fetchDepositAccounts)
            {
                atmCard.getDepositAccounts().size();
            }
            
            return atmCard;
        }
        else
        {
            throw new AtmCardNotFoundException("Atm Card ID " + atmCard + " does not exist");
        }
    }
    
    @Override
    public AtmCard retrieveAtmCardByAtmCardNumber(String atmCardNumber, Boolean fetchCustomer, Boolean fetchDepositAccounts) throws AtmCardNotFoundException{
        Query query = em.createQuery("SELECT a FROM AtmCard a WHERE a.cardNumber = :inCardNumber");
        query.setParameter("inCardNumber", atmCardNumber);
        
        try
        {
            AtmCard atmCard =(AtmCard)query.getSingleResult();
            if (atmCard == null) throw new AtmCardNotFoundException("Atm Card Number " + atmCardNumber + " does not exist");
            
            if (fetchDepositAccounts){
                atmCard.getDepositAccounts().size();
            }
            if (fetchCustomer){
                atmCard.getCustomer();
            }
           
            return atmCard;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new AtmCardNotFoundException("ATM Card ID " + atmCardNumber + " does not exist!");
        }
    }
   
    
    //link the depositAccount to AtmCard
    @Override
    public void linkDepositAccountToAtmCard(Long atmCardId, Long depositAccountId) throws AtmCardLinkingException
    {
        try
        {
            AtmCard atmCard = retrieveAtmCardByAtmCardId(atmCardId, false, false);
            DepositAccount depositAccount = depositAccountControllerLocal.retrieveDepositAccountByDepositAccountId(depositAccountId, false, false, false);
        
            if(atmCard.getCustomer().equals(depositAccount.getCustomer()))
            {
                if(!atmCard.getDepositAccounts().contains(depositAccount))// if it already exists
                {
                    atmCard.getDepositAccounts().add(depositAccount);
                    depositAccount.setAtmCard(atmCard);
                }
                else
                {
                    throw new AtmCardLinkingException("The deposit account is already linked to the ATM card");
                }
            }
            else
            {
                throw new AtmCardLinkingException("ATM card holder is different from deposit account holder");
            }
        }
        catch(AtmCardNotFoundException | DepositAccountNotFoundException ex)
        {
            throw new AtmCardLinkingException(ex.getMessage());
        }
    }
    
    
    
    @Override
    public void removeAtmCard(Long atmCardId) throws AtmCardNotFoundException
    {
        AtmCard atmCard = retrieveAtmCardByAtmCardId(atmCardId, false, false);
        atmCard.getCustomer().setAtmCard(null);
        atmCard.setCustomer(null);
        
        for(DepositAccount depositAccount:atmCard.getDepositAccounts())
        {
            depositAccount.setAtmCard(null);
        }//remove all associated depositAccount;
        
        atmCard.getDepositAccounts().clear();// clear out atmCard's depositAccount List
        
        em.remove(atmCard);
    }
    
    
    //take the accounts out
    @Override
    public List<DepositAccount> retrieveLinkedDepositAccountsByAtmCardId(Long atmCardId) throws AtmCardNotFoundException
    {
        AtmCard atmCard = retrieveAtmCardByAtmCardId(atmCardId, false, false);
        List<DepositAccount> depositAccounts = atmCard.getDepositAccounts();
        
        for(DepositAccount depositAccount:depositAccounts)
        {
            em.detach(depositAccount);
            depositAccount.setCustomer(null);
            depositAccount.setAtmCard(null);
        }
        
        return depositAccounts;
    }
    
    
    
    @Override
    public void changePin(Long atmCardId, String currentPin, String newPin) throws AtmCardNotFoundException, AtmCardPinChangeException
    {
        AtmCard atmCard = retrieveAtmCardByAtmCardId(atmCardId, false, false);
        
        if(atmCard.getPin().equals(currentPin))
        {
            atmCard.setPin(newPin);
        }
        else
        {
            throw new AtmCardPinChangeException("Current PIN is invalid");
        }
    }
    
    @Override
    public void updateAtmCard(AtmCard atmCard){
        em.merge(atmCard);
    }
    
    @Override
    public AtmCard atmCardVerify(String cardNumber, String pin) throws InvalidLoginCredentialException{
        try{
            AtmCard atmCard = retrieveAtmCardByAtmCardNumber(cardNumber,true,true);
            if (atmCard.getPin().equals(pin)){
                return atmCard;
            }
            else{
                throw new InvalidLoginCredentialException("Card Number does not exist or invalid PIN");
            }
        }catch(AtmCardNotFoundException ex){
            throw new InvalidLoginCredentialException("Card Number does not exist or invalid PIN");
        }
    }
    
}
