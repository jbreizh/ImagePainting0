#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <FastLED.h>

//UDP --------------
WiFiUDP Udp;
unsigned int localPort = 5000; // local port to listen on
char packetBuffer[3000]; //buffer to hold incoming packet
//end UDP-----------

//WIFI --------------
WiFiServer server(80);

const char* ssid = "imagePainting";
String st;
String esid = "";
String epass = "";
//end WIFI-----------

// LED APA102 --------------
const int DATA_PIN = D1;
const int CLOCK_PIN = D2;
const int NUMPIXELS = 60; //How many pixels
CRGB pixels[NUMPIXELS];
//end APA102-----------

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

void setup()
{
  // LED setup
  FastLED.addLeds<APA102, DATA_PIN, CLOCK_PIN, BGR>(pixels, NUMPIXELS);
  //FastLED.setDither( 0 ); //desactivate dithering
  FastLED.setBrightness(40);
  for (int pixel = 0; pixel < NUMPIXELS ; pixel++)
  {
    pixels[pixel][0] = 0;  // red
    pixels[pixel][1] = 255; // green
    pixels[pixel][2] = 0; // blue
  }
  FastLED.show();
  delay(100);

  // Wifi setup
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);

  WiFi.softAP(ssid);
  Udp.begin(localPort);

  // Serial setup
  //Serial.begin(115200);
  //delay(100);
  //Serial.println("");
  //Serial.println("Stay in AP mode, UDP started");
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

void loop()
{
  // if there's data available, read a packet
  int packetSize = Udp.parsePacket();
  if (packetSize)
  {
    // read the packet into packetBufffer
    int len = Udp.read(packetBuffer, 800);
    //
    if (len > 0)
    {
      packetBuffer[len] = 0;
    }
    
    //Serial.println(packetSize);
    
    // image frame
    if (packetBuffer[0]=='F' & packetBuffer[1]=='R' & packetBuffer[2]=='M')
    {
      //Serial.print("frame receive : ");
      //for (int j = 0; j < sizeof(packetBuffer); j++)
      //{
      //  Serial.print(int(packetBuffer[j]));
      //  Serial.print("|");
      //}
      //Serial.println("");

      for (int pixel = 0; pixel < NUMPIXELS ; pixel++)
      {
        pixels[pixel][0] = packetBuffer[pixel * 3 + 3]; // red
        pixels[pixel][1] = packetBuffer[pixel * 3 + 4]; // green
        pixels[pixel][2] = packetBuffer[pixel * 3 + 5]; // blue
      }

      FastLED.show();

    }
    // settings frame
    else if (packetBuffer[0]=='S' & packetBuffer[1]=='E' & packetBuffer[2]=='T')
    {
      //Serial.print("Settings receive : brightness = ");
      //Serial.print(int(packetBuffer[3]));
      //Serial.print(" ; pixels = ");
      //Serial.print(int(packetBuffer[4]));
      //Serial.print(" ; delay = ");
      //Serial.print(int(packetBuffer[5]));
      //Serial.println("");

      FastLED.setBrightness(packetBuffer[3]);
    }
  }
}

