package uk.gov.dwp.health.account.manager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.service.impl.AccountCheckCanApplyV5Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV5Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateTransferStatusImpl;

@Service
@AllArgsConstructor
@Getter
public class V5AccountManagerServices {
  private AccountCheckCanApplyV5Impl accountCheckCanApplyV5;
  private AccountCreateV5Impl accountCreateV5;
  private final AccountUpdateTransferStatusImpl accountUpdateTransferStatus;
}
