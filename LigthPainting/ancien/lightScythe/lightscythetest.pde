
/*
This is the most basic demonstration code of using HL1606-based digital LED strips. 
The HL1606 chips are not very 'smart' and do not have built in PWM control. (Although
there is a fading ability, its not that useful)
 
We have a few examples of using the setLEDcolor() and writeStrip() command that will 
allow changing the strip around
 
public domain
*/
 
 
// HL1606strip is an adaptation of LEDstrip from  http://code.google.com/p/ledstrip/
#include "HL1606strip.h"
 
// for the LED belt, these are the pins used
#define STRIP_D 5
#define STRIP_C 4
#define STRIP_L 3

#define PB_PIN 15
#define DIAL_PIN 2

#define NUM_LEDS 65 

int scythe_data[NUM_LEDS];
// Pin S is not really used in this demo since it doesnt use the built in PWM fade
// The last argument is the number of LEDs in the strip. Each chip has 2 LEDs, and the number
// of chips/LEDs per meter varies so make sure to count them! if you have the wrong number
// the strip will act a little strangely, with the end pixels not showing up the way you like
HL1606strip strip = HL1606strip(STRIP_D, STRIP_L, STRIP_C, NUM_LEDS);
 
#define LEDPIN 13
#define SCYTHEPIN 14  //jumpered to GND on Analog 0, scythe 2.
#define BATTPIN 5 // Battery via middle of two 4kohm resistors volt div.

int scythe_num = 0;
float batt_volt = 0;

void setup(void) {
  Serial.begin(57600);

  pinMode(PB_PIN,INPUT); digitalWrite(PB_PIN,HIGH);  
  
  check_scythe_num();
  read_battery();
  
  for (int i = 0; i<5; i++){
    strip.setLEDcolor(1, BLUE);
    strip.writeStrip();
    delay(100);
    stripOff();
  }
  
  Serial.print("\nLightScythe v.01");
  Serial.print("\t Scythe: "); Serial.println(scythe_num);  
  Serial.print("\t Batt volt: "); Serial.println(batt_volt);
  Serial.print("\t Batt raw volt: "); Serial.println(analogRead(BATTPIN));
  
  //Quick demo on startup:
  //swish(0,RED, TEAL);
}

int dial_pos = 0;
void loop(void) { 
  if (digitalRead(PB_PIN)==0){
    Serial.println(scythe_num);
  }
  dial_pos = map(analogRead(DIAL_PIN),0,1024,0,10);
  if (dial_pos == 0){
    parse_serial();
  }
  check_battery_low();
}

int serial_pos = 0;
boolean serial_valid = false;
/*
// colors, each bit one LED
#define BLACK 0b000 0 
#define WHITE 0b111 7
#define RED 0b100 4
#define YELLOW 0b110 6
#define GREEN 0b010 2
#define TEAL 0b011 3
#define BLUE 0b001 1
#define VIOLET 0b101 5
*/
//**********************************************************************
// Parse the serial input. 
void parse_serial(){
  while ( Serial.available()) {
    byte data = Serial.read();
    if (data == 'M'){
      serial_valid = true;
      serial_pos = 0;
      //stripOff();
    } else if ( (data >= '0' ) && (data <= '9') ){      
      scythe_data[serial_pos]=data-'0';
       strip.setLEDcolor(serial_pos, data-'0');
      serial_pos++;
    } else {
      //Not valid data
      serial_valid = false;
      serial_pos = 0;
      
    }
  }
  if (serial_pos >= NUM_LEDS) {
    serial_pos = 0;
    serial_valid = false;  
    stripOff();
  }
  strip.writeStrip();    
}

//**********************************************************************

boolean check_scythe_num(){
    pinMode(SCYTHEPIN,INPUT); digitalWrite(SCYTHEPIN,HIGH); //Pullups
    if (digitalRead(SCYTHEPIN)){
      scythe_num = 1;
    } else {
      scythe_num = 2;
    }
}

void read_battery(){
  batt_volt = float( analogRead(BATTPIN)) / 1024 * 3.3 * 2;  
}
void check_battery_low(){
  read_battery();
  
  //Serial.print("\t Batt volt: "); Serial.print(batt_volt);
  //Serial.print("\t Batt raw volt: "); Serial.println(analogRead(BATTPIN));
  
  if (batt_volt < 5) {
    while(true){
      read_battery();
      Serial.print("Batt voltage too low! Scythe ");
      Serial.print(scythe_num); Serial.println(" offline");
      Serial.print("\t Batt volt: "); Serial.print(batt_volt);
      Serial.print("\t Batt raw volt: "); Serial.println(analogRead(BATTPIN));
      
      stripOff();       stripOff();       stripOff();
      delay(500);
      strip.setLEDcolor(1, RED);
      strip.setLEDcolor(2, RED);
      strip.setLEDcolor(3, RED);
      strip.writeStrip();
      delay(500);
    }    
  }  
}
 
