provider "google" {
  credentials = file("./gkey.json")
  project     = "cloudcomputing-388816"
  region      = "us-west2"
}

resource "google_container_cluster" "my_cluster" {
  name               = "my-cluster"
  location           = "us-west2"
  initial_node_count = 1

  node_config {
    machine_type        = "e2-micro"
    disk_size_gb        = 10
    disk_type           = "pd-standard"
    image_type          = "COS_CONTAINERD"
    preemptible         = false
    oauth_scopes        = ["https://www.googleapis.com/auth/cloud-platform"]
    metadata            = {
      disable-legacy-endpoints = "true"
    }
  }

  master_auth {
    client_certificate_config {
      issue_client_certificate = false
    }
  }
}

