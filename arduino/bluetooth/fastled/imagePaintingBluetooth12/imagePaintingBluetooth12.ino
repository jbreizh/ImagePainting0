// include the library code:
#include <FastLED.h>

// constante
const int PIN = 12;                       //Pin used for neopixels
const int NUMPIXELS = 60;                  //How many pixels

// variables
int pixel;
byte bytes[3];

// initialize
CRGB pixels[NUMPIXELS];

// setup
void setup() {
  // hardware serial setup
  Serial.begin(115200);
  // LED setup
  FastLED.addLeds<WS2812B, PIN, GRB>(pixels, NUMPIXELS);
  FastLED.setBrightness(46);
  for (pixel = 0; pixel < NUMPIXELS ; pixel++) {
    pixels[pixel][0] = 0;  // red
    pixels[pixel][1] = 0; // green
    pixels[pixel][2] = 0; // blue
  }
  FastLED.show();
}

// loop
void loop() {
  //
  Serial.readBytes(bytes, 3);
  //
  if (bytes[0] == 'F' & bytes[1] == 'R' & bytes[2] == 'M') {    // Image Frame
    Serial.readBytes( (char*)pixels, NUMPIXELS * 3);
    FastLED.show();
    while (Serial.available() > 0) {
      Serial.read();
    }
  }
  if (bytes[0] == 'S' & bytes[1] == 'E' & bytes[2] == 'T') {    // Setting Frame
    Serial.readBytes(bytes, 3);
    FastLED.setBrightness(bytes[0]);
  }
}



