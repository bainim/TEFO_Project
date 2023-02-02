#!/bin/bash

# Update the system
echo "Updating The System..."
sudo apt-get -y update
echo "Install snapd..."
sudo apt install snapd
echo "Add snapd PATH to env var..."
#sudo su
#cat /etc/environment | xargs -I{} echo {}  ":/snap/bin/" | tr -d " " > /etc/environment
#exit
export PATH=$PATH:/snap/bin
echo "Done"
echo "---------------------------------------------------------------------"
printf "\n\n\n"


# Install docker & docker-composer
echo "Installing docker & docker-composer..."
sudo snap install docker
echo "Done"
echo "---------------------------------------------------------------------"
printf "\n\n\n"

# Configure docker to run without sudo
echo "Configure docker to run without sudo..."
sudo groupadd docker
sudo usermod -aG docker ${USER}
sudo chown "$USER":"$USER" "/var/run/docker.sock" -R
sudo chmod g+rwx "/var/run/docker.sock" -R
echo "Done"
echo "---------------------------------------------------------------------"
printf "\n\n\n"

# Install Java 8
echo "Install Java 8..."
sudo apt -y install openjdk-8-jdk
echo "Done"
echo "---------------------------------------------------------------------"
printf "\n\n\n"


# Install Maven
echo "Install Maven..."
sudo apt -y install maven
echo "Done"
echo "---------------------------------------------------------------------"
printf "\n\n\n"

# Build Backend
echo "Build Backend..."
rm -rf target/* && mvn package
echo "Done"
echo "---------------------------------------------------------------------"
printf "\n\n\n"


# Start The Stack
sudo docker-compose up -d backend_empreinte_carbone_container


#docker build -t sala7li_docker_back . && docker run -p 8080:8080 --name Sala7li-back --network host sala7li_docker_back
#rm -rf target/* && mvn package

#S@1a711






















