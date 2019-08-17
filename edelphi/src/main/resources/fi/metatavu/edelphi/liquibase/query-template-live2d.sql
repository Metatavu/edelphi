SET @CLOCENID = (SELECT max(id) FROM LocalizedEntry);
SET @CLOCVAID = (SELECT max(id) FROM LocalizedValue);
SET @TEMPLID = (SELECT id from QueryPageTemplate where iconName = 'live2d');
SET @TEMPLSETID = (SELECT max(id) FROM QueryPageTemplateSetting);
SET @SETKEYID = (SELECT max(id) FROM QueryPageSettingKey);

INSERT INTO QueryPageSettingKey VALUES (@SETKEYID + 1, 'live2d.options.x'), (@SETKEYID + 2, 'live2d.options.y'), (@SETKEYID + 3, 'live2d.label.x'), (@SETKEYID + 4, 'live2d.label.y'), (@SETKEYID + 5, 'live2d.color.x'), (@SETKEYID + 6, 'live2d.color.y');

INSERT INTO LocalizedEntry VALUES (@CLOCENID + 1), (@CLOCENID + 2), (@CLOCENID + 3), (@CLOCENID + 4);

INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 1, '---&--&-&%2B/-&%2B&%2B%2B&%2B%2B%2B', 'en', @CLOCENID + 1);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 2, '---&--&-&%2B/-&%2B&%2B%2B&%2B%2B%2B', 'fi', @CLOCENID + 1);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 3, '---&--&-&%2B/-&%2B&%2B%2B&%2B%2B%2B', 'en', @CLOCENID + 2);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 4, '---&--&-&%2B/-&%2B&%2B%2B&%2B%2B%2B', 'fi', @CLOCENID + 2);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 5, '-- Insert Text --', 'en', @CLOCENID + 3);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 6, '-- Teksti --', 'fi', @CLOCENID + 3);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 7, '-- Insert Text --', 'en', @CLOCENID + 4);
INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (@CLOCVAID + 8, '-- Teksti --', 'fi', @CLOCENID + 4);

INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 1, (SELECT id FROM QueryPageSettingKey WHERE name = 'live2d.options.x'), @TEMPLID);
INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 2, (SELECT id FROM QueryPageSettingKey WHERE name = 'live2d.options.y'), @TEMPLID);
INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 3, (SELECT id FROM QueryPageSettingKey WHERE name = 'live2d.label.x'), @TEMPLID);
INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 4, (SELECT id FROM QueryPageSettingKey WHERE name = 'live2d.label.y'), @TEMPLID);

INSERT INTO QueryPageTemplateLocalizedSetting (id, value_id) VALUES (@TEMPLSETID + 1, @CLOCENID + 1);
INSERT INTO QueryPageTemplateLocalizedSetting (id, value_id) VALUES (@TEMPLSETID + 2, @CLOCENID + 2);
INSERT INTO QueryPageTemplateLocalizedSetting (id, value_id) VALUES (@TEMPLSETID + 3, @CLOCENID + 3);
INSERT INTO QueryPageTemplateLocalizedSetting (id, value_id) VALUES (@TEMPLSETID + 4, @CLOCENID + 4);


INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 5, (SELECT id FROM QueryPageSettingKey WHERE name = 'live2d.color.x'), @TEMPLID);
INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 6, (SELECT id FROM QueryPageSettingKey WHERE name = 'live2d.color.y'), @TEMPLID);
INSERT INTO QueryPageTemplateSetting (id, key_id, queryPageTemplate_id) VALUES (@TEMPLSETID + 7, (SELECT id FROM QueryPageSettingKey WHERE name = 'visible'), @TEMPLID);
INSERT INTO QueryPageTemplateSimpleSetting (id, value) VALUES (@TEMPLSETID + 5, 'RED');
INSERT INTO QueryPageTemplateSimpleSetting (id, value) VALUES (@TEMPLSETID + 6, 'GREEN');
INSERT INTO QueryPageTemplateSimpleSetting (id, value) VALUES (@TEMPLSETID + 7, '1');

DELETE FROM hibernate_sequences WHERE sequence_name in ('QueryPageTemplateSetting', 'QueryPageTemplateSimpleSetting', 'LocalizedEntry', 'QueryPageTemplateLocalizedSetting', 'QueryPageSettingKey');
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) select max(id) + 1, 'QueryPageTemplateSetting' from QueryPageTemplateSetting;
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) select max(id) + 1, 'QueryPageTemplateSimpleSetting' from QueryPageTemplateSimpleSetting;
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) select max(id) + 1, 'LocalizedEntry' from LocalizedEntry;
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) select max(id) + 1, 'QueryPageTemplateLocalizedSetting' from QueryPageTemplateLocalizedSetting;
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) select max(id) + 1, 'QueryPageSettingKey' from QueryPageSettingKey;