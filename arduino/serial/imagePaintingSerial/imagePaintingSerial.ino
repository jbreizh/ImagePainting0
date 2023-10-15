// include the library code:
#include <Adafruit_NeoPixel.h>

// constante
const int PIN = 12;                       //Pin used for neopixels
const int NUMPIXELS = 60;                  //How many pixels

// variables
int pixel;
int r;  //Red pixel value
int g;  //Green pixel value
int b;  //Blue pixel value

// initialize
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

// setup
void setup() {

  // hardware serial setup
  Serial.begin(115200);

  // neopixel setup
  pixels.begin();
  pixels.setBrightness(40);
  for (pixel = 0; pixel < NUMPIXELS ; pixel = pixel + 1) {
    pixels.setPixelColor(pixel , 0, 0, 0);
  }
  pixels.show();

  //Boot Message
  Serial.println("RDY");
}

// loop
void loop() {

  while (Serial.available() < 3);  // Waiting 3 bytes to come

  if (Serial.read() == 'F') {  //
    if (Serial.read() == 'R') {
      if (Serial.read() == 'M') {
        for (pixel = 0; pixel < (NUMPIXELS) ; pixel = pixel + 1) {        //
          while (Serial.available() < 3);
          r = Serial.read();
          g = Serial.read();
          b = Serial.read();
          pixels.setPixelColor(pixel , r, g, b);
        }
        pixels.show();
        Serial.println("ACK");
      }
    }
  }
}
