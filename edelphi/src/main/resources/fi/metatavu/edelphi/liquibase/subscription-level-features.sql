INSERT INTO SubscriptionLevelFeature (id, feature, subscriptionLevel) VALUES 
  (1, 'BASIC_USAGE', 'PREMIUM'),
  (2, 'CREATE_PANELS', 'PREMIUM'),
  (3, 'ACCESS_HELPDESK', 'PREMIUM'),
  (4, 'MANAGE_PANEL_QUERIES', 'PREMIUM'),
  (5, 'MANAGE_PANEL', 'PREMIUM'),
  (6, 'MANAGE_PANEL_INVITATIONS', 'PREMIUM'),
  (7, 'MANAGE_PANEL_COMMUNICATION', 'PREMIUM'),
  (8, 'MANAGE_PANEL_MATERIALS', 'PREMIUM'),
  (9, 'ACCESS_PANEL_QUERY_ACTIVITY', 'PREMIUM'),
  (10, 'ACCESS_PANEL_QUERY_RESULTS', 'PREMIUM'),
  (11, 'ACCESS_PANEL_QUERY_EXPORT', 'PREMIUM'),
  (12, 'ACCESS_PANEL_REPORT_COMPARISON', 'PREMIUM'),
  (13, 'MANAGE_PANEL_TIMESTAMPS', 'PREMIUM'),
  (14, 'SERVICE_LIVE_DELPHI', 'PREMIUM'),
  (15, 'SERVICE_SANELUKONE', 'PREMIUM'),
  
  (16, 'BASIC_USAGE', 'PLUS'),
  (17, 'CREATE_PANELS', 'PLUS'),
  (18, 'ACCESS_HELPDESK', 'PLUS'),
  (19, 'MANAGE_PANEL_QUERIES', 'PLUS'),
  (20, 'MANAGE_PANEL', 'PLUS'),
  (21, 'MANAGE_PANEL_INVITATIONS', 'PLUS'),
  (22, 'MANAGE_PANEL_COMMUNICATION', 'PLUS'),
  (23, 'MANAGE_PANEL_MATERIALS', 'PLUS'),
  (24, 'ACCESS_PANEL_QUERY_ACTIVITY', 'PLUS'),
  (25, 'ACCESS_PANEL_QUERY_RESULTS', 'PLUS'),
  (26, 'ACCESS_PANEL_QUERY_EXPORT', 'PLUS'),
  (27, 'ACCESS_PANEL_REPORT_COMPARISON', 'PLUS'),
  
  (28, 'BASIC_USAGE', 'BASIC'),
  (29, 'CREATE_PANELS', 'BASIC'),
  (30, 'ACCESS_HELPDESK', 'BASIC'),
  (31, 'MANAGE_PANEL_QUERIES', 'BASIC'),
  (32, 'MANAGE_PANEL', 'BASIC'),
  (33, 'MANAGE_PANEL_INVITATIONS', 'BASIC'),
  (34, 'MANAGE_PANEL_COMMUNICATION', 'BASIC'),
  (35, 'MANAGE_PANEL_MATERIALS', 'BASIC');
  
INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) select max(id) + 1, 'SubscriptionLevelFeature' from SubscriptionLevelFeature;