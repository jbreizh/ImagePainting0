// include the library code:
#include <FastLED.h>

// constante
const int DATA_PIN = 11;
const int DATA_RATE = 2;
const int CLOCK_PIN = 10;

//How many pixels
const int NUMPIXELS = 60;

// variables
int pixel;
int nbpixel = NUMPIXELS;
byte bytes[3];

// initialize
CRGB pixels[NUMPIXELS];

// setup
void setup() {
  // hardware serial setup
  Serial.begin(115200);
  // LED setup
  //FastLED.addLeds<WS2812B, PIN, GRB>(pixels, NUMPIXELS);
  FastLED.addLeds<APA102, DATA_PIN, CLOCK_PIN, BGR, DATA_RATE_MHZ(DATA_RATE)>(pixels, NUMPIXELS);
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
  }
  if (bytes[0] == 'S' & bytes[1] == 'E' & bytes[2] == 'T') {    // Setting Frame
    Serial.readBytes(bytes, 3);
    FastLED.setBrightness(bytes[0]);
  }
}
