#!/usr/bin/python
 
# Port serie utilisait par l'arduino
SERIALPORT = "/dev/ttyACM3"
 
# Vitesse du port serie
SERIALSPEED = 230400
#SERIALSPEED = 921600
 
# Temps entre deux frame (en secondes, nombre a virgule)
WAITTIME = 0
 
# Nombre de led sur le ruban
NBPIXELS = 60
 
# Image source
IMGSRC = "images/test.bmp"
 
# Temps avant lancement du streaming
STREAMDELAY = 2
 
# ---------------------------------------------------------------------
from PIL import Image
import   serial, time
 
def delay(t):
    for i in range(t, 0):
        print i,
        time.sleep(1)
 
print "~ Led Strip - Ligth Painting Assistant ~"
print "Created by SkyWodd"
print " "

print "Openning serial port ... ",
try:
    arduino = serial.Serial(SERIALPORT, SERIALSPEED, timeout=1)
except:
    print "FAILLED !"
    exit(-1)
print "OK !"
 
print "Booting up arduino ... ",
arduino.setDTR(True)
time.sleep(0.5)
arduino.setDTR(False)
ligne = arduino.readline()
while not "RDY" in ligne:
    ligne = arduino.readline()
print "Ok !"
delay(5)
 
print "Streaming system loaded !"
delay(STREAMDELAY)
print " "
 
nbFrames = 60
print "Number of frames to sent : %d" % nbFrames
tstart = time.time()
for k in range(0, 10): 
    for frame in range(0, nbFrames):
        tframestart = time.time()
        arduino.write("FRM")
        for i in range(0,NBPIXELS):
            if frame == i:
                arduino.write(chr(255))
                arduino.write(chr(255))
                arduino.write(chr(255))
            else:
                arduino.write(chr(0))
                arduino.write(chr(0))
                arduino.write(chr(0)) 
        print "# %d / %d time : %d" % (frame, nbFrames - 1, (time.time() - tframestart)*1000)
        time.sleep(WAITTIME)
print "Power off led ..."
arduino.write("FRM")

for i in range(0, NBPIXELS):
    arduino.write(chr(0))
    arduino.write(chr(0))
    arduino.write(chr(0))
 
print "End of stream !"
print "Streaming take %d seconds" % (time.time() - tstart)
 
print "Closing serial port ... ",
arduino.close()
print "OK !"
 
print "Bye bye !"
