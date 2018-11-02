--
-- This SQL script builds the first Life Manager database, deleting any pre-existing version.
--
-- @author TeamGG
-- @version November, 2018
--

DROP TABLE IF EXISTS LifeUser;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS Activity;
DROP TABLE IF EXISTS Schedule;


--Create the schema
CREATE TABLE LifeUser (
	ID integer PRIMARY KEY,
	UserName varchar(50),
	UserEmail varchar(100)
	);

CREATE TABLE Category (
	ID integer PRIMARY KEY,
	CategoryType varchar(50)
	);

CREATE TABLE Activity(
	ID integer PRIMARY KEY,
	ActivityName varchar(50),
	ActivityLocation varchar(50),
	ActivityDescription varchar(50),
	CategoryID integer REFERENCES Category(ID),
	ActivityDate DATE,
	ActivityStartTime TIMESTAMP,
	ActivityEndTime TIMESTAMP,
	ActivityLabel varchar(150),
	ActivityNote varchar(150)
);

CREATE TABLE Schedule(
	ID integer PRIMARY KEY,
	UserID integer REFERENCES LifeUser(ID),
	ActivityID integer REFERENCES Activity(ID)
);


-- Allow a user to select data from the tables.
GRANT SELECT ON LifeUser TO PUBLIC;
GRANT SELECT ON Category TO PUBLIC;
GRANT SELECT ON Activity TO PUBLIC;
GRANT SELECT ON Schedule TO PUBLIC;


--ADD sample records
INSERT INTO LifeUser VALUES (1, 'Josh', 'jww23@calvin.edu');
INSERT INTO LifeUser VALUES (2, 'Peter', 'pjh27@calvin.edu');
INSERT INTO LifeUser VALUES (3, 'Chris', 'cap32@calvin.edu');
INSERT INTO LifeUser VALUES (4, 'Hamin', 'hh32@calvin.edu');
INSERT INTO LifeUser VALUES (5, 'Chan', 'ck45@calvin.edu');

INSERT INTO Category VALUES (1, 'Self-Dev');
INSERT INTO Category VALUES (2, 'Personal');
INSERT INTO Category VALUES (3, 'Direct');
INSERT INTO Category VALUES (4, 'Indirect');
INSERT INTO Category VALUES (5, 'ETC');

INSERT INTO Activity VALUES (1, 'Study', 'Epsilon Study Room', 'Study for Intro Stats',2, '2018-11-01', 
'2018-11-01 12:21:34', '2018-11-01 13:21:34', 'Study section 5', 'Done with sections 1 to 4');
INSERT INTO Activity VALUES (2, 'Exercise', 'Mad MAx Gym', 'Working out',1, '2018-11-01', 
'2018-11-01 18:11:14', '2018-11-01 20:21:48', 'Work on Arms and Legs', 'Do two reps of 20 each for 3 sessions');
INSERT INTO Activity VALUES (3, 'Meet Keith', 'Celebration North Cinema', 'Watching Venmo',4, '2018-11-01', 
'2018-11-01 22:00:00', '2018-11-01 23:23:14', ' NA', 'Bring free popcorn Basket');
INSERT INTO Activity VALUES (4,'Work on CS262 project', 'PoolSide', 'Update the prototype',3, '2018-11-02', 
'2018-11-02 9:00:00', '2018-11-02 14:23:14', 'Adding a setting function', 'Chan worked on part of the setting, check his work and improve from there');



INSERT INTO Schedule (1, 1, 1);
INSERT INTO Schedule (2, 3, 2);
INSERT INTO Schedule (3, 4, 3);
INSERT INTO Schedule (4, 2, 3);




  



