package uk.gov.dwp.health.account.manager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.service.impl.AccountCheckCanApplyV5Impl;

@Service
@AllArgsConstructor
@Getter
public class V5AccountManagerServices {

  private AccountCheckCanApplyV5Impl accountCheckCanApplyV5;

}
