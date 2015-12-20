# Schau genau!
Eyetracking game developed by Raphael Menges and Kevin Schmidt as part of their employement for the  [Institute for Web Science and Technologies](http://west.uni-koblenz.de/) at University Koblenz-Landau. It was developed for the [State Horticultural Show Landau 2015](http://lgs-landau.de/) as arcarde box game, using only gaze and one buzzer as input. Nearly 3000 sessions were played during summer without any known issues.

## History
This project was started as part of their studies in "Projektpraktikum" during summer semester 2013 supervised by Dr. Tina Walber. Other students involved were Heiko Bengel, Matthias Kuich and Marc Wilhelmy, but none of their source code is used in current version.

## Screenshots
![Screenshot-A](/media/Screenshot-A.png "Idle screen")
![Screenshot-B](/media/Screenshot-B.png "Tutorial")
![Screenshot-C](/media/Screenshot-C.png "Beginning of game")
![Screenshot-D](/media/Screenshot-D.png "Game")
![Screenshot-E](/media/Screenshot-E.png "Name input")

## Launch
Run main function in "schaugenau.app". Due too license issues, neither the connectin to our image database nor the connection to an eyetracker is integrated. All important code lines are marked with a "TODO" comment. The game itself should work out of the box with mouse support, which emulates the gaze. During gameplay, errors about no connection to an image database are thrown and placeholders are used instead.

## Dependencies
* jMonkey: http://jmonkeyengine.org/
* log4j: http://logging.apache.org/log4j/2.x/
* LWJGL: https://www.lwjgl.org/
* OpenAL: https://www.openal.org/
* opencsv: http://opencsv.sourceforge.net/

All necessary dependencies are included in this repository.

## License
MIT License

## Acknowledgments
* Supported by [EYEVIDO](http://eyevido.de/)
* Sound and music by Sebastian Prusak
