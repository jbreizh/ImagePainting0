#include <Adafruit_NeoPixel.h>
#include <avr/pgmspace.h>
#define PIN            11  //Pin used for neopixels
#define NUMPIXELS      60  //How many neopixels are you using?


Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);


int pixel;


void setup() {
  pixels.begin();
  pinMode(2, INPUT);
  Serial.begin(9600);


}

void loop() {

  for (pixel = 0; pixel < (NUMPIXELS + 1) ; pixel = pixel + 1) {    //Turns off all pixels at the end
    pixels.setPixelColor(pixel , random(0, 255), random(0, 255), random(0, 255));
    pixels.show();
    delay(20);     //Waits 5 seconds before going back to the start of the program
    pixels.setPixelColor(pixel , 0, 0, 0);
    pixels.show();
  }
  pixels.setPixelColor(pixel , 0, 0, 0);
  pixels.show();
}
