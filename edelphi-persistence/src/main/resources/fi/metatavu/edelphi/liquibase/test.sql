INSERT INTO
  SettingKey (id, name)
VALUES 
  (1, 'system.environment'),
  (2, 'paytrail.merchantId'),
  (3, 'paytrail.merchantSecret');

INSERT INTO
  Setting (id, key_id, value)
VALUES 
  (1, 1, 'test'),
  (2, 2, '13466'),
  (3, 3, '6pKF4jkv97zmqBJ3ZL8gUw5DfT2NMQ');
  
INSERT INTO
  User (id, firstName, lastName, archived, creator_id, created, lastModifier_id, lastModified, subscriptionLevel)
VALUES 
  (1, 'Admin', 'User', false, 1, NOW(), 1, NOW(), 'NONE');
  
INSERT INTO
  UserEmail (id, user_id, address)
VALUES
  (1, 1, 'admin@example.com');

UPDATE
  User
SET
  defaultEmail_id = 1
WHERE
  id = 1;
  
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
  (1, 'test.edelphi.org', 1);

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
  DelfoiDefaults (id, delfoi_id, defaultPanelCreatorRole_id, defaultDelfoiUserRole_id)
VALUES
  (1, 1, 6, 4);
  
INSERT INTO
  DelfoiUser (id, delfoi_id, user_id, archived, creator_id, created, lastModifier_id, lastModified, role_id)
VALUES
  (1, 1, 1, false, 1, NOW(), 1, NOW(), 2);
  
INSERT INTO
  AuthSource (id, name, strategy)
VALUES 
  (1, 'test', 'test'),
  (2, 'eDelphi', 'Keycloak');
  
INSERT INTO
  DelfoiAuth (id, delfoi_id, authSource_id)
VALUES 
  (1, 1, 2);
  
INSERT INTO
  AuthSourceSetting (id, authSource_id, settingKey, value)
VALUES 
  (1, 2, 'oauth.keycloak.apiKey', 'edelphi.org'),
  (2, 2, 'oauth.keycloak.apiSecret', 'secret'),
  (3, 2, 'oauth.keycloak.serverUrl', 'http://localhost:8380/auth'),
  (4, 2, 'oauth.keycloak.realm', 'edelphi'),
  (5, 2, 'oauth.keycloak.callbackUrl', 'http://test.edelphi.org:8280/dologin.page?loginType=Keycloak&_stg=rsp'),
  (6, 2, 'oauth.keycloak.adminUser', 'admin'),
  (7, 2, 'oauth.keycloak.adminPassword', 'admin');
 
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) VALUES ((SELECT max(id) + 1 FROM Resource), 'Resource');