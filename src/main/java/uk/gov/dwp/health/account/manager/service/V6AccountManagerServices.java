package uk.gov.dwp.health.account.manager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.service.impl.AccountCheckCanApplyV5Impl;
import uk.gov.dwp.health.account.manager.service.impl.CheckCanApplyServiceImplV6;

@Service
@AllArgsConstructor
@Getter
public class V6AccountManagerServices {
  private CheckCanApplyServiceImplV6 accountCheckCanApplyV6;
}
