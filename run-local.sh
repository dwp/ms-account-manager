#!/bin/bash

TOTP_BASE_URL=http://wm-totp-authenticator:8080 \
TOTP_VERIFY_PATH=/v1/totp/verify \
TOTP_GENERATE_PATH=/v1/totp/generate \
AWS_ENCRYPTION_MESSAGE_DATA_KEY_ID=alias/test_event_request_id \
mvn spring-boot:run
