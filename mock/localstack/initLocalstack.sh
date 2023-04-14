#!/bin/sh

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
export AWS_REGION=us-east-1

create_sns_queue() {
  echo "Creating SNS Topic - '$1'"
  /home/aws/aws/env/bin/aws --endpoint-url=http://localstack:4566 sns create-topic --name "$1"
}

create_sqs_queue() {
  echo "Creating SQS queue '$1'"
  /home/aws/aws/env/bin/aws --endpoint-url=http://localstack:4566 sqs create-queue --queue-name "$1"
}

subscribe_queue_to_topic() {
  echo "Subscribing '$1' to '$2' with routing-key prefix '$3'"
  subHandlerSubArn=$(/home/aws/aws/env/bin/aws --endpoint-url=http://localstack:4566 sns subscribe --topic-arn "arn:aws:sns:us-east-1:000000000000:$1" --protocol sqs --notification-endpoint "arn:aws:sqs:elasticmq:000000000000:$2")
  echo "$subHandlerSubArn"
  subHandlerSubArn=$(echo "$subHandlerSubArn" | awk '{ print $3 }' | sed 's/\"//g')
  /home/aws/aws/env/bin/aws --endpoint-url=http://localstack:4566 sns set-subscription-attributes --subscription-arn "$subHandlerSubArn" --attribute-name FilterPolicy --attribute-value "{\"x-dwp-routing-key\":{\"prefix\": \"$3\"}}"
}

create_kms_key() {
  keyId=$(/home/aws/aws/env/bin/aws kms create-key --endpoint-url http://localstack:4566 --output=text | sed -E 's/.*('"[0-9a-z]{8}\-[0-9a-z]{4}\-[0-9a-z]{4}\-[0-9a-z]{4}\-[0-9a-z]{12}"').*/\1/')
  echo "created KMS key '$keyId' for alias '$1'"
  /home/aws/aws/env/bin/aws kms create-alias --endpoint-url http://localstack:4566 --alias-name "$1" --target-key-id "$keyId"
}

# TOPIC
create_sns_queue "stream-topic"

sleep 10
# QUEUES (and subscriptions)
create_sqs_queue "change-stream-queue"

sleep 10

subscribe_queue_to_topic "stream-topic" "change-stream-queue" "stream"

# KMS
create_kms_key "alias/test_mongo_request_id"
create_kms_key "alias/test_event_request_id"
