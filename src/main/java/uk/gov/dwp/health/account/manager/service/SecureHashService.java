package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface SecureHashService<T, S> {
  S hash(T t);
}
