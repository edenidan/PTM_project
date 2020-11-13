# PTM_project
Includes two parts:
- The **[FlightGear](https://www.flightgear.org/) controller** (which runs on the client)
- The **path calculation server**

## The FlightGear Controller
A controller for the airplane that's in the FlightGear simulation, written in Java using JavaFX and the MVVM architecture.  
The controller GUI has:
- Controls for the direction, throttle, and rudder of the plane (manual mode).
- A textbox for a script written in our custom script language (autopilot mode).

The script is interpreted by the interpreter we wrote specifically for our script language.

## The Path Calculation Server
A server which calculates the optimal path for the plane from one point to another, using the A* algorithm.

## Creators
- [Erez Ben Moshe](https://github.com/erezbm)
- [Idan Eden](https://github.com/edenidan)
