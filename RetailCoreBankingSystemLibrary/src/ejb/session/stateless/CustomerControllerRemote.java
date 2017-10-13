package ejb.session.stateless;

import entity.Customer;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.GeneralException;



public interface CustomerControllerRemote
{
    public Customer createNewCustomer(Customer customer) throws CustomerExistException, GeneralException;
    
    public Customer retrieveCustomerByCustomerId(Long customerId, Boolean fetchDepositAccounts, Boolean fetchAtmCard) throws CustomerNotFoundException;
}