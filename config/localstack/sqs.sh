#!/bin/bash

awslocal sqs create-queue --queue-name change-stream-queue
SUBSCRIPTION_ARN=$(awslocal sns subscribe --protocol sqs --topic-arn arn:aws:sns:eu-west-2:000000000000:stream-topic --notification-endpoint arn:aws:sqs:eu-west-2:000000000000:change-stream-queue --query 'SubscriptionArn' --output text)
awslocal sns set-subscription-attributes --subscription-arn "$SUBSCRIPTION_ARN" --attribute-name FilterPolicy --attribute-value "{\"x-dwp-routing-key\":{\"prefix\": \"stream\"}}"
awslocal sns get-subscription-attributes --subscription-arn "$SUBSCRIPTION_ARN"
