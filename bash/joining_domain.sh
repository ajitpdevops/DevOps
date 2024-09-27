#!/bin/bash

# Configure Server for joining Active Directory.

if [ "$EUID" -ne 0 ]; then
    echo "Error: This script requires root privileges. Please run with sudo"
    echo "or execute as root user,  sudo bash"
    exit 1
fi

echo "Required information for joining this host to a domain:"
echo "Hostname:"
echo "Password for account with domain admin credentials to joing the domain"
echo ""

echo "Do you want to continue? (yes/no)"
read response

if [ "$response" == "no" ]; then
    echo "Terminating script."
    exit 1
elif [ "$response" != "yes" ]; then
    echo "Invalid response. Please enter 'yes' or 'no'."
    exit 1
fi



# Read the new hostname from the user
read -p "Enter the hostname: " new_hostname
echo "Hostname is $new_hostname"
echo ""
echo "Enter an AD Domain Aministration Account used to join AD"
read -p "example: srvifsadm: " realmjoinacct
#realmjoinacct=srvifsadm
echo "Domain Admin Account for joining AD is: $realmjoinacct"

echo ""

echo "Validate that the hostname $fqdn does not already exist in DNS"
# Perform DNS lookup
result=$(nslookup "$fqdn")
# Check if the DNS lookup was successful
if [[ $result == *"Name:"* ]]; then
     echo "ERROR: the Error Server $fqdn already exists in DNS"
     exit 1
else
     echo "Server $fqdn does not exist in DNS: continuing"
fi

input_lowercase=$(echo "$new_hostname" | tr '[:upper:]' '[:lower:]')

# Setting up SSSD for active directory integration requires reverse DNS lookup for the domain
# controllers to resolve correctly.  Because DNS is currently provided by AWS and the reverse DNS
# we need to add the domain controllers to the local host files.
# A better solution might be to add the reverse DNS lookup zones to the AWS DC and change the local DNS directly to the # Still needs to research this.

# Perform case-insensitive matching
# TESTING: input_lowercase="ue2pg3xdbsl502"
case "$input_lowercase" in
    ue2d*)
        echo "Updating /etc/hosts"
        domainname="devdomain.int"
        sed -i "/127.0.0.1 localhost/ {
        a\\
10.246.64.56 ue2difsdcx001.devdomain.int ue2difsdcx001
        n
        a\\
10.246.72.56 ue2difsdcx002.devdomain.int ue2difsdcx002
}" "/etc/hosts"
        ;;
    ue2s*)
        echo "Updating /etc/hosts"
        domainname="stgdomain.int"
        sed -i "/127.0.0.1 localhost/ {
        a\\
10.246.32.56 ue2sifsdcx001.stgdomain.int ue2sifsdcx001
        n
        a\\
10.246.40.56 ue2sifsdcx002.stgdomain.int ue2sifsdcx001
}" "/etc/hosts"
        ;;
    ue2p*)
        echo "Updating /etc/hosts"
        domainname="proddomain.int"
        sed -i "/127.0.0.1 localhost/ {
        a\\
10.246.8.40 ue2pifsdcx001.proddomain.int ue2pifsdcx001
        n
        a\\
10.246.16.40 ue2pifsdcx002.proddomain.int ue2pifsdcx002
}" "/etc/hosts"
        ;;
    *)
        echo "aws_hostname $new_hostname invalid"
        # Default action if no match is found
        exit 1
        ;;
esac

fqdn="${new_hostname}.${domainname}"
upperdomain=`echo ${domainname} | tr '[:lower:]' '[:upper:]'`
lowerdomain=`echo ${domainname} | tr '[:upper:]' '[:lower:]'`


echo "Set up the hostname to $fqdn "
sudo hostnamectl set-hostname $new_hostname

dtime=`date`
echo $dtime

# Configure SSSD

FILE="/etc/sssd/sssd.conf"
echo $FILE
echo "-----"

