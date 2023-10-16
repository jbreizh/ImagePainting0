__author__ = 'gav'

import serial
from time import sleep
import sys

def scan():
    """scan for available ports. return a list of tuples (num, name)"""
    available = []
    for i in range(256):
        try:
            s = serial.Serial(i)
            available.append( (i, s.portstr))
            s.close()   # explicit close 'cause of delayed GC in java
        except serial.SerialException:
            pass
    return available

def output_data():
    im_text = open(infilename, 'r')
    port = '/dev/tty.usbserial-A600eLdh'
    print "Opening port: " + port
    ser = serial.Serial(port=port, baudrate=57600, rtscts=0)
    sleep(0)
    wait_pushbutton = True
    ser.flushInput()
    if wait_pushbutton:
        print "Waiting for pushbutton"
        while(ser.inWaiting()==0):
            0
    for line in im_text:
        print "Reading line: " + line
        ser.write(line)
        sleep(time_step )

    print "Finished transmit"
    sleep(10)
    ser = ser.close()

def file_len(fname):
    with open(fname) as f:
        for i, l in enumerate(f):
            pass
    return i + 1

infilenamebase = "output"
if (len(sys.argv)>1):
    infilenamebase = sys.argv[1]

infilename = "images/" + infilenamebase + ".scythe"
print "Opening: ", infilename

time_step = 0.02
num_steps = file_len(infilename)
print "\tNumber of columns is: ", num_steps
print "\tTime per step: ", time_step, ", total time is: ", time_step * num_steps, "seconds"
output_data()

