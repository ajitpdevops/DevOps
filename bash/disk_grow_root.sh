#!/bin/bash

# Owner:  Ajit Patil
# Script to hot add additional disk space added from VMWARE.
#
# There are many options for adding and expanding the root disk space.  The only one
# That this scripts addresses is the expansion of the orginal disk /dev/sda
#

# Set the Volume Group and Logical Volume we will be working with.

# Check if the script is being run as root

# TODO:   Add support for adding new drives instead of expanding drives on /dev/sdba

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

VG1=ubuntu-vg
LV1=ubuntu-lv
PD1=sda
PD2=sdb
LV_PATH="/dev/${VG1}/${LV1}"

prelvsize=$(lvdisplay $LV_PATH | awk '/LV Size/ {print $3}')
prerootdiskspaceuse=$(df -m / | tail -1 | awk '{print $5'})


# If the disk drive was hot added we need to rescan for new devices
echo "Forcing a rescan of scsi devices"
echo ""
declare -a scsidev=(`ls /sys/class/scsi_device`)
for i in "${scsidev[@]}"
do
      echo $i
      echo "/sys/class/scsi_device/$i/device/rescan"
      echo 1 > /sys/class/scsi_device/$i/device/rescan
done

echo ""
echo "The following creates a new primary disk from an expanded VMWARE disk"
echo "Up to 4 can be created /dev/${PD1}1 - /dev/${PD1}4"
echo ""
echo "By default: /dev/${PD1}2 is boot /dev/${PD1}3 is root"
echo "This leaves up to 2 additional expansions of the primary drive"
echo " "
echo "If the existing disk drive is expanded in Vsphere,  we will be expanding the disk drive"
echo ""
echo "We will be adding the expanded space  the ${VG1} Volume group"
echo "This will then be added to the ${LV1} logical volume group"
echo " "

lvdisplay $LV_PATH | awk '/LV Size/ {print $3}'



physical_volume="/dev/$PD1"

unallocated_space=$(parted -s "$physical_volume" unit GB print free | awk '/Free Space/ {total += $3} END {print total}')
unallocated_space=${unallocated_space%.*}

echo " "
echo "Unallocated DiskSpace = $unallocated_space"
echo " "

# Check if there is unallocated disk space
if [[ $unallocated_space -gt 10 ]]; then
  echo "There is more than 10GB of unallocated disk space on $physical_volume"
  expandvg="true"
else
  echo "Not sufficitent unallocated disk space on $physical_volume ;  will not expand"
  expandvg="false"
fi


# Change this to True
if [[ "$expandvg" == "true" ]]; then
   physical_volumes=$(pvdisplay -C --separator '|' --units b --noheadings | awk -F '|' '{print $1}' | sort)
   last_volume=$(pvdisplay -C --separator '|' --units b --noheadings | awk -F '|' '{print $1}' | sort | tail -1)
   echo "Expanding disk space on $last_volume "


  # Print the sorted list of physical volumes
  echo ""
  echo "Sorted list of physical volumes:"
  echo " $physical_volumes"
  echo ""
  echo "Last physical volume will be used to expand. Typically this is /dev/sda3 or /dev/sda4:"
  echo " $last_volume"
  partition=$(echo $last_volume | rev | cut -c 1 )
  echo "Partition is $partition"
  #parted -s /dev/sda resizepart $partition 100%
  echo "Resizing partition $partition on disk $physical_volume"
  growpart "$physical_volume" "$partition"
  if [[ $? -ne 0 ]]; then
     echo "Failed to resize partition $partition on disk $physical_volume"
     exit 1
  fi
  # run Partprobe to  refresh partition table"
  echo "running partprobe to on $physical_volume to refresh the partition table"
  partprobe $physical_volume
  if [[ $? -ne 0 ]]; then
     echo "Failed to run partprobe on $physical_volume to refresh partition table"
     exit 1
  fi

  # Resize the file system using resize2fs
  echo "PVresize on file system on partition $last_volume"
  pvresize -y $last_volume
  # Check if pvresize command succeeded
  if [[ $? -ne 0 ]]; then
    echo "Failed to run pvresize resize file $last_volume"
    exit 1
  fi

  # Resize the Logical Volume
  echo "Resize the Logical volume $LV_PATH"
  lvresize -r -l +100%FREE "$LV_PATH"
  # Check if lvresize command succeeded
  if [[ $? -ne 0 ]]; then
    echo "Failed to run lvresize on $LV_PATH"
    exit 1
  fi

  echo "Disk expansion completed successfully"
  echo "Previous Logical Volume Size $LV_PATH is:  $prelvsize"
  echo "Previous disk space usage% for root / is ${prerootdiskspaceuse}"
  echo ""
  newlvsize=$(lvdisplay $LV_PATH | awk '/LV Size/ {print $3}')
  newrootdiskspaceuse=$(df -m / | tail -1 | awk '{print $5'})
  echo "New Logical Volume Size $LV_PATH is:  $newlvsize"
  echo "New disk space usage% for root / is ${newrootdiskspaceuse}"

fi

