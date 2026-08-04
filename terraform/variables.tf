##############################
# Project
##############################

variable "project_name" {
  description = "Project Name"
  type        = string
  default     = "sentinel"
}

variable "environment" {
  description = "Environment"
  type        = string
  default     = "dev"
}

##############################
# AWS
##############################

variable "aws_region" {
  description = "AWS Region"
  type        = string
  default     = "ap-south-1"
}

variable "instance_type" {
  description = "EC2 Instance Type"
  type        = string
  default     = "t2.micro"
}

variable "key_name" {
  description = "AWS EC2 Key Pair Name"
  type        = string
}

variable "my_ip" {
  description = "Your Public IP with CIDR (Example: 103.21.xx.xx/32)"
  type        = string
}

##############################
# Application
##############################

variable "api_key" {
  description = "Groq API Key (Temporary until IncidentIQ is integrated)"
  type        = string
  sensitive   = true
}

##############################
# Storage
##############################

variable "root_volume_size" {
  description = "EC2 Root Volume Size (GB)"
  type        = number
  default     = 20
}

##############################
# Alerts
##############################

variable "alert_email" {
  description = "SNS Email Subscription"
  type        = string
  default     = ""
}