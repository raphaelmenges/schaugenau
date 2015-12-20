/*
 * SQL Create table statements
 */


/* Create table for pictures saved as BLOB's
 * ID is set when picture is imported
 * TagGerman
 * TagEnglish
 * UsedAsCorrect: How often the picture was shown as correct one
 * ChosenAsCorrect: How often the picture was chosen correctly
 */
Create Table pictures(
	ID INT NOT NULL AUTO_INCREMENT,
    TagGerman VARCHAR(50) NOT NULL,
    TagEnglish VARCHAR(50) NOT NULL,
    Picture LONGBLOB NOT NULL,
    UsedAsCorrect INT,
    ChosenAsCorrect INT,
    PRIMARY KEY (ID)
);

/* Create table for highscores
 * Name: Name of the player
 * Score: Score of the Player
 * CreationTime: timelogging (YYYY-MM-DD hh:mm:ss)
 * PlayedTime: played Time of the round
 * CorrectPictures: Correctly chosen pictures
 * IncorrectPictures: equivalent
 * Pkey: Count of highscores for primary key 
 */
Create Table scores(
	Name VARCHAR(50) NOT NULL,
	Score INT,
	Gamestyle VARCHAR(1) NOT NULL,	
	CreationTime datetime,
	PlayedTime INT,
	AvMultiplicator DOUBLE,
	MaxMultiplicator INT,
	CorrectPictures INT,
	IncorrectPictures INT,
	Pkey INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (Pkey)
);

/* Create table for surveyItems
 *
 */
Create Table surveyItems(
	Type VARCHAR(50),
	QuestionEN VARCHAR(255),
	QuestionDE VARCHAR(255),
	AnswerM1EN VARCHAR(255),
	AnswerM2EN VARCHAR(255),
	AnswerM3EN VARCHAR(255),
	AnswerM1DE VARCHAR(255),
	AnswerM2DE VARCHAR(255),
	AnswerM3DE VARCHAR(255),
	AnswerLikertLeftEN VARCHAR(255),
	AnswerLikertRightEN VARCHAR(255),
	AnswerLikertLeftDE VARCHAR(255),
	AnswerLikertRightDE VARCHAR(255),
	SessionID INT,
	OrderPriority INT,
	Pkey INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (Pkey)
);

/* Create table for surveyItems
 *
 */
Create Table surveyResults(
	Gamestyle VARCHAR(10),
	Score INT,
	ItemID INT,
	Choice INT,
	Participant INT,
	Pkey INT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (Pkey)
);