cat > $FILE <<- EOM
[sssd]
domains = LOWERDOMAIN
config_file_version = 2

[domain/LOWERDOMAIN]
default_shell = /bin/bash
krb5_store_password_if_offline = True
cache_credentials = True
krb5_realm = UPPERDOMAIN
realmd_tags = manages-system joined-with-adcli
id_provider = ad
fallback_homedir = /home/%u@%d
ad_domain = LOWERDOMAIN
use_fully_qualified_names = False
ldap_id_mapping = True
access_provider = ad
dyndns_update = false
ad_gpo_ignore_unreadable = True
EOM

sed -i "s/LOWERDOMAIN/${lowerdomain}/g" "$FILE"
sed -i "s/UPPERDOMAIN/${upperdomain}/g" "$FILE"

# Make sure /etc/sssd/sssd.conf permissions are set correctly.
chmod 600 $FILE




FILE="/etc/krb5.conf"
echo $FILE
echo "-----"

cat > $FILE <<- EOM
[libdefaults]

default_realm = UPPERDOMAIN
ticket_lifetime = 24h
renew_lifetime = 7d
[libdefaults]

[realms]
UPPERDOMAIN = {
kdc = UE2XIFSDCX001.LOWERDOMAIN
kdc = UE2XIFSDCX002.LOWERDOMAIN
}

EOM

if [ "$upperdomain" = "DEVDOMAIN.INT" ]; then
        sed -i "s/UE2X/UE2D/g" "$FILE"
elif [ "$upperdomain" = "STGDOMAIN.INT" ]; then
        sed -i "s/UE2X/UE2S/g" "$FILE"
elif [ "$upperdomain" = "PRODDOMAIN.INT" ]; then
        sed -i "s/UE2X/UE2P/g" "$FILE"
else
   echo "INVALIDDOMAIN failing"
fi

sed -i "s/UPPERDOMAIN/${upperdomain}/g" "$FILE"
sed -i "s/LOWERDOMAIN/${lowerdomain}/g" "$FILE"

echo ""
echo $FILE
cat $FILE


# UPDATE PAM CONFIGURATIONS
cp /etc/pam.d/common-session /etc/pam.d/common-session.org
echo "session required pam_mkhomedir.so skel=/etc/skel/ umask=0022" >> /etc/pam.d/common-session

FILE="/usr/share/pam-configs/mkhomedir"
sed -i "s/Default: no/Default: yes/g" "$FILE"
pam-auth-update


echo "You will need the password for ${realmjoinacct} "
echo "kinit ${realmjoinacct}@${upperdomain}"
#echo "Kerberose Credentials "
klist

echo "Joing REALM"
realm join -v -U $realmjoinacct $lowerdomain

echo "Adding Groups to the realm"
realm permit -g 'Domain Admins'
realm permit -g 'G3DBA'
realm permit -g 'G3 Local Admins'
realm permit -g 'G3X-Local-Admin-Access'
realm permit -g 'OpsGroup'


echo "systemctl restart sssd"
systemctl enable sssd
systemctl restart sssd

echo "Allow password authentication vis ssh instead of just keys"
sed -i 's/PasswordAuthentication no/PasswordAuthentication yes/' /etc/ssh/sshd_config
systemctl restart ssh

# Sudo updates
sudo update-alternatives --config editor
idomain="${domainname%.int}"
cp /opt/scripts/sudoers_${idomain} /etc/sudoers.d/
chmod 400 /etc/sudoers.d/sudoers_${idomain}
# Force a reload of the sudoers file
sudo -k && sudo -n true


echo ""
echo "Update DNS entries for this server in Active Directory on the Domain Controller with the following PS Command"
echo ""
recordIPAddress="$(ip -4 addr show scope global | awk '/inet / {print $2}' | cut -d'/' -f1)"
echo "Add-DnsServerResourceRecordA -ZoneName $domainname -Name $new_hostname -IPv4Address $recordIPAddress"
