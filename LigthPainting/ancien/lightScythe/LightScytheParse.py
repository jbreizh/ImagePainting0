__author__ = 'gav'

import PIL

import Image
import serial
import sys


infilenamebase = "output"
if (len(sys.argv)>1):
    infilenamebase = sys.argv[1]
#infilename = "input_photo.jpg"
outfilename = "images/" + infilenamebase + "_mod.gif"

im = Image.open("images/"+ infilenamebase + ".gif")
im = im.convert("RGB")
im_modified = im

im_text = open("images/" + infilenamebase + ".scythe", 'w')

print "Image dimension is: ", im.size, " format: ", im.format, " mode: ", im.mode
imwidth, imheight = im.size
m = im.getpixel((1,1))
print m

import serial
#ser = serial.Serial(0)  # open first serial port
#print ser.portstr       # check which port was really used
#ser.write("hello")      # write a string
#ser.close()             # close port

def write_column(n):
    print "\tPrinting col: ",n
    im_text.write("M")
    for y in range(0,imheight):
        scythe_col, rgb_col = colour_match(im.getpixel((n,y)))
        im_modified.putpixel((n,y),rgb_col)
        im_text.write(str(scythe_col))
        #print im.getpixel((n,y))
    im_text.write("\n")
        

def output_image():
    print "Outputting image: "
    for x in range(0,imwidth):
        write_column(x)

#// colors, each bit one LED
#define BLACK 0b000 0
#define WHITE 0b111 7
#define RED 0b100 4
#define YELLOW 0b110 6
#define GREEN 0b010 2
#define TEAL 0b011 3
#define BLUE 0b001 1
#define VIOLET 0b101 5
def colour_match(rgbin):
    col_dict = {0:(0,0,0), 1:(0,0,255), 2:(0,255,0), 3:(0,255,255), 4:(255,0,0), 5:(255,0,255),
                6:(255,255,0), 7:(255,255,255)}
    best_col = (0,1e6)
    for colnum, colval in col_dict.items():
        col_dist = ((rgbin[0] - colval[0])**2 + (rgbin[1] - colval[1])**2 + (rgbin[2] - colval[2])**2) ** 0.5
        if col_dist < best_col[1]:
            best_col = (colnum,col_dist)
        #print "Colour num is:" , colnum
        #print "Colour val is:" , colval

    return best_col[0],col_dict[best_col[0]]


def dither_image(imin):
    return imin





output_image()
im_modified = dither_image(im_modified)
im_modified.save(outfilename)
im_text.close()




