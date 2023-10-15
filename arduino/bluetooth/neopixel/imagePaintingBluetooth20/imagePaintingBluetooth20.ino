// include the library code:
#include <Adafruit_NeoPixel.h>

// constante
const int PIN = 12;                       //Pin used for neopixels
const int NUMPIXELS = 60;                  //How many pixels

// variables
boolean pixelRead;
int pixelIndex;  //pixel index
int byte1;  //first byte value
int byte2;  //second byte  value
int byte3;  //third byte value

// initialize
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

// setup
void setup() {

  // hardware serial setup
  Serial.begin(115200);

  // neopixel setup
  pixels.begin();
  pixels.setBrightness(40);
  for (pixelIndex = 0; pixelIndex < NUMPIXELS ; pixelIndex = pixelIndex + 1) {
    pixels.setPixelColor(pixelIndex , 0, 0, 0);
  }
  pixels.show();
}

// read bluetooth incomming
void readBluetooth() {
  while (Serial.available() < 3);  // Waiting 3 bytes to come
  byte1 = Serial.read();
  byte2 = Serial.read();
  byte3 = Serial.read();
}

// loop
void loop() {
  //
  readBluetooth();
  //
  if (byte1 == 'F' & byte2 == 'R' & byte3 == 'M') {
    pixelRead = true;
    pixelIndex = 0;
    while (pixelRead) {
      readBluetooth();
      if (byte1 == 'E' & byte2 == 'N' & byte3 == 'D') {
        pixelRead = false;
      }
      else {
        pixels.setPixelColor(pixelIndex , byte1, byte2, byte3);
        pixelIndex = pixelIndex + 1;
      }
    }
    pixels.show();
  }
  if (byte1 == 'S' & byte2 == 'E' & byte3 == 'T') {
    readBluetooth();
    pixels.setBrightness(byte1);
  }

  if (byte1 == 'G' or byte2 == 'G' or byte3 == 'G') {
    for (pixelIndex = 0; pixelIndex < NUMPIXELS ; pixelIndex = pixelIndex + 1) {
      pixels.setPixelColor(pixelIndex , 0, 0, 0);
    }
    pixels.show();
      while (Serial.available())
    Serial.read();
  }
}
