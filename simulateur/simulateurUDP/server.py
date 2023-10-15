 
import socket
# Here we define the UDP IP address as well as the port number that we have 
# already defined in the client python script.
#UDP_IP_ADDRESS = "127.0.0.1"
UDP_IP_ADDRESS = "192.168.43.170"
UDP_PORT_NO = 6789

# declare our serverSocket upon which
# we will be listening for UDP messages
serverSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# One difference is that we will have to bind our declared IP address
# and port number to our newly declared serverSock
serverSock.bind((UDP_IP_ADDRESS, UDP_PORT_NO))

while True:
    
    data, addr = serverSock.recvfrom(1024)
    #print addr, data
    # If is an image frame
    if (data[0]=="F" and data[1]=="R" and data[2]=="M"):
        message = "Image Frame : "
        for pixel in xrange(3,len(data),3):
            message += "\x1b[38;2;"+str(ord(data[pixel]))+";"+str(ord(data[pixel+1]))+";"+str(ord(data[pixel+2]))+"m@"
        message += "\x1b[0m"
        print message
    
    # if is a settings frame    
    elif (data[0]=="S" and data[1]=="E" and data[2]=="T"):
        message = "Settings frame : "
        message += "brightness = " + str(ord(data[3])) 
        message += " and ledSize = " + str(ord(data[4]))
        message += " and delay = " + str(ord(data[5]))
        print message
    
    # if is garbage
    else:
        message = "unknown frame: "
    
    
    
