Eye Tracking Simulation Project

**Team Members**

Ashton Alonge <br>
Anthony Colin Herrera<br>
David Hernandez

**Overview** <br>
This project is a simulation that visualizes real-time brain wave data using an Emotiv brain wave device. The application receives data from its source, processes it, and updates a graphical interface to reflect the brain wave data dynamically.

**How It Works**

**Real-Time Data Visualization:** <br>
Brain wave data is visualized on a graphical interface as data points are received.

**Data Sources:** <br>
The system supports receiving data via:

TCP connections<br>
MQTT subscriptions<br>

**Centralized Data Management:**<br>
A Blackboard class acts as the central hub for storing and distributing the data to listeners.

**Interactive UI:**<br>
The interface allows users to start and stop data reception and displays real-time brain wave data.

***Project Structure***

**Data Management:**<br>

Classes that handle incoming data:<br>
Blackboard
Publisher
TheSubscriber
TheSubscriberMQTT
Visualization:
Classes for rendering brain wave data:

CanvasController
TrackArea

**Diagrams**<br>
[UML](https://lucid.app/lucidchart/69d0c9be-11c2-4c1d-98e8-85a34af699c4/edit?invitationId=inv_2f47569e-b400-44d5-9cc8-9e2b107dcc53&page=HWEp-vi-RSFO#) <br>
[Architecture](https://lucid.app/lucidchart/3879a143-ac14-4573-a6d0-43adef0e9144/edit?beaconFlowId=C2C31C0113EA8FC8&invitationId=inv_0c78ed9f-2a6c-4d9b-8537-34477a4595f6&page=0_0#)
