############################################################
# SECURITY GROUP
############################################################

resource "aws_security_group" "sentinel" {

  name        = "${var.project_name}-sg"
  description = "Security Group for Sentinel Platform"
  vpc_id      = data.aws_vpc.default.id

  tags = {
    Name = "${var.project_name}-sg"
  }
}

############################################################
# SSH
############################################################

resource "aws_vpc_security_group_ingress_rule" "ssh" {

  security_group_id = aws_security_group.sentinel.id

  cidr_ipv4 = var.my_ip

  from_port = 22
  to_port   = 22

  ip_protocol = "tcp"

  description = "SSH Access"
}

############################################################
# HTTP
############################################################

resource "aws_vpc_security_group_ingress_rule" "http" {

  security_group_id = aws_security_group.sentinel.id

  cidr_ipv4 = "0.0.0.0/0"

  from_port = 80
  to_port   = 80

  ip_protocol = "tcp"

  description = "NGINX"
}

############################################################
# HTTPS
############################################################

resource "aws_vpc_security_group_ingress_rule" "https" {

  security_group_id = aws_security_group.sentinel.id

  cidr_ipv4 = "0.0.0.0/0"

  from_port = 443
  to_port   = 443

  ip_protocol = "tcp"

  description = "HTTPS"
}

############################################################
# Supervisor API
############################################################

resource "aws_vpc_security_group_ingress_rule" "supervisor" {

  security_group_id = aws_security_group.sentinel.id

  cidr_ipv4 = var.my_ip

  from_port = 8086
  to_port   = 8086

  ip_protocol = "tcp"

  description = "Supervisor API"
}

############################################################
# Monitored Service (Testing Only)
############################################################

resource "aws_vpc_security_group_ingress_rule" "monitored" {

  security_group_id = aws_security_group.sentinel.id

  cidr_ipv4 = var.my_ip

  from_port = 8081
  to_port   = 8081

  ip_protocol = "tcp"

  description = "Monitored Service"
}

############################################################
# EGRESS
############################################################

resource "aws_vpc_security_group_egress_rule" "all" {

  security_group_id = aws_security_group.sentinel.id

  cidr_ipv4 = "0.0.0.0/0"

  ip_protocol = "-1"

  description = "Allow All Outbound"
}