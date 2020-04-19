# User Manual<br>COMP208 Group29

<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/app.png"></div>

## Overview
<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/main_menu.png"></div>


This network toolkit application includes 4 main tools, which respectively are
Ping tool **①**, Traceroute tool **②**, Geolocation tool **③** and LAN manager tool **④** .
These tools will support user to diagnose current network environment,
manage their family network devices and detect potential cyber threat.


## Target users
**Web developers, general public**

## Hardware requirements
* Mobile phone with Android OS version from 6.0 to 9.0
* CPU speed: Quad-core 1.2GHz
* RAM: 2GB
* Storage: at least 50M free storage

## Installation and run
Use official APK to install this application to your phone. (Click APK and Android
phone will automatically install)
<br><br>
All resource about this application is accessible on Github, user can download
the newest update from the link below.
* [Download here](https://github.com/COMP208/Visual_IP_apk/blob/master/Network_toolkit.apk)

## Quick start
The action bar (as shown in Ping tool) of this toolkit includes three parts, which
are back button **①** , the tool title and the tool bar **④** . In every diagnose tool
and map interface, users can return to the previous interface by clicking the
back button or just use their phones’ back key. As for tool bar, every tool shares
the same item which is “My IP”. Users can click it to get their phones’ IP address
and geolocation information.

## Ping Tool
<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/ping_tool.png"></div>

**①** Back button **②** IP/Domain name input box **③** Package number input box
**④** Tool bar    **⑤** Start Ping button **⑥** Output area

### For Ping tool

**1.** Enter IP/Domain name in input box **②** and packet number in input box **③**.

**2.** Click button **⑤** to start Ping and result will be displayed in output area **⑥**.

### Notice
If you input a wrong IP address/ domain name, an error message will be shown in the output area. Only numerical input is acceptable in package number input box. If you leave blank here, the application will set default package number to 4. Except the geolocation tool, other tools’ outputs are loaded dynamically. The outputs are added in a scroll view (chunks) for users to check.

## Traceroute tool
<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/traceroute_tool.png"></div>

**①** Back button **②** IP/Domain name input box **③** Enter button **④** Tool bar 
**⑤** Map view button  **⑥** Chunk contains Traceroute infomation **⑦** Clear button

### For Traceroute tool

**1.** Enter IP/Domain name in input box **②**.

**2.** Click enter button **③** to start traceroute and result will be output in chunks (scroll view).

**3.** Click map view button **⑤** to see traceroute path on Google map.

### Notice
Clear button **⑦** is one of the most common button in this application. When you are typing or there are contents in input box, the clear button will be visible for you to clear the input area. If users input an invalid IP address/domain name in traceroute or geolocation tool, a warning dialog will be shown.

## Geolocation tool
<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/geo.png"></div>

**①** Back button **②** IP/Domain name input box **③** Enter button
**④** Output area contains IP info **⑤** Map view button **⑥** Chunk contains IP info **⑦** Pin

### For geolocation tool

**1.** Enter IP/Domain name in input box **②** .

**2.** Click enter button **③** and the required geolocation information will be displayed in output area **④**.

**3.** Click map view button **⑤** and then the geolocation of the IP address will be displayed on Google map.

### Notice
You can move Map view with one finger and zoom Map view with two fingers. If you click the red pin, the detail information of this IP will be shown in a text frame, including IP address, city, country, continent, latitude, longitude.

## LAN Manager tool
<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/lan.png"></div>

**①** Back button **②** Refresh button **③** Topological graph button
**④** Registered devices button **⑤** Register switch button

### For LAN manager tool

**1.** When entering LAN manager page, all devices in the same local area network will be retrived and displayed in scroll view.

**2.** Click refresh button **②** ,the tool will scan and retrieve the devices in the same local area network again.

**3.** On each chunk that contains the information about the device there is a switch button named “Registered”, click this button and then corresponding device information will be stored. All registered devices can be seen in “Registered Devices” page. (“My Device” has been stored as shown above)

**4.** Click the topological graph button **③** and then a real-time topological graph will be generated and displayed in “Topology Graph” page. (As shown in figure below)

<br>

<div align=center><img src = "https://github.com/Weihao-Jin/readme_pictures/blob/master/topology_graph.png"></div>

### Notice
After you changed your registered devices, the output in “Registered Devices” page will be changed automatically at the same time. 

Note that the graph is generated dynamically. If you click item **③** during refreshing, you will get an uncompleted topological graph. To avoid this issue, it is designed to be shown only after finish refreshing.

The “Registered Devices” page will show all of the registered devices, even they’re not currently connected.

You can move topological graph with one finger and zoom topological graph with two fingers. If you want to reset the topology graph to initial location, you can click “Reset” key in the tool bar.

