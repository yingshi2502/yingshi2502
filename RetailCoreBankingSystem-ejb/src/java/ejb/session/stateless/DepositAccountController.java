package ejb.session.stateless;

import entity.Customer;
import entity.DepositAccount;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.CustomerNotFoundException;
import util.exception.DepositAccountExistException;
import util.exception.DepositAccountNotFoundException;
import util.exception.GeneralException;



@Stateless
@Local(DepositAccountControllerLocal.class)
@Remote(DepositAccountControllerRemote.class)

public class DepositAccountController implements DepositAccountControllerLocal, DepositAccountControllerRemote
{
    @PersistenceContext(unitName = "RetailCoreBankingSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private CustomerControllerLocal customerControllerLocal;
    
    
    
    @Override
    public DepositAccount createNewDepositAccount(DepositAccount depositAccount, Long customerId) throws CustomerNotFoundException, DepositAccountExistException, GeneralException
    {
        try
        {
            Customer customer = customerControllerLocal.retrieveCustomerByCustomerId(customerId, false, false);
            
            em.persist(depositAccount);
            
            depositAccount.setCustomer(customer);
            customer.getDepositAccounts().add(depositAccount);
            
            em.flush();
            em.refresh(depositAccount);

            return depositAccount;
        }
        catch(CustomerNotFoundException ex)
        {
            throw new CustomerNotFoundException("Unable to create new deposit account as the customer record does not exist");
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && 
                    ex.getCause().getCause() != null &&
                    ex.getCause().getCause().getClass().getSimpleName().equals("MySQLIntegrityConstraintViolationException"))
            {
                throw new DepositAccountExistException("DepositAccount with same identification number already exist");
            }
            else
            {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
    
    
    
    @Override
    public DepositAccount retrieveDepositAccountByDepositAccountId(Long depositAccountId, Boolean fetchCustomer, Boolean fetchAtmCard, Boolean fetchTransactions) throws DepositAccountNotFoundException
    {
        DepositAccount depositAccount = em.find(DepositAccount.class, depositAccountId);
        
        if(depositAccount != null)
        {
            if(fetchCustomer)
            {
                depositAccount.getCustomer();
            }
            
            if(fetchAtmCard)
            {
                depositAccount.getAtmCard();
            }
            
            if(fetchTransactions)
            {
                depositAccount.getDepositAccountTransactions().size();
            }
            
            return depositAccount;
        }
        else
        {
            throw new DepositAccountNotFoundException("Deposit Account ID " + depositAccount + " does not exist");
        }
    }
}