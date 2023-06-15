#!/bin/bash


# Variable declaration
PACKAGE="apche2 wget unzip"
SVC="apache2"
URL="https://www.tooltape.com/zip-templates/2098_health.zip"
ART_NAME='2098_health'
TEMP_DIR='/tmp/webfiles'
WEB_DIR='/var/www/html'

# Install packages
echo "####################################################"
echo "Installing packages"
echo "####################################################"
sudo apt-get install -y $PACKAGE > dev/null

echo "####################################################"
echo "Starting the Service"
echo "####################################################"
# Start and enable service
sudo systemctl start $SVC
sudo systemctl enable $SVC

# Create temp directory
echo "####################################################"
echo "Create a temp directory"
echo "####################################################"
mkdir -p $TEMP_DIR
cd $TEMP_DIR

# Download and unzip files
echo "####################################################"
echo "Download and unzip files"
echo "####################################################"
wget $URL
unzip $ART_NAME.zip
cp -r $ART_NAME/* $WEB_DIR

# Remove temp files
rm -rf $TEMP_DIR

# restart apache
sudo systemctl restart $SVC
echo "####################################################"
echo "Apache is up and running"
echo "####################################################"

