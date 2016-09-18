insert into
  User (id, firstName, lastName, archived, creator_id, created, lastModifier_id, lastModified)
values 
  (1, 'Admin', 'User', false, 1, NOW(), 1, NOW());

insert into
  Resource (id, name, urlName, description, parentFolder_id, type, visible, archived, created, lastModifier_id, lastModified, indexNumber)
values
  (1, 'Root Folder', 'root', 'Root Folder', null, 'FOLDER', true, false, NOW(), 1, NOW(), 0);
  
insert into 
  Folder 
values 
  (1);
  
insert into
  Delfoi (id, domain, rootFolder_id)
values
  (1, 'test.edelphi.fi', 1);