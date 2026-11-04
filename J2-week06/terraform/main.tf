# 학습용 예제: terraform apply 시 실제 AWS 자원이 생성되어 비용이 발생할 수 있습니다.
terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_vpc" "study" {
  cidr_block           = "10.20.0.0/16"
  enable_dns_hostnames = true

  tags = { Name = "week06-study-vpc" }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.study.id
  cidr_block              = "10.20.1.0/24"
  map_public_ip_on_launch = true

  tags = { Name = "week06-public-subnet" }
}

resource "aws_internet_gateway" "study" {
  vpc_id = aws_vpc.study.id
  tags   = { Name = "week06-igw" }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.study.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.study.id
  }

  tags = { Name = "week06-public-rt" }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "web" {
  name_prefix = "week06-web-"
  description = "Example: public HTTP/HTTPS only, no public SSH/DB/Redis"
  vpc_id      = aws_vpc.study.id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "week06-web-sg" }
}

resource "aws_instance" "study" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.web.id]
  associate_public_ip_address = true

  # 예제는 VPC/EC2 구조 확인용이며 앱·Docker·SSM 설정은 포함하지 않습니다.
  tags = { Name = "week06-study-ec2" }
}
