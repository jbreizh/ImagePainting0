 #!/usr/bin/python
 
from PIL import Image
import socket, time

UDP_IP_ADDRESS = "192.168.43.170"
#UDP_IP_ADDRESS = "127.0.0.1"
UDP_PORT_NO = 6789


# Temps entre deux frame (en millisecondes, nombre a virgule)
WAITTIME = 0.01

 
# Nombre de led sur le ruban
NBPIXELS = 60
 
# Image source
IMGSRC = "movies/bfuture2.bmp"
 
# ---------------------------------------------------------------------

 
def SendUDP(message):
    clientSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    clientSock.sendto(message, (UDP_IP_ADDRESS, UDP_PORT_NO))

 
 
print "Image painting simulator start"
print "Checking source image ... ",
try:
    size = NBPIXELS, 100000
    im2 = Image.open(IMGSRC)
    im2.thumbnail(size, Image.ANTIALIAS)
    
    
    data = list(im2.getdata())
    if len(data) < NBPIXELS or len(data[0]) != 3 :
        print "FAIL ! =("
        print "Source image must contain more than %d pixels in 24bits colors !" % NBPIXELS
        exit(-1)
except:
    print "FAILLED !"
    exit(-1)
print  "OK !"





nbFrames = len(data) / NBPIXELS
print "Number of frames to sent : %d" % nbFrames

tstart = time.time()


for frame in range(0, nbFrames):
    print "# %d / %d" % (frame, nbFrames - 1)
    frameContent= bytearray()
    frameContent.append("F")#70)
    frameContent.append("R")#82)
    frameContent.append("M")#77)
    offset = frame * NBPIXELS
    for i in range(offset, offset + NBPIXELS):
        frameContent.append(chr(data[i][0]))
        frameContent.append(chr(data[i][1]))
        frameContent.append(chr(data[i][2]))
    SendUDP(frameContent) 
    time.sleep(WAITTIME)




print "Streaming take %d seconds" % (time.time() - tstart)
print "Image painting simulator stop"




