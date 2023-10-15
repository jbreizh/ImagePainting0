#!/usr/bin/python
 
# Port serie utilisait par l'arduino
SERIALPORT = "/dev/ttyACM0"
 
# Vitesse du port serie
SERIALSPEED = 115200
 
# Temps entre deux frame (en secondes, nombre a virgule)
WAITTIME = 0
 
# Nombre de led sur le ruban
NBPIXELS = 60
 
# Image source
IMGSRC = "images/shambhala.bmp"
 
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
 
print "Checking source image ... ",
try:
    im2 = Image.open(IMGSRC)
    data = list(im2.getdata())
    if len(data) < NBPIXELS or len(data[0]) != 3 :
        print "FAIL ! =("
        print "Source image must contain more than %d pixels in 24bits colors !" % NBPIXELS
        exit(-1)
except:
    print "FAILLED !"
    exit(-1)
print  "OK !"

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
 
nbFrames = len(data) / NBPIXELS
print "Number of frames to sent : %d" % nbFrames
tstart = time.time()
 
for frame in range(0, nbFrames):
    print "# %d / %d" % (frame, nbFrames - 1)
    arduino.write("FRM")
    offset = frame * NBPIXELS
    for i in range(offset, offset + NBPIXELS):
        arduino.write(chr(data[i][0]))
        arduino.write(chr(data[i][1]))
        arduino.write(chr(data[i][2]))
    ack = arduino.readline()
    if not "ACK" in ack:
        print "- COM ERROR -"
    time.sleep(WAITTIME)
     
print "Power off led ..."
arduino.write("FRM")
for i in range(0, NBPIXELS):
    arduino.write(chr(0))
    arduino.write(chr(0))
    arduino.write(chr(0))
ack = arduino.readline()
if not "ACK" in ack:
    print "- COM ERROR -"
 
print "End of stream !"
print "Streaming take %d seconds" % (time.time() - tstart)
 
print "Closing serial port ... ",
arduino.close()
print "OK !"
 
print "Bye bye !"
