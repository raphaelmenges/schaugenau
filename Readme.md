# Schau genau!
Eye tracking game developed by Raphael Menges and Kevin Schmidt as part of their employement for the  [Institute for Web Science and Technologies](http://west.uni-koblenz.de/) at University Koblenz-Landau. It was designed for the [State Horticultural Show Landau 2015](http://lgs-landau.de/) as arcarde box game, using only gaze and one buzzer as input. Nearly 3000 sessions were played during the summer without any known issues. Powered by Java and jMonkey engine.

For the scientific background, please take a look at the [paper from EyePlay workshop](/media/schaefer_etal_schaugenau.pdf).

## History
This project was started by computer science students as part of their studies in "Projektpraktikum" (research lab) during summer semester 2013, supervised by Dr. Tina Walber. Main developers were Raphael Menges and Kevin Schmidt. Other students involved were Heiko Bengel, Matthias Kuich and Marc Wilhelmy, but none of their source code is contained in current version.

## Presentations
* Nacht der Technik 2013 at HWK in Koblenz
* Nacht der Technik 2014 at HWK in Koblenz
* [CV-Tag 2014](http://userpages.uni-koblenz.de/~cvtag/web/demos/demos-2014/)
* [EyePlay workshop in Toronto](http://www.eyeplayworkshop.org/)
* State Horticultural Show Landau 2015

## Screenshots
![Screenshot-A](/media/Screenshot-A.png "Idle screen")
![Screenshot-B](/media/Screenshot-B.png "Tutorial")
![Screenshot-C](/media/Screenshot-C.png "Beginning of game")
![Screenshot-D](/media/Screenshot-D.png "Game")
![Screenshot-E](/media/Screenshot-E.png "Name input")

## Video
For a video of the beta version, hosted on YouTube, click [here](https://youtu.be/eEWfZ5EqSLE).

## Launch
Run main function in "schaugenau.app" in Eclipse. Due to license issues, neither the connection to our image database nor the usage of an eyetracker is integrated. All regarding lines in code are marked with a "TODO" comment. The game itself should work out of the box with mouse support, which emulates the gaze. During gameplay, errors about no connection to an image database are thrown and placeholders are used instead.

## Dependencies
* jMonkey: http://jmonkeyengine.org
* log4j: http://logging.apache.org/log4j/2.x
* LWJGL: https://www.lwjgl.org
* OpenAL: https://www.openal.org
* opencsv: http://opencsv.sourceforge.net

All necessary dependencies are included in this repository.

## License
MIT License

## Acknowledgments
* Supported by [EYEVIDO](http://eyevido.de/)
* Sound and music by Sebastian Prusak
