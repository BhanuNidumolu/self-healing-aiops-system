############################################################
# DynamoDB Table
############################################################

resource "aws_dynamodb_table" "sentinel_events" {

  name = "${var.project_name}-events"

  billing_mode = "PAY_PER_REQUEST"

  hash_key = "incidentId"

  range_key = "timestamp"

  attribute {
    name = "incidentId"
    type = "S"
  }

  attribute {
    name = "timestamp"
    type = "N"
  }

  attribute {
    name = "serviceName"
    type = "S"
  }

  global_secondary_index {

    name = "ServiceIndex"

    hash_key = "serviceName"

    range_key = "timestamp"

    projection_type = "ALL"

  }

  server_side_encryption {

    enabled = true

  }

  point_in_time_recovery {

    enabled = true

  }

  deletion_protection_enabled = false

  tags = {

    Name = "${var.project_name}-events"

    Component = "Storage"

    Environment = var.environment

  }

}