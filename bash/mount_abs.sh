#!/bin/bash

# Manage the exit status of the Script 
#set -e -u -x

# We specify the EBS Volume mount variable
[ -n "${FS_TYPE:-}" ]   || FS_TYPE="ext4"
[ -n "${FS_OPTS:-}" ]   || FS_OPTS="errors=remount-ro,nofail,noatime,nodiratime"
[ -n "${FS_LABEL:-}" ]  || FS_LABEL="data-vol"
[ -n "${MK_PARTITION:-}" ] || MK_PARTITION="0"

# We validate the arguments passed to the Script when it's called
if [ $# -gt 0 ] ; then
    if [ "$1" = "-h" -o "$1" = "--help" ] ; then
        echo "Usage: $0 [TAG_NAME MOUNT_DEVICE MOUNT_DIR]"
        echo ""
        echo "You can omit the above arguments if there are environment variables of the same name."
        #exit 1
    fi
    if [ $# -eq 3 ] ; then
        TAG_NAME="$1"
        MOUNT_DEVICE="$2"
        MOUNT_DIR="$3"
        shift 3
    fi
fi

_has_fs () {
    blkid "$MOUNT_DEVICE" | grep -i "$FS_TYPE"
}


_mk_part () {
    # If an initial partition doesn't exist, create one.
    [ -n "${PARTED_SCRIPT:-}" ] || PARTED_SCRIPT="mklabel gpt mkpart primary 0% 100%"
    [ -n "${SFDISK_SCRIPT:-}" ] || SFDISK_SCRIPT="label: gpt\n;"
    if ! blkid "$MOUNT_DEVICE" ; then
        # Where supported: GPT partition table and a single large initial partition
        if command -v parted >/dev/null ; then
            parted -a opt --script "$MOUNT_DEVICE" "$PARTED_SCRIPT"
        elif command -v sfdisk >/dev/null ; then
            printf "$SFDISK_SCRIPT\n" | sfdisk "$MOUNT_DEVICE"
        fi
    fi

    # default new partition: append "1" to $MOUNT_DEVICE
    MOUNT_DEVICE="$MOUNT_DEVICE"1
    # wait for partition to show up
    for i in `seq 1 30` ; do
        [ -b "$MOUNT_DEVICE" ] && break
        sleep 1
    done
}

_mk_fs () {
    if ! _has_fs "$MOUNT_DEVICE" ; then
        # Try to just mount it, just in case filesystem detection failed
        if ! mount -t "$FS_TYPE" -o "$FS_OPTS" "$MOUNT_DEVICE" "$MOUNT_DIR" ; then
            # Oh well, we gave it a shot. Format the partition.
            # Both 'ext4' and 'xfs' support a '-L' option for partition label, so might
            # as well tack that on
            mkfs.$FS_TYPE -L "$FS_LABEL" "$MOUNT_DEVICE"
        fi
    fi
}


# Prepend '/dev/' if missing
expr match "$MOUNT_DEVICE" /dev/ >/dev/null || MOUNT_DEVICE="/dev/$MOUNT_DEVICE"

if [ ! -b "$MOUNT_DEVICE" ] ; then
    echo "$0: Error: failed to find block device '$MOUNT_DEVICE'"
    exit 1
fi

[ -d "$MOUNT_DIR" ] || mkdir -p "$MOUNT_DIR"

# Create a filesystem if it doesn't exist yet (new volumes)
if ! _has_fs "$MOUNT_DEVICE" ; then

    # Set MK_PARTITION=1 to create and/or use a partition for the MOUNT_DEVICE
    if [ "${MK_PARTITION:-}" = "1" ] ; then
        _mk_part
    fi

    _mk_fs
fi

mount -t "$FS_TYPE" -o "$FS_OPTS" "$MOUNT_DEVICE" "$MOUNT_DIR"

# Add new mount to /etc/fstab
grep -q -e "[[:space:]]$MOUNT_DIR[[:space:]]" /etc/fstab || echo "$MOUNT_DEVICE  $MOUNT_DIR  $FS_TYPE  $FS_OPTS  1  2" >> /etc/fstab