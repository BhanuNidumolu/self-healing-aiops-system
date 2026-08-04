############################################################
# IAM ROLE
############################################################

resource "aws_iam_role" "sentinel_role" {

  name = "${var.project_name}-ec2-role"

  assume_role_policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = {
          Service = "ec2.amazonaws.com"
        }

        Action = "sts:AssumeRole"

      }

    ]

  })

  tags = {
    Name = "${var.project_name}-ec2-role"
  }

}

############################################################
# CUSTOM POLICY
############################################################

resource "aws_iam_policy" "sentinel_policy" {

  name = "${var.project_name}-policy"

  description = "Sentinel EC2 Permissions"

  policy = jsonencode({

  Version = "2012-10-17"

  Statement = [

    {
      Sid = "CloudWatch"

      Effect = "Allow"

      Action = [

        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:DescribeLogGroups",
        "logs:DescribeLogStreams"

      ]

      Resource = "*"
    },

    {
      Sid = "DynamoDB"

      Effect = "Allow"

      Action = [

        "dynamodb:GetItem",
        "dynamodb:PutItem",
        "dynamodb:UpdateItem",
        "dynamodb:Query",
        "dynamodb:Scan"

      ]

      Resource = aws_dynamodb_table.sentinel_events.arn
    },

    {
      Sid = "SNS"

      Effect = "Allow"

      Action = [

        "sns:Publish"

      ]

      Resource = aws_sns_topic.sentinel_alerts.arn
    }

  ]

})

}

############################################################
# POLICY ATTACHMENT
############################################################

resource "aws_iam_role_policy_attachment" "custom_policy" {

  role = aws_iam_role.sentinel_role.name

  policy_arn = aws_iam_policy.sentinel_policy.arn

}

############################################################
# SSM
############################################################

resource "aws_iam_role_policy_attachment" "ssm" {

  role = aws_iam_role.sentinel_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"

}

############################################################
# CLOUDWATCH AGENT
############################################################

resource "aws_iam_role_policy_attachment" "cloudwatch" {

  role = aws_iam_role.sentinel_role.name

  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"

}

############################################################
# INSTANCE PROFILE
############################################################

resource "aws_iam_instance_profile" "sentinel_profile" {

  name = "${var.project_name}-instance-profile"

  role = aws_iam_role.sentinel_role.name

}