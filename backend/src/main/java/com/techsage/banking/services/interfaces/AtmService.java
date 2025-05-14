package com.techsage.banking.services.interfaces;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;

import javax.naming.*;

public interface AtmService {
    BankAccountDto deposit(AtmDepositDto atmDepositDto, User initiator) throws TransactionException;
    BankAccountDto withdraw(AtmWithdrawDto atmWithdrawDto, User initiator) throws TransactionException;
}
