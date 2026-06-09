package com.workintech.s18d4.controller;

import com.workintech.s18d4.dto.AccountResponse;
import com.workintech.s18d4.dto.CustomerResponse;
import com.workintech.s18d4.entity.Account;
import com.workintech.s18d4.entity.Customer;
import com.workintech.s18d4.service.AccountService;
import com.workintech.s18d4.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private final CustomerService customerService;

    @GetMapping
    public List<Account> findAll(){
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account find(@PathVariable long id){
        Account account = accountService.find(id);

        if(account == null){
            throw new RuntimeException("There is no account with this id");
        }
        return account;
    }

    @PostMapping("/{customerId}")
    public AccountResponse save(@PathVariable long customerId, @RequestBody Account account){
         Customer customer = customerService.find(customerId);
         if(customer != null){
         customer.getAccounts().add(account);
         account.setCustomer(customer);
         accountService.save(account);
         } else {
             throw new RuntimeException("no customer found");
         }
         return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(customer.getId(), customer.getEmail(), customer.getSalary()));
    }

    @PutMapping("/{customerId}")
    public AccountResponse update(@RequestBody Account account, @PathVariable long customerId){
        Customer customer = customerService.find(customerId);
        Account toBeUpdated = null;

        for(Account account1 : customer.getAccounts()){
            if(account.getId() == account1.getId()){
                toBeUpdated = account1;
            }
        }
            if(toBeUpdated == null){
                throw new RuntimeException("Customer does not exists");
            }

        int indexOfToBeUpdated = customer.getAccounts().indexOf(toBeUpdated);
        customer.getAccounts().set(indexOfToBeUpdated, account);
        account.setCustomer(customer);
        accountService.save(account);
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(customerId, customer.getEmail(), customer.getSalary()));
    }

    @DeleteMapping("/{id}")
    public AccountResponse remove(@PathVariable long id){
        Account account = accountService.find(id);
        if(account ==null){
            throw new RuntimeException("This Id does not exists");
        }
        accountService.delete(id);
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(account.getCustomer().getId(), account.getCustomer().getEmail(),account.getCustomer().getSalary()));
    }

}
