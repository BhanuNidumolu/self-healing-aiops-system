############################################################
# SNS Topic
############################################################

resource "aws_sns_topic" "sentinel_alerts" {

  name = "${var.project_name}-alerts"

  tags = {

    Name = "${var.project_name}-alerts"

    Environment = var.environment

  }

}

############################################################
# Optional Email Subscription
############################################################

resource "aws_sns_topic_subscription" "email" {

  count = var.alert_email == "" ? 0 : 1

  topic_arn = aws_sns_topic.sentinel_alerts.arn

  protocol = "email"

  endpoint = var.alert_email

}