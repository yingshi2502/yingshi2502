package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.GeneralException;



@Stateless
@Local(CustomerControllerLocal.class)
@Remote(CustomerControllerRemote.class)



public class CustomerController implements CustomerControllerLocal, CustomerControllerRemote
{
    @PersistenceContext(unitName = "RetailCoreBankingSystem-ejbPU")
    private EntityManager em;
    
    
    
    @Override
    public Customer createNewCustomer(Customer customer) throws CustomerExistException, GeneralException
    {
        try
        {
            em.persist(customer);
            em.flush();
            em.refresh(customer);

            return customer;
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && 
                    ex.getCause().getCause() != null &&
                    ex.getCause().getCause().getClass().getSimpleName().equals("MySQLIntegrityConstraintViolationException"))
            {
                throw new CustomerExistException("Customer with same identification number already exist");
            }
            else
            {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());//All other exceptions
            }
        }
    }
    
    
    
    @Override
    public Customer retrieveCustomerByCustomerId(Long customerId, Boolean fetchDepositAccounts, Boolean fetchAtmCard) throws CustomerNotFoundException
    {
        Customer customer = em.find(Customer.class, customerId);
        
        if(customer != null)
        {
            if(fetchDepositAccounts) //lazy. get the needed attributes
            {
                customer.getDepositAccounts().size();
            }
            
            if(fetchAtmCard)
            {
                customer.getAtmCard();
            }
            
            return customer;
        }
        else
        {
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist");
        }
    }
}