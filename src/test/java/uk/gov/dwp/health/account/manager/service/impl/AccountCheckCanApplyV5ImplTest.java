package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountCheckCanApplyV5ImplTest {

  @Test
  void checkCanApply() {
    final CheckCanApplyService service = mock(CheckCanApplyService.class);
    final List mockList = mock(List.class);
    when(service.checkCanApply(anyString())).thenReturn(mockList);
    final AccountCheckCanApplyV5Impl checkCanApplyV5 = new AccountCheckCanApplyV5Impl(service);
    assertEquals(mockList, checkCanApplyV5.checkCanApply("N123"));
  }
}
