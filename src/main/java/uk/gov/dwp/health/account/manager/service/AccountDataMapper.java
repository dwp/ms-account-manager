package uk.gov.dwp.health.account.manager.service;

import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V4AccountDetails;

@Service("accountDataMapper")
public class AccountDataMapper {

  public AccountDetails mapToAccountDetails(Claimant claimant) {
    var details = new AccountDetails();
    details.setDob(claimant.getDateOfBirth());
    details.setSurname(claimant.getSurname());
    details.setForename(claimant.getForename());
    details.setPostcode(claimant.getPostcode());
    details.setEmail(claimant.getEmailAddress());
    details.setLanguage(AccountDetails.LanguageEnum.fromValue(claimant.getLanguage()));
    details.setMobilePhone(claimant.getMobileNumber());
    details.setNino(claimant.getNino());
    details.setRegion(
        claimant.getRegion() == null
            ? AccountDetails.RegionEnum.GB
            : AccountDetails.RegionEnum.valueOf(claimant.getRegion().name()));
    return details;
  }

  public V3AccountDetails mapToV3AccountDetails(Claimant claimant) {
    var details = new V3AccountDetails();
    details.setDob(claimant.getDateOfBirth());
    details.setSurname(claimant.getSurname());
    details.setForename(claimant.getForename());
    details.setPostcode(claimant.getPostcode());
    details.setEmail(claimant.getEmailAddress());
    details.setLanguage(V3AccountDetails.LanguageEnum.fromValue(claimant.getLanguage()));
    details.setMobilePhone(claimant.getMobileNumber());
    details.setNino(claimant.getNino());
    details.setRegion(
        claimant.getRegion() == null
            ? V3AccountDetails.RegionEnum.GB
            : V3AccountDetails.RegionEnum.valueOf(claimant.getRegion().name()));
    details.setUserJourney(
        V3AccountDetails.UserJourneyEnum.fromValue(claimant.getUserJourney().name()));
    return details;
  }

  public V4AccountDetails mapToV4AccountDetails(Claimant claimant) {
    var details = new V4AccountDetails();
    details.setDob(claimant.getDateOfBirth());
    details.setSurname(claimant.getSurname());
    details.setForename(claimant.getForename());
    details.setPostcode(claimant.getPostcode());
    details.setEmail(claimant.getEmailAddress());
    details.setLanguage(V4AccountDetails.LanguageEnum.fromValue(claimant.getLanguage()));
    details.setMobilePhone(claimant.getMobileNumber());
    details.setNino(claimant.getNino());
    details.setRegion(
            claimant.getRegion() == null
                    ? V4AccountDetails.RegionEnum.GB
                    : V4AccountDetails.RegionEnum.valueOf(claimant.getRegion().name()));
    details.setUserJourney(
            V4AccountDetails.UserJourneyEnum.fromValue(claimant.getUserJourney().name()));
    details.setResearchContact(
            claimant.getResearchContact() == null
                    ? V4AccountDetails.ResearchContactEnum.NO
                    : V4AccountDetails.ResearchContactEnum
                      .fromValue(claimant.getResearchContact().name()));
    return details;
  }
}
