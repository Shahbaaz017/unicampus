# infrastructure/packer/base-aws-ami.pkr.hcl

# Packer block defines required plugins
packer {
  required_plugins {
    amazon = {
      version = ">= 1.2.0" # Use a recent version
      source  = "github.com/hashicorp/amazon"
    }
    # Add other plugins here if needed (e.g., virtualbox, googlecompute)
  }
}

# Variables block for configuration (improves reusability)
variable "aws_region" {
  type    = string
  default = "us-east-1" # Example: Set your preferred AWS region
}

variable "source_ami_id" {
  type    = string
  default = "" # Optional: Specify a specific source AMI ID
  # If empty, the filter below will be used
}

variable "instance_type" {
  type    = string
  default = "t3.micro" # Choose a cost-effective instance type for building
}

variable "java_version_package" {
  type    = string
  default = "openjdk-17-jre-headless" # Package name for Java 17 JRE on Ubuntu
}

# Source block defines the base image and connection details (AWS EBS example)
source "amazon-ebs" "ubuntu-base" {
  region          = var.aws_region
  instance_type   = var.instance_type
  ssh_username    = "ubuntu" # Default username for Ubuntu cloud images
  ami_name        = "unicampus-base-${formatdate("YYYYMMDD-HHmmss", timestamp())}" # Unique AMI name
  source_ami      = var.source_ami_id # Use variable if set

  # Use filter only if source_ami_id variable is empty
  source_ami_filter {
    filters = {
      name                = "ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*" # Find latest Ubuntu 22.04
      root-device-type    = "ebs"
      virtualization-type = "hvm"
    }
    most_recent = true
    owners      = ["099720109477"] # Canonical's AWS account ID
  }

  tags = {
    Name        = "Unicampus Base Image"
    Project     = "Unicampus"
    OS_Version  = "Ubuntu 22.04"
    Timestamp   = "${timestamp()}"
  }
}

# Build block defines provisioners (how to configure the image)
build {
  name    = "unicampus-base-image"
  sources = ["source.amazon-ebs.ubuntu-base"] # Must match source block name

  # Provisioners run scripts on the instance before image creation
  provisioner "shell" {
    inline = [
      "echo 'Waiting for apt lock...' && sleep 5", # Short delay for safety
      "sudo apt-get update -y",
      "sudo apt-get upgrade -y",
      "echo 'Installing prerequisites...'",
      # Install Java, Docker, and potentially Ansible client if needed on target
      "sudo apt-get install -y --no-install-recommends ${var.java_version_package} docker.io ansible python3-pip",
      "echo 'Enabling Docker service...'",
      "sudo systemctl enable docker", # Ensure Docker starts on boot
      "echo 'Cleaning up apt cache...'",
      "sudo apt-get clean",
      "sudo rm -rf /var/lib/apt/lists/*" # Reduce image size
    ]
  }

  # Optional: Could use Ansible provisioner later if needed
  # provisioner "ansible" {
  #   playbook_file = "../ansible/configure-image.yml" # Example path
  # }
}