# ms-account-manager

micro-service to enable claimants to authenticate with DWP. The main purposes of this service are to support online HTML application process for PIP2 questionnaire. 

## Dependency

the API stores and queries user's account details from a Mongo database. To be able to successfully start the application, the application must be 
able to connect to a Mongo instance at start up. 

The API also rely on [TOTP micro service](https://gitlab.com/health-pdu/save-and-resume/ms-totp-authenticator) to generate and verify
TOTP. API invoke TOTP micro service via POST rest calls.

The API has a transitive dependency to [Notify](https://www.notifications.service.gov.uk/), where it sends out TOTP one time passcode to the citizen.

## rest api

the api is built from the [openapi-spec.yaml](api-spec/openapi-spec-v1.yaml)

## running the application

this is a standard SpringBoot application with all the configuration items held in `src/main/resources/application.yml` and bundled 
into the project at build.

```bash
mvn clean verify
```
to build and vulnerability check
```bash
sh run-local.sh #this includes some sample environment variable values to get the app running

or

mvn spring-boot:run

or

java -jar target/ms-account-manager-<artifactId>.jar
```
to run

## Running the Component Tests

### Running Locally
Run the following command to spin up the service in docker
```bash 
docker-compose up --scale api-test=0
```
Open another terminal window and run the following maven command to execute the tests locally
```bash 
mvn clean verify -Papi-component-tests
```

Alternatively if you run into any issues you can try the following:
In a separate terminal window spin up the supporting stubbed services by running the bash script
```zsh
sh run-docker-local.sh
```

## configuration elements

All configuration is listed in `src/main/resources/application.yml` and follows the standard spring convention for yml file notation.  
The custom setup is configured with the following section and can be overridden (either on the command line or by environment variables).

The main configuration is serialised into handler classes 
`uk.gov.dwp.health.account.manager.config.TotpClientPropereis`,
`uk.gov.dwp.health.account.manager.config.properties.CryptoConfigProperties`,

```yaml
account-manager:
    allow-failure: 3

encryption:
  kms-override: http://localhost:4549
  data-key: arn:address
  # kms-key-caching default to false
  kms-key-cache: true 


totp:
  base-url: http://localhost:9999
  verify-path: /v1/totp/verify
  generate-path: /v1/totp/generate

feature:
  encryption:
    data:
      enabled: true 
```

* `account-manager.allow-failure` = the number of failed login attempts allowed before user account is locked **(note 1)**
* `encryption.kms-override` = override kms url e.g. http://localhost:4599
* `encryption.data-key` = aws KMS arn
* `totp.base-url` = base url of totp micro-service
* `totp.verify-path` = endpoint path of verify totp 
* `totp.generate-path` = endpoint path generate totp
* `feature.encryption.data.enabled` = enable data encryption/decryption onBeforeSave and onAfterLoad event. **(note 2)**

**NOTE 1** : unless you would like to override the max number of allowed login challenges, the default is set to 3. 

**NOTE 2** : ***MUST ENABLE FOR CLOUD ENV EXCEPT DEV***, with an exception you want examine the actual data captured in clear form in ***DEV*** only

## docker

The docker image is built on the distroless base image
