variable "aws_region" {
  description = "AWS region for the learning example"
  type        = string
  default     = "ap-northeast-2"
}

variable "instance_type" {
  description = "EC2 instance size; check AWS charges before applying"
  type        = string
  default     = "t3.micro"
}
