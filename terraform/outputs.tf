############################################################
# EC2
############################################################

output "instance_id" {
  description = "Sentinel EC2 Instance ID"
  value       = aws_instance.sentinel.id
}

output "public_ip" {
  description = "Public IP"
  value       = aws_instance.sentinel.public_ip
}

output "public_dns" {
  description = "Public DNS"
  value       = aws_instance.sentinel.public_dns
}

output "ssh_command" {

  description = "SSH Command"

  value = "ssh -i <YOUR_KEY>.pem ubuntu@${aws_instance.sentinel.public_ip}"

}

############################################################
# DynamoDB
############################################################

output "dynamodb_table_name" {

  value = aws_dynamodb_table.sentinel_events.name

}

############################################################
# SNS
############################################################

output "sns_topic_arn" {

  value = aws_sns_topic.sentinel_alerts.arn

}

############################################################
# Security Group
############################################################

output "security_group_id" {

  value = aws_security_group.sentinel.id

}