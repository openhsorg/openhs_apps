# Arduino binding

1. Requires **gui.io.rxt** bundle
2. Please note that on Ubuntu 11.04, the Arduino Uno and possibly others are recognised as /dev/ttyACMxx . The RXTX library only searches through /dev/ttySxx, so you need to make symlinks if your distro does the same, so for example ln -s /dev/ttyACM0 /dev/ttyS33 .
Besides that, you need to close the serial port after starting, to prevent Linux from making new devices, like /dev/ttyACM2. Do not forget to remove the lock file from /var/lock if you forgot to close the port.