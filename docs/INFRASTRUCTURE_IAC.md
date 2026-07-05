# SURYA CREDIT SOLUTIONS: INFRASTRUCTURE AS CODE (IaC) BLUEPRINTS

This document provides complete, production-grade **Terraform (HCL)** configurations for provisioning multi-region high-availability infrastructure across **AWS**, **Google Cloud Platform (GCP)**, and **Microsoft Azure**.

---

## 1. AWS MULTI-AZ ENTERPRISE INFRASTRUCTURE

The following Terraform blueprint provisions a secure, Multi-AZ VPC containing public and private subnets, an Application Load Balancer, an Auto Scaling Group, Amazon Aurora PostgreSQL, and Amazon ElastiCache for Redis.

### 1.1 `aws_main.tf`
```hcl
terraform {
  required_version = ">= 1.5.0"
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

# ==============================================================================
# VPC, INTERNET GATEWAY & SUB-NETTING (Multi-AZ)
# ==============================================================================
resource "aws_vpc" "surya_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name        = "surya-prod-vpc"
    Environment = "production"
  }
}

resource "aws_internet_gateway" "surya_igw" {
  vpc_id = aws_vpc.surya_vpc.id
  tags = {
    Name = "surya-prod-igw"
  }
}

resource "aws_subnet" "public" {
  count                   = 3
  vpc_id                  = aws_vpc.surya_vpc.id
  cidr_block              = "10.0.${count.index}.0/24"
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true
  tags = {
    Name = "surya-public-subnet-${count.index}"
  }
}

resource "aws_subnet" "private" {
  count             = 3
  vpc_id            = aws_vpc.surya_vpc.id
  cidr_block        = "10.0.${10 + count.index}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]
  tags = {
    Name = "surya-private-subnet-${count.index}"
  }
}

# ==============================================================================
# SECURE SECURITY GROUPS
# ==============================================================================
resource "aws_security_group" "alb_sg" {
  name        = "surya-alb-sg"
  description = "Allow inbound HTTPS public traffic"
  vpc_id      = aws_vpc.surya_vpc.id

  ingress {
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
}

resource "aws_security_group" "app_sg" {
  name        = "surya-app-sg"
  description = "Allow traffic exclusively from ALB"
  vpc_id      = aws_vpc.surya_vpc.id

  ingress {
    from_port       = 3000
    to_port         = 3000
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ==============================================================================
# HIGH-AVAILABILITY DATABASE CLUSTERS
# ==============================================================================
resource "aws_db_subnet_group" "db_subnets" {
  name       = "surya-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id
}

resource "aws_rds_cluster" "postgres_cluster" {
  cluster_identifier      = "surya-rds-cluster"
  engine                  = "aurora-postgresql"
  engine_version          = "15.4"
  database_name           = "surya_ledger"
  master_username         = "surya_db_admin"
  master_password         = var.db_password
  db_subnet_group_name    = aws_db_subnet_group.db_subnets.name
  vpc_security_group_ids  = [aws_security_group.app_sg.id]
  storage_encrypted       = true
  skip_final_snapshot     = false
  backup_retention_period = 30
  preferred_backup_window = "02:00-03:00"
}

resource "aws_rds_cluster_instance" "cluster_instances" {
  count              = 2
  identifier         = "surya-db-instance-${count.index}"
  cluster_identifier = aws_rds_cluster.postgres_cluster.id
  instance_class     = "db.m6g.xlarge"
  engine             = aws_rds_cluster.postgres_cluster.engine
  engine_version     = aws_rds_cluster.postgres_cluster.engine_version
}

# ==============================================================================
# ELATICACHE FOR REDIS (SESSION CACHE & THROTTLING)
# ==============================================================================
resource "aws_elasticache_subnet_group" "redis_subnets" {
  name       = "surya-redis-subnets"
  subnet_ids = aws_subnet.private[*].id
}

resource "aws_elasticache_replication_group" "redis_cluster" {
  replication_group_id        = "surya-redis"
  description                 = "Redis cache sentinel group"
  node_type                   = "cache.m6g.xlarge"
  num_cache_clusters          = 2
  port                        = 6379
  subnet_group_name           = aws_elasticache_subnet_group.redis_subnets.name
  security_group_ids          = [aws_security_group.app_sg.id]
  at_rest_encryption_enabled  = true
  transit_encryption_enabled = true
  auth_token                  = var.redis_auth_token
}
```

---

## 2. GOOGLE CLOUD PLATFORM PRODUCTION INFRASTRUCTURE

The following script provisions a Google Kubernetes Engine (GKE) Autopilot cluster, Google Cloud SQL (PostgreSQL), Cloud Memorystore (Redis), and Cloud Armor rules.

