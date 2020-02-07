INSERT INTO 
  LocalizedEntry 
VALUES (), ();
  
INSERT INTO 
  LocalizedValue (entry_id, locale, text)
SELECT 
  MAX(id) - 1, 'fi', '1d skaalojen monivalintakysymys'  
FROM 
  LocalizedEntry;  
    
INSERT INTO 
  LocalizedValue (entry_id, locale, text)
SELECT 
  MAX(id) - 1, 'en', 'Multiple 1d scale question'  
FROM 
  LocalizedEntry;  
  
INSERT INTO 
  LocalizedValue (entry_id, locale, text)
SELECT 
  MAX(id), 'fi', 'Tällä sivulla panelistit pääsevät vastaamaan useisiin 1d skaalakysymyksiin yhdellä kerralla.'  
FROM 
  LocalizedEntry; 
    
INSERT INTO 
  LocalizedValue (entry_id, locale, text)
SELECT 
  MAX(id), 'en', 'This page allows panelists to answer multiple 1D scale questions at once.'  
FROM 
  LocalizedEntry; 

INSERT INTO 
  QueryPageTemplate (id, name_id, description_id, iconName, archived, creator_id, created, lastModifier_id, lastModified, pageType)
SELECT 
  (SELECT max(id) + 1 FROM QueryPageTemplate), MAX(id) - 1, MAX(id), 'multiple1dscales', false, 1, NOW(), 1, NOW(),  'THESIS_MULTIPLE_1D_SCALES'  
FROM 
  LocalizedEntry;
  
DELETE FROM hibernate_sequences WHERE sequence_name = 'QueryPageSettingKey';
INSERT INTO hibernate_sequences (sequence_name, sequence_next_hi_value) SELECT 'QueryPageSettingKey', max(id) + 4 FROM QueryPageSettingKey;
  
INSERT INTO  
  QueryPageSettingKey (id, name)
VALUES   
  ((SELECT sequence_next_hi_value - 3 FROM hibernate_sequences WHERE sequence_name = 'QueryPageSettingKey'), 'multiple1dscales.label'),
  ((SELECT sequence_next_hi_value - 2 FROM hibernate_sequences WHERE sequence_name = 'QueryPageSettingKey'), 'multiple1dscales.options'),
  ((SELECT sequence_next_hi_value - 1 FROM hibernate_sequences WHERE sequence_name = 'QueryPageSettingKey'), 'multiple1dscales.theses')
ON DUPLICATE KEY UPDATE name = VALUES(name);


INSERT INTO 
  QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) 
SELECT 
  (SELECT max(id) + 1 FROM QueryPageTemplateSetting), id, (SELECT id FROM QueryPageTemplate WHERE iconName = 'multiple1dscales') 
FROM 
  QueryPageSettingKey 
WHERE 
  name = 'visible';

INSERT INTO QueryPageTemplateSimpleSetting (id, value) select (select max(id) from QueryPageTemplateSetting), '1';

DELETE FROM hibernate_sequences WHERE sequence_name = 'QueryPageTemplate';
INSERT INTO hibernate_sequences (sequence_name, sequence_next_hi_value) SELECT 'QueryPageTemplate', max(id) + 1 FROM QueryPageTemplate;