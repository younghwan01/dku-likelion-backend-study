output "vpc_id" {
  description = "Created VPC ID"
  value       = aws_vpc.study.id
}

output "ec2_public_ip" {
  description = "Public EC2 IP, if the example resources are created"
  value       = aws_instance.study.public_ip
}