/**********************************************/
/***             LED Control                ***/
/**********************************************/
void swish(int time, int top_colour){
  swish( time, top_colour, BLACK);
}
void swish(int time, int top_colour, int bot_colour){
  for( int i=0; i< strip.numLEDs(); i+=1) {
    bar_graph(i,top_colour,bot_colour);
    delay(time);  
  }
  for( int i=strip.numLEDs(); i>=0 ; i--) {
    bar_graph(i,top_colour,bot_colour);
    delay(time);  
  }
}

void bar_graph(int val, int top_colour, int bot_colour){
  //allowable colours, WHITE, BLACK, TEAL, RED, YELLOW, GREEN, BLUE, VOILET
  for( int i=0; i< strip.numLEDs(); i+=1) {
    // initialize strip with 'rainbow' of colors
    if (i<=val) {
      strip.setLEDcolor(i, top_colour);   
    } else {
      strip.setLEDcolor(i, bot_colour);   
    }    
  }
  strip.writeStrip();     
}

void bar_graph(int val, int colour){
  bar_graph(val, colour, BLACK);
}

void slow_test(){
  // first argument is the color, second is the delay in milliseconds between commands
   colorFill(RED);
   delay(1000);
   stripOff();
   colorFill(BLUE);
   delay(1000);
   stripOff();
   colorFill(GREEN);
   delay(1000);
   stripOff();
   // test all the LED colors with a wipe
}
void dontrun(){
   colorWipe(RED, 40);
   colorWipe(YELLOW, 40);
   colorWipe(GREEN, 40);
   colorWipe(TEAL, 40);
   colorWipe(BLUE, 40);
   colorWipe(VIOLET, 40);
   colorWipe(WHITE, 40);
   colorWipe(BLACK, 40);
 
   // then a chase
   chaseSingle(RED, 40);
   chaseSingle(YELLOW, 40);
   chaseSingle(GREEN, 40);
   chaseSingle(TEAL, 40);
   chaseSingle(VIOLET, 40);
   chaseSingle(WHITE, 40);
 
   // a colorcycle party!
   rainbowParty(60);
}
 
  

 
// scroll a rainbow!
void rainbowParty(uint8_t wait) {
  uint8_t i, j;
 
  for (i=0; i< strip.numLEDs(); i+=6) {
    // initialize strip with 'rainbow' of colors
    strip.setLEDcolor(i, RED);
    strip.setLEDcolor(i+1, YELLOW);
    strip.setLEDcolor(i+2, GREEN);
    strip.setLEDcolor(i+3, TEAL);
    strip.setLEDcolor(i+4, BLUE);
    strip.setLEDcolor(i+5, VIOLET);
 
  }
  strip.writeStrip();   
 
  for (j=0; j < strip.numLEDs(); j++) {
 
    // now set every LED to the *next* LED color (cycling)
    uint8_t savedcolor = strip.getLEDcolor(0);
    for (i=1; i < strip.numLEDs(); i++) {
      strip.setLEDcolor(i-1, strip.getLEDcolor(i));  // move the color back one.
    }
    // cycle the first LED back to the last one
    strip.setLEDcolor(strip.numLEDs()-1, savedcolor);
    strip.writeStrip();
    delay(wait);
  }
}
 
 
// turn everything off (fill with BLACK)
void stripOff(void) {
  // turn all LEDs off!
  for (uint8_t i=0; i < strip.numLEDs(); i++) {
      strip.setLEDcolor(i, BLACK);
  }
  strip.writeStrip();   
}
 
// have one LED 'chase' around the strip
void chaseSingle(uint8_t color, uint8_t wait) {
  uint8_t i;
 
  // turn everything off
  for (i=0; i< strip.numLEDs(); i++) {
    strip.setLEDcolor(i, BLACK);
  }
 
  for (i=0; i < strip.numLEDs(); i++) {
    strip.setLEDcolor(i, color);
    if (i != 0) {
      // make the LED right before this one OFF
      strip.setLEDcolor(i-1, BLACK);
    }
    strip.writeStrip();
    delay(wait);  
  }
  // turn off the last LED before leaving
  strip.setLEDcolor(strip.numLEDs() - 1, BLACK);
}
 
// fill the entire strip, with a delay between each pixel for a 'wipe' effect
void colorWipe(uint8_t color, uint8_t wait) {
  uint8_t i;
 
  for (i=0; i < strip.numLEDs(); i++) {
      strip.setLEDcolor(i, color);
      strip.writeStrip();   
      delay(wait);
  }
}

// fill the entire strip, with a delay between each pixel for a 'wipe' effect
void colorFill(uint8_t color) {
  uint8_t i;
 
  for (i=0; i < strip.numLEDs(); i++) {
      strip.setLEDcolor(i, color);      
  }
  strip.writeStrip();   
}
