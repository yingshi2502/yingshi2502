package ejb.session.stateless;

import entity.DepositAccount;
import util.exception.CustomerNotFoundException;
import util.exception.DepositAccountExistException;
import util.exception.DepositAccountNotFoundException;
import util.exception.GeneralException;



public interface DepositAccountControllerLocal
{
    public DepositAccount createNewDepositAccount(DepositAccount depositAccount, Long customerId) throws CustomerNotFoundException, DepositAccountExistException, GeneralException;
    
    public DepositAccount retrieveDepositAccountByDepositAccountId(Long depositAccountId, Boolean fetchCustomer, Boolean fetchAtmCard, Boolean fetchTransactions) throws DepositAccountNotFoundException;
}