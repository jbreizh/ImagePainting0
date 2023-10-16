import math
from PIL import Image

#Don't forget the max number of items in each array is 32767 items (max signed 16 bit int size).
#This means the max value for length below is 10922!
#Length is the number is pixels stored in each array, so should be the largest number divisible by pixels


pixels = 8
length = pixels*pixels
delay = 100

alphabet = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","Space"]

file="fonts/led.bmp"
#file="fonts/font16x16.bmp"

if length % pixels != 0:
    raise NameError("Length constant must be divisible by pixel number! Recommended length = " + str((length / pixels) * pixels))
if length > 10922:
    raise NameError('Length is too large! It only supports up to 10922 (due to 16 bit signed ints)')


def splitSmaller(longList, newList):
    numNeeded = len(longList)
    numNeeded = numNeeded /float(length)
    numNeeded = int(math.ceil(numNeeded))
    for i in range(0, numNeeded):
        if i == numNeeded-1:
            start = i*length
            end = len(longList)
        else:
            start = i*length
            end = ((i+1) *length)
        newList.append(longList[start:end])
    return newList

def printAll(mainList):
    i = 0
    lengths = []
    
    for i in range(0, len(mainList)):
        bob = ""
        lengths.append(len(mainList[i]))
        for x in range(0, len(mainList[i])):
            for y in range(0,len(mainList[i][x])):
                bob = bob+ ", " + str(mainList[i][x][y])
        bob = bob[2:len(bob)]
        print("const byte font"+ str(alphabet[i]) + " [" + str(len(mainList[i])*3) +"] PROGMEM = { " + bob + "};")
    
#    bob = ""
#    for i in range(0, len(mainList)):
#        bob = bob+ ", font" + str(alphabet[i])       
#    bob = bob[2:len(bob)]     
#    print("const byte fonts [" + str(len(mainList)) +"] PROGMEM = { " + bob + "};")      


#-------------------------Main program--------------------------


im = Image.open(file, "r")
pix_val = list(im.getdata())
pix_val2 = splitSmaller(pix_val, [])
#print pix_val
#print pix_val2

printAll(pix_val2)


