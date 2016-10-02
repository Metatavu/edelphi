INSERT INTO
  SettingKey (id, name)
VALUES 
  (1, 'system.environment');

INSERT INTO
  Setting (id, key_id, value)
VALUES 
  (1, 1, 'test');
  
INSERT INTO
  User (id, firstName, lastName, archived, creator_id, created, lastModifier_id, lastModified, subscriptionLevel)
VALUES 
  (1, 'Admin', 'User', false, 1, NOW(), 1, NOW(), 'NONE');
  
INSERT INTO
  UserEmail (id, user_id, address)
VALUES
  (1, 1, 'admin@example.com');

INSERT INTO
  Resource (id, name, urlName, description, parentFolder_id, type, visible, archived, created, lastModifier_id, lastModified, indexNumber)
VALUES
  (1, 'Root Folder', 'root', 'Root Folder', NULL, 'FOLDER', true, false, NOW(), 1, NOW(), 0);
  
INSERT INTO 
  Folder 
VALUES 
  (1);
  
INSERT INTO
  Delfoi (id, domain, rootFolder_id)
VALUES
  (1, 'test.edelphi.fi', 1);
  
INSERT INTO
  LocalizedEntry (id)
VALUES 
  (1),
  (2),
  (3),
  (4),
  (5),
  (6),
  (7),
  (8);
  
INSERT INTO 
  UserRole (id, name_id)
VALUES 
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 4),
  (5, 5),
  (6, 6),
  (7, 7),
  (8, 8);
  
INSERT INTO
  LocalizedValue (id, entry_id, locale, text)
VALUES 
  (1, 1, 'en', 'Everyone'),
  (2, 2, 'en', 'Administrators'),
  (3, 3, 'en', 'Managers'),
  (4, 4, 'en', 'Users'),
  (5, 5, 'en', 'Guests'),
  (6, 6, 'en', 'Panel Managers'),
  (7, 7, 'en', 'Panelists'),
  (8, 8, 'en', 'Panel Guests');
  
INSERT INTO 
  DelfoiUserRole (id)
VALUES 
  (2), 
  (3), 
  (4), 
  (5);
  
INSERT INTO 
  PanelUserRole (id)
VALUES 
  (6), 
  (7), 
  (8);
  
INSERT INTO
  DelfoiUser (id, delfoi_id, user_id, archived, creator_id, created, lastModifier_id, lastModified, role_id)
VALUES
  (1, 1, 1, false, 1, NOW(), 1, NOW(), 2);
  
INSERT INTO
  AuthSource (id, name, strategy)
VALUES 
  (1, 'test', 'test');