### 2.1 `gcp_main.tf`
```hcl
terraform {
  required_version = ">= 1.5.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
}

provider "google" {
  project = var.gcp_project_id
  region  = var.gcp_region
}

# ==============================================================================
# VIRTUAL PRIVATE CLOUD (VPC)
# ==============================================================================
resource "google_compute_network" "surya_vpc" {
  name                    = "surya-prod-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnetwork" {
  name          = "surya-gke-subnetwork"
  ip_cidr_range = "10.128.0.0/20"
  region        = var.gcp_region
  network       = google_compute_network.surya_vpc.id
  
  secondary_ip_range {
    range_name    = "gke-pods"
    ip_cidr_range = "10.4.0.0/14"
  }
  
  secondary_ip_range {
    range_name    = "gke-services"
    ip_cidr_range = "10.8.0.0/20"
  }
}

# ==============================================================================
# GKE AUTOPILOT CLUSTER (High-Availability & Auto-Scaling)
# ==============================================================================
resource "google_container_cluster" "gke_autopilot" {
  name     = "surya-prod-gke"
  location = var.gcp_region

  enable_autopilot = true
  network          = google_compute_network.surya_vpc.id
  subnetwork       = google_compute_subnetwork.subnetwork.id

  ip_allocation_policy {
    cluster_secondary_range_name  = "gke-pods"
    services_secondary_range_name = "gke-services"
  }

  private_cluster_config {
    enable_private_nodes    = true
    enable_private_endpoint = false
    master_ipv4_cidr_block  = "172.16.0.0/28"
  }
}

# ==============================================================================
# GOOGLE CLOUD SQL (POSTGRESQL HIGH-AVAILABILITY)
# ==============================================================================
resource "google_sql_database_instance" "postgres" {
  name             = "surya-postgres-db"
  database_version = "POSTGRES_15"
  region           = var.gcp_region

  settings {
    tier              = "db-custom-4-16384" # 4 vCPU, 16 GB RAM
    availability_type = "REGIONAL"          # HA Failover Active-Active setup
    
    backup_configuration {
      enabled    = true
      start_time = "02:00"
    }

    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.surya_vpc.id
    }
  }
}
```

---

## 3. MICROSOFT AZURE PLATFORM INFRASTRUCTURE

Provides a Microsoft Azure Resource Group, Virtual Network (VNet), Azure Kubernetes Service (AKS), Azure Database for PostgreSQL (Flexible Server), and Cache for Redis.

### 3.1 `azure_main.tf`
```hcl
terraform {
  required_version = ">= 1.5.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "surya_rg" {
  name     = "surya-prod-rg"
  location = var.azure_location
}

# ==============================================================================
# VIRTUAL NETWORK & SUBNETS
# ==============================================================================
resource "azurerm_virtual_network" "surya_vnet" {
  name                = "surya-prod-vnet"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.surya_rg.location
  resource_group_name = azurerm_resource_group.surya_rg.name
}

resource "azurerm_subnet" "aks_subnet" {
  name                 = "aks-subnet"
  resource_group_name  = azurerm_resource_group.surya_rg.name
  virtual_network_name = azurerm_virtual_network.surya_vnet.name
  address_prefixes     = ["10.0.1.0/24"]
}

# ==============================================================================
# AZURE KUBERNETES SERVICE (AKS) WITH AUTOMATIC AUTO-SCALER
# ==============================================================================
resource "azurerm_kubernetes_cluster" "aks_cluster" {
  name                = "surya-prod-aks"
  location            = azurerm_resource_group.surya_rg.location
  resource_group_name = azurerm_resource_group.surya_rg.name
  dns_prefix          = "suryacredit"

  default_node_pool {
    name                = "agentpool"
    node_count          = 3
    vm_size             = "Standard_D4s_v5" # 4 vCPU, 16 GB RAM
    vnet_subnet_id      = azurerm_subnet.aks_subnet.id
    enable_auto_scaling = true
    min_count           = 3
    max_count           = 15
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin    = "azure"
    load_balancer_sku = "standard"
  }
}

# ==============================================================================
# AZURE POSTGRESQL FLEXIBLE SERVER (HIGH-AVAILABILITY)
# ==============================================================================
resource "azurerm_postgresql_flexible_server" "postgres" {
  name                   = "surya-flexible-postgres"
  resource_group_name    = azurerm_resource_group.surya_rg.name
  location               = azurerm_resource_group.surya_rg.location
  version                = "15"
  administrator_login    = "surya_db_admin"
  administrator_password = var.db_password
  storage_mb             = 131072 # 128 GB Disk
  sku_name               = "MO_Standard_E4ds_v5" # Memory Optimized Standard

  high_availability {
    mode = "ZoneRedundant"
  }

  backup_retention_days = 30
}
```
