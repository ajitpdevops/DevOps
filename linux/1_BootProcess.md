BIOS Vs UEFI 

GRUB Vs GRUB2 

GRUB "legacy"
menu.lst grub.conf 
difficult to modify 
boot menu usuall 


grub.cfg 
customization in /etc/default/grub 
Can boot ISO/USB
hidden boot menu 


Boot Methods 
- Hardware vs Software
- PXE, USB, CD, iPXE, ISO
- PXE 
	- Preenvironment Executable Env
	- Network boot 		
	- 

BIOS/UEFI -> GRUB/GRUB2 -> ulinux & unlinuz -> Full Kernel - mod - mod -mod 
	- initrd 
	- InitramFS (dracut) tiny File system that is part of kernel which is loaded on RAM


Kernel Panic ! 
	- Something went wrong with Kernel 
	- Can be faulty hardware
	- Overclocked CPU 
	- Fault RAM / USB Card 
	- Faulty Video card 
	- When you upgrade the system and install a new kernel 
		- You can rescue by restoring 
		- Reboot -> go to grub -> use the old kernel 
		- Linux keeps a few kernels 
	- 

Loading Kernel modules on boot 
	- it loads the modules dynamically 
	- when modules are loaded there is a dependancy checking happening in the background 
	- Loading & Blacklisting of modules can be done 
		- /etc/module
		- /etc/modprobe.d/blacklist.conf 
	- Properly manipulate kernel modules 
		- insmod 
		- modprobe - more advanced programs 
		- 

	
	
# Import File to look at - 

/etc/default/grub :- Contains the GRUB configuration
/etc/grub.d :- Contains the GRUB scripts
/etc/grub.d/40_custom :- Contains the custom GRUB configuration

/etc/fstab :- Contains the filesystem mount information
/etc/crypttab :- Contains the encrypted filesystem information

/etc/initramfs-tools/initramfs.conf : Contains the initramfs configuration
/etc/initramfs-tools/modules : Contains the modules to be loaded in initramfs

/etc/sudoers :- Contains the sudo configuration
/etc/sudoers.d :- Contains the sudo configuration files

/etc/hostname :- Contains the hostname of the system
/etc/hosts :- Contains the IP address and hostname of the system
/etc/resolv.conf :- Contains the DNS server IP address
/etc/network/interfaces :- Contains the network interface configuration

/etc/ssh/sshd_config :- Contains the SSH server configuration
/etc/ssh/ssh_config :- Contains the SSH client configuration
/etc/ssh/ssh_host_rsa_key :- Contains the SSH RSA key
/etc/ssh/ssh_host_dsa_key :- Contains the SSH DSA key

/etc/sysctl.conf :- Contains the kernel parameters
/etc/sysctl.d :- Contains the kernel parameters configuration files

/etc/udev/rules.d :- Contains the udev rules
/etc/udev/udev.conf :- Contains the udev configuration

/etc/modules :- Contains the kernel modules to be loaded on boot
/etc/modprobe.d :- Contains the kernel modules configuration files
/etc/modprobe.d/blacklist.conf :- Contains the kernel modules to be blacklisted

/lib/modules :- Contains the kernel modules
/lib/modules/<kernel_version>/kernel/drivers :- Contains the kernel modules
/lib/modules/<kernel_version>/kernel/drivers/net :- Contains the network kernel modules
- Important Commands 
	- lsmod - list kernel modules 
	- rmmod - Remove Modules 
	- depmod - to update the mods 

/lib/firmware :- Contains the firmware files

/proc/cmdline :- Contains the kernel boot parameters
/proc/cpuinfo :- Contains the CPU information


