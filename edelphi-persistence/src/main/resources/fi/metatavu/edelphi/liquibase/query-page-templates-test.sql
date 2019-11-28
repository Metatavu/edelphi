INSERT INTO 
  LocalizedEntry 
VALUES 
  (50),
  (51),
  (52),
  (53),
  (54),
  (55),
  (56),
  (57),
  (58),
  (59),
  (60),
  (61),
  (62),
  (100),
  (101),
  (102),
  (103),
  (104),
  (105),
  (106),
  (107),
  (108),
  (109),
  (110),
  (111),
  (112);
  
INSERT INTO 
  LocalizedValue (id, text, locale, entry_id)
VALUES 
  (50, 'Text', 'en', 50),
  (100, 'Text', 'en', 50),
  (51, 'Expertise', 'en', 51),
  (101, 'Expertise', 'en', 51),
  (52, 'Scale 1d', 'en', 52),
  (102, 'Scale 1d', 'en', 52),
  (53, 'Scale 2d', 'en', 53),
  (103, 'Scale 2d', 'en', 53),
  (54, 'Time Serie', 'en', 54),
  (104, 'Time Serie', 'en', 54),
  (55, 'Grouping', 'en', 55),
  (105, 'Grouping', 'en', 55),
  (56, 'Multiselect', 'en', 56),
  (106, 'Multiselect', 'en', 56),
  (57, 'Ordering', 'en', 57),
  (107, 'Ordering', 'en', 57),
  (58, 'Form', 'en', 58),
  (108, 'Form', 'en', 58),
  (60, 'Background Information Form', 'en', 60),
  (110, 'Background Information Form', 'en', 60),
  (61, 'Collage 2d', 'en', 61),
  (111, 'Collage 2d', 'en', 61),
  (62, 'Multiple 2d scales', 'en', 62),
  (112, 'Multiple 2d scales', 'en', 62);

INSERT INTO 
  QueryPageTemplate (id, iconName, archived, creator_id, created, lastModifier_id, lastModified, name_id, pageType, description_id)
VALUES 
  (1,'text',false,1,NOW(),1,NOW(),50,'TEXT',100),
  (2,'expertise',false,1,NOW(),1,NOW(),51,'EXPERTISE',101),
	(3,'scale1d',false,1,NOW(),1,NOW(),52,'THESIS_SCALE_1D',102),
	(4,'scale2d',false,1,NOW(),1,NOW(),53,'THESIS_SCALE_2D',103),
	(5,'timeserie',false,1,NOW(),1,NOW(),54,'THESIS_TIME_SERIE',104),
	(6,'timeline',false,1,NOW(),1,NOW(),55,'THESIS_TIMELINE',105),
	(7,'grouping',false,1,NOW(),1,NOW(),56,'THESIS_GROUPING',106),
	(8,'multiselect',false,1,NOW(),1,NOW(),57,'THESIS_MULTI_SELECT',107),
	(9,'ordering',false,1,NOW(),1,NOW(),58,'THESIS_ORDER',108),
	(10,'form',false,1,NOW(),1,NOW(),59,'FORM',109),
	(11,'backgroundinformationform',false,1,NOW(),1,NOW(),60,'FORM',110),
	(12,'collage2d',false,1,NOW(),1,NOW(),61,'COLLAGE_2D',111),
	(13,'multiple2dscales',false,1,NOW(),1,NOW(),62,'THESIS_MULTIPLE_2D_SCALES',112);