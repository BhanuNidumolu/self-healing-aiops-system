############################################################
# DATA SOURCES
############################################################

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {

  filter {

    name = "vpc-id"

    values = [
      data.aws_vpc.default.id
    ]

  }

}

data "aws_ami" "ubuntu" {

  most_recent = true

  owners = ["099720109477"]

  filter {

    name = "name"

    values = [
      "ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"
    ]

  }

  filter {

    name = "virtualization-type"

    values = ["hvm"]

  }

}

############################################################
# EC2 INSTANCE
############################################################

resource "aws_instance" "sentinel" {

  ami = data.aws_ami.ubuntu.id

  instance_type = var.instance_type

  key_name = var.key_name

  subnet_id = data.aws_subnets.default.ids[0]

  associate_public_ip_address = true

  vpc_security_group_ids = [
    aws_security_group.sentinel.id
  ]

  iam_instance_profile = aws_iam_instance_profile.sentinel_profile.name

  user_data = base64encode(
    templatefile(
      "${path.module}/scripts/bootstrap.sh",
      {
        api_key = var.api_key
      }
    )
  )

  root_block_device {

    volume_size = var.root_volume_size

    volume_type = "gp3"

    delete_on_termination = true

  }

  metadata_options {

    http_endpoint = "enabled"

    http_tokens = "required"

  }

  tags = {

    Name = "${var.project_name}-ec2"

    Environment = var.environment

  }

}