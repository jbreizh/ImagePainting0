// include the library code:
#include <FastLED.h>

// constante
const int PIN = 12;                       //Pin used for neopixels
const int NUMPIXELS = 60;                  //How many pixels

// variables
int pixel;
int nbpixel = NUMPIXELS;
int r;  //Red pixel value
int g;  //Green pixel value
int b;  //Blue pixel value

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

  while (Serial.available() < 3);        // Waiting 3 bytes to come

  if (Serial.read() == 'F') {            // This is an image Frame
    if (Serial.read() == 'R') {
      if (Serial.read() == 'M') {
        Serial.readBytes( (char*)pixels, NUMPIXELS * 3);
        FastLED.show();
        }
      }
    }

    else {
      if (Serial.read() == 'E') {          // This is a Setting Frame
        if (Serial.read() == 'T') {
          while (Serial.available() < 3);
          r = Serial.read();
          g = Serial.read();
          b = Serial.read();
          FastLED.setBrightness(r);
          nbpixel = g;
        }
      }
    }

  }
