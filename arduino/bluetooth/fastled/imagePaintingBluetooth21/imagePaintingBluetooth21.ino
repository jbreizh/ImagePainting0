// include the library code:
#include <FastLED.h>

// constante
const int PIN = 12;        //Pin used for neopixels
const int NUMPIXELS = 60;  //How many pixels

// variables
boolean pixelRead;
int pixelIndex;  //pixel index
int byte1;       //first byte value
int byte2;       //second byte  value
int byte3;       //third byte value

// initialize
CRGB pixels[NUMPIXELS];

// setup
void setup() {

  // hardware serial setup
  Serial.begin(115200);

  // LED setup
  FastLED.addLeds<WS2812B, PIN, RGB>(pixels, NUMPIXELS);
  FastLED.setBrightness(46);
  for (pixelIndex = 0; pixelIndex < NUMPIXELS ; pixelIndex++) {
    pixels[pixelIndex][0] = 0;  // red
    pixels[pixelIndex][1] = 0; // green
    pixels[pixelIndex][2] = 0; // blue
  }
  FastLED.show();
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
  if (byte1 == 'F' & byte2 == 'R' & byte3 == 'M') {    // Image Frame
    pixelRead = true;
    pixelIndex = 0;
    while (pixelRead) {
      readBluetooth();
      if (byte1 == 'E' & byte2 == 'N' & byte3 == 'D') {
        pixelRead = false;
      }
      else {
        pixels[pixelIndex][0] = byte1;  // red
        pixels[pixelIndex][1] = byte2; // green
        pixels[pixelIndex][2] = byte3; // blue
        pixelIndex = pixelIndex + 1;
      }
    }
    FastLED.show();
    while (Serial.available() > 0) {
      Serial.read();
    }
  }
  if (byte1 == 'S' & byte2 == 'E' & byte3 == 'T') {    // Setting Frame
    readBluetooth();
    FastLED.setBrightness(byte1);
  }

  if (byte1 == 'G' or byte2 == 'G' or byte3 == 'G') {    // Debug Frame
    for (pixelIndex = 0; pixelIndex < NUMPIXELS ; pixelIndex++) {
      pixels[pixelIndex][0] = 0;  // red
      pixels[pixelIndex][1] = 0; // green
      pixels[pixelIndex][2] = 0; // blue
    }
    FastLED.show();
    while (Serial.available() > 0) {
      Serial.read();
    }
  }
}
