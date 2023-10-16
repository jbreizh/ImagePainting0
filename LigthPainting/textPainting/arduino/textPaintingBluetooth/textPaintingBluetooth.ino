// include the library code:
#include <Adafruit_NeoPixel.h>
#include <SoftwareSerial.h>
#include <avr/pgmspace.h>

// constante
const int TXPIN = 10;                     //Pin TX used for softwareSerial
const int RXPIN = 11;                     //Pin RX used for softwareSerial
const int PIN = 12;                       //Pin used for neopixels
const int BLOCS = 7;                      //How many pixels square per metapixel?
const int NUMPIXELS = 8;                  //How many metapixels your font is?

// constante for standard alphabet
const byte fontA [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontB [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontC [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontD [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontE [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontF [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontG [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontH [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontI [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontJ [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontK [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontL [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontM [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1};
const byte fontN [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontO [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontP [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontQ [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontR [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontS [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontT [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontU [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontV [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontW [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1};
const byte fontX [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontY [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontZ [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const byte fontSpace [64] PROGMEM = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

// variables
String order;// string to store incoming message from bluetooth
String message = "HELLO WORD";
int red = 255;
int green = 255;
int blue = 255;
int delayTime = 80;
int brightness = 30;

char character;  //character value
int characterl;  //character location (in message)

int layer; //layer location
int blocH; //pixel location
int pixel; //pixel location
int blocV; //pixel location

int p;  //pixel value
int pl; //pixel location (in array)
// initialize
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS*BLOCS, PIN, NEO_GRB + NEO_KHZ800);
SoftwareSerial BT(TXPIN, RXPIN); //TX, RX respetively

// setup
void setup() {
  // software bluetooth setup
  BT.begin(38400);
  // hardware serial (debug) setup
  Serial.begin(9600);
  // neopixel setup
  pixels.begin();
  pixels.setBrightness(brightness); // set the led brightness
  for (pixel = 0; pixel < (NUMPIXELS * BLOCS + 1) ; pixel = pixel + 1) {   //Turns off all pixels at the start
    pixels.setPixelColor(pixel , 0, 0, 0);
  }
  pixels.show();
}

//choose the right character
void chooseCharacter() {
  switch (character) {
    // alphabet
    case 'A':
      p = pgm_read_byte (fontA + pl);     //Get current pixel colour value
      break;
    case 'B':
      p = pgm_read_byte (fontB + pl);
      break;
    case 'C':
      p = pgm_read_byte (fontC + pl);
      break;
    case 'D':
      p = pgm_read_byte (fontD + pl);
      break;
    case 'E':
      p = pgm_read_byte (fontE + pl);
      break;
    case 'F':
      p = pgm_read_byte (fontF + pl);
      break;
    case 'G':
      p = pgm_read_byte (fontG + pl);
      break;
    case 'H':
      p = pgm_read_byte (fontH + pl);
      break;
    case 'I':
      p = pgm_read_byte (fontI + pl);
      break;
    case 'J':
      p = pgm_read_byte (fontJ + pl);
      break;
    case 'K':
      p = pgm_read_byte (fontK + pl);
      break;
    case 'L':
      p = pgm_read_byte (fontL + pl);
      break;
    case 'M':
      p = pgm_read_byte (fontM + pl);
      break;
    case 'N':
      p = pgm_read_byte (fontN + pl);
      break;
    case 'O':
      p = pgm_read_byte (fontO + pl);
      break;
    case 'P':
      p = pgm_read_byte (fontP + pl);
      break;
    case 'Q':
      p = pgm_read_byte (fontQ + pl);
      break;
    case 'R':
      p = pgm_read_byte (fontR + pl);
      break;
    case 'S':
      p = pgm_read_byte (fontS + pl);
      break;
    case 'T':
      p = pgm_read_byte (fontT + pl);
      break;
    case 'U':
      p = pgm_read_byte (fontU + pl);
      break;
    case 'V':
      p = pgm_read_byte (fontV + pl);
      break;
    case 'W':
      p = pgm_read_byte (fontW + pl);
      break;
    case 'X':
      p = pgm_read_byte (fontX + pl);
      break;
    case 'Y':
      p = pgm_read_byte (fontY + pl);
      break;
    case 'Z':
      p = pgm_read_byte (fontZ + pl);
      break;
    default:
      p = pgm_read_byte (fontSpace + pl);
      break;
  }
}

// print the message
void printMessage() {
  BT.print("message : ");
  BT.println(message);
  BT.print("size : ");
  BT.println(message.length());
  for (characterl = 0; characterl < message.length() ; characterl = characterl + 1) {
    character = message[characterl];
    BT.print("character : ");
    BT.println(character);
    for (layer = 0; layer < NUMPIXELS; layer = layer + 1) {                      //For each layer in the image
      for (blocH = 0; blocH < BLOCS ; blocH = blocH + 1) {                       //For each bloc Horizontal
        for (pixel = 0; pixel < NUMPIXELS ; pixel = pixel + 1) {                 //For each pixel
          pl = layer * NUMPIXELS + pixel;                                        //Get location of the current pixel
          chooseCharacter();
          for (blocV = 0; blocV < BLOCS ; blocV = blocV + 1) {                   //For each bloc Vertical
            if (p == 1) {
              pixels.setPixelColor(pixel * BLOCS + blocV , red, green, blue);    //Set the pixel data
            }
            else {
              pixels.setPixelColor(pixel * BLOCS + blocV , 0, 0, 0);             //Set the pixel data
            }
          }
        }
        pixels.show();
        delay(delayTime);
      }
    }
  }
  BT.println("turning off");
  for (pixel = 0; pixel < (NUMPIXELS * BLOCS + 1) ; pixel = pixel + 1) {   //Turns off all pixels at the end
    pixels.setPixelColor(pixel , 0, 0, 0);
  }
  pixels.show();
}

// loop
void loop() {

  while (BT.available()) {    //Check if there is an available byte to read
    delay(10);                //Delay added to make thing stable
    char c = BT.read();       //Conduct a serial read
    order += c;             //build the string
  }

  if (order.length() > 0) {

    if (order[0] == 'F') {
      BT.println("arduino printing");
      printMessage();
      BT.println("arduino end printing");
    }
    if (order[0] == 'M') {
      message = order.substring(1);
      BT.print("arduino message : ");
      BT.println(message);
    }
    if (order[0] == 'R') {
      red = order.substring(1).toInt();
      BT.print("arduino red : ");
      BT.println(red);
    }
    if (order[0] == 'G') {
      green = order.substring(1).toInt();
      BT.print("arduino green : ");
      BT.println(green);
    }
    if (order[0] == 'B') {
      blue = order.substring(1).toInt();
      BT.print("arduino blue : ");
      BT.println(blue);
    }
    if (order[0] == 'D') {
      delayTime = order.substring(1).toInt();
      BT.print("arduino delay : ");
      BT.println(delayTime);
    }
    if (order[0] == 'L') {
      brightness = order.substring(1).toInt();
      pixels.setBrightness(brightness); // set the led brightness
      BT.print("arduino brightness : ");
      BT.println(brightness);
    }

    order = "";             //Reset the message
  }
}
