-- Defined core privileges
INSERT INTO privilege VALUES ('PRIVILEGE1', 'CREATE_ENTITY')
INSERT INTO privilege VALUES ('PRIVILEGE2', 'EDIT_ENTITY')
INSERT INTO privilege VALUES ('PRIVILEGE3', 'DELETE_ENTITY')
INSERT INTO privilege VALUES ('PRIVILEGE4', 'VIEW_ENTITY')
INSERT INTO privilege VALUES ('PRIVILEGE5', 'CREATE_USER')
INSERT INTO privilege VALUES ('PRIVILEGE6', 'DELETE_USER')
INSERT INTO privilege VALUES ('PRIVILEGE7', 'ACCESS_BASELINE')
INSERT INTO privilege VALUES ('PRIVILEGE8', 'ACCESS_UPDATE')
INSERT INTO privilege VALUES ('PRIVILEGE9', 'ACCESS_AMENDMENT_FORMS')
INSERT INTO privilege VALUES ('PRIVILEGE10', 'ACCESS_REPORTS')
INSERT INTO privilege VALUES ('PRIVILEGE11', 'ACESSS_UTILITY_ROUTINES')

-- Defined  core roles
INSERT INTO role (uuid, name, description, deleted) VALUES ('ROLE1', 'ADMINISTRATOR', 'Administrator of OpenHDS', false)
INSERT INTO role (uuid, name, description, deleted) VALUES ('ROLE2', 'DATA CLERK', 'Data Clerk of OpenHDS', false)
INSERT INTO role (uuid, name, description, deleted) VALUES ('ROLE3', 'DATA MANAGER', 'Data Manager of OpenHDS', false)
INSERT INTO role (uuid, name, description, deleted) VALUES ('ROLE4', 'TEST USER', 'Test User of OpenHDS', false)
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE1')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE2')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE3')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE4')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE5')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE6')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE7')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE8')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE9')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE10')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE1', 'PRIVILEGE11')

INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE2', 'PRIVILEGE4')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE4', 'PRIVILEGE1')
INSERT INTO role_privileges (role_uuid, privilege_uuid) VALUES ('ROLE4', 'PRIVILEGE8')

-- Defined Admin user
INSERT INTO user (uuid, firstName, lastName, fullName, description, username, password, lastLoginTime, deleted) VALUES ('User 1', 'FirstName', 'LastName', 'Administrator', 'Administrator User', 'admin', 'test', 0, false)
INSERT INTO user_roles (user_uuid, role_uuid) VALUES ('User 1', 'ROLE1')
INSERT INTO user (uuid, firstName, lastName, fullName, description, username, password, lastLoginTime, deleted) VALUES ('User 2', 'Test', 'Account', 'Test Account', 'Test User Account', 'test', 'test', 0, false)
INSERT INTO user_roles (user_uuid, role_uuid) VALUES ('User 2', 'ROLE4')
INSERT INTO user (uuid, firstName, lastName, fullName, description, username, password, lastLoginTime, deleted) VALUES ('User 3', 'DataClerk', 'Account', 'Test Account', 'Test User Account', 'dataclerk', 'dataclerk', 0, false)
INSERT INTO user_roles (user_uuid, role_uuid) VALUES ('User 3', 'ROLE2')

-- Location Hierarchy root
INSERT INTO locationhierarchy(uuid,name,extId,level_uuid,parent_uuid) VALUES('hierarchy_root','', 'HIERARCHY_ROOT', NULL,NULL)

-- Field Worker
INSERT INTO fieldworker (uuid, extid, firstname, lastname, deleted) VALUES ('UnknownFieldWorker','UNK', 'Unknown', 'FieldWorker', false)
INSERT INTO fieldworker (uuid, extid, firstname, lastname, deleted) VALUES ('FieldWorker1','FWEK1D', 'Editha', 'Kaweza', false)

-- Location Hierarchy Levels, these must be configured
INSERT INTO locationhierarchylevel(uuid,keyIdentifier,name) VALUES('HierarchyLevel1',1,'LGA')
INSERT INTO locationhierarchylevel(uuid,keyIdentifier,name) VALUES('HierarchyLevel2',2,'Ward')
INSERT INTO locationhierarchylevel(uuid,keyIdentifier,name) VALUES('HierarchyLevel3',3,'Village')

-- Unknown Individual: This should always be pre-populated
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES('Unknown Individual','UNK','Unknown','','UNKNOWN','1', '1900-12-19', NULL, NULL,'User 1','2009-12-19','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 1','BRIHA001','Brian','','Harold','1','1987-09-02','Unknown Individual','Unknown Individual','User 1','2012-02-28','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 2','CHRHA001','Chris','','Harold','1','1989-02-21','Unknown Individual','Unknown Individual','User 1','2012-02-28','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 3','SARRO001','Sarah','','Ross','2','1980-01-02','Unknown Individual','Unknown Individual','User 1','2012-02-28','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 4','JESMA001','Jessica','','Marsh','2','2011-01-03','Indiv 1','Indiv 3','User 1','2012-03-19','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 5','TONMA001','Tony','','Marsh','1','1988-03-19','Unknown Individual','Unknown Individual','User 1','2012-03-19','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 6','HENMA001','Henry','','Marsh','1','1998-03-20','Unknown Individual','Unknown Individual','User 1','2012-03-20','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 7','ABBHA001','Abby','','Harold','2','2011-04-15','Unknown Individual','Unknown Individual','User 1','2012-03-22','A',NULL,NULL,NULL,false,'UnknownFieldWorker')
INSERT INTO individual(uuid,extId,firstName,middleName,lastName,gender,dob,mother_uuid,father_uuid,insertBy_uuid,insertDate,status,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid) VALUES ('Indiv 8','PETBA001','Peter','','Bash','1','2009-01-01','Unknown Individual','Unknown Individual','User 1','2012-04-17','A',NULL,NULL,NULL,false,'UnknownFieldWorker')

INSERT INTO location(uuid,extId,locationName,locationLevel_uuid,locationType,insertDate,voidDate,voidReason,voidBy_uuid,deleted,collectedBy_uuid,insertBy_uuid,status,locationHead_uuid) VALUES ('Location1','AKP0101003','Harolds House',NULL,'RUR','2012-02-28',NULL,NULL,NULL,false,'FieldWorker1','User 1','A','Indiv 1')

INSERT INTO round(uuid,roundNumber,startDate,endDate) VALUES('ROUND 1',1,'2010-06-30','2010-07-31')
INSERT INTO visit(uuid,extId,visitDate,status,insertDate,collectedBy_uuid,visitLocation_uuid,deleted,roundNumber,insertBy_uuid) VALUES ('Visit1','VMBI01','2012-02-28','P','2012-03-28','FieldWorker1','Location1',false,1,'User 1')

INSERT INTO residency(uuid,location_uuid,individual_uuid,startDate,startType,endDate,endType,collectedBy_uuid,deleted,status,insertDate,insertBy_uuid) VALUES ('Residency1','Location1','Indiv 2','1989-02-21','BIR',NULL,'NA','FieldWorker1',false,'A','2012-04-17','User 1')
INSERT INTO residency(uuid,location_uuid,individual_uuid,startDate,startType,endDate,endType,collectedBy_uuid,deleted,status,insertDate,insertBy_uuid) VALUES ('Residency2','Location1','Indiv 4','2011-03-01','BIR',NULL,'NA','FieldWorker1',false,'A','2012-04-17','User 1')
INSERT INTO residency(uuid,location_uuid,individual_uuid,startDate,startType,endDate,endType,collectedBy_uuid,deleted,status,insertDate,insertBy_uuid) VALUES ('Residency3','Location1','Indiv 3','1993-02-01','IMG',NULL,'NA','FieldWorker1',false,'A','2012-04-17','User 1')
INSERT INTO residency(uuid,location_uuid,individual_uuid,startDate,startType,endDate,endType,collectedBy_uuid,deleted,status,insertDate,insertBy_uuid) VALUES ('Residency4','Location1','Indiv 7','2012-04-15','BIR',NULL,'NA','FieldWorker1',false,'A','2012-04-17','User 1')
INSERT INTO residency(uuid,location_uuid,individual_uuid,startDate,startType,endDate,endType,collectedBy_uuid,deleted,status,insertDate,insertBy_uuid) VALUES ('Residency5','Location1','Indiv 8','2009-01-01','BIR',NULL,'NA','FieldWorker1',false,'A','2012-04-17','User 1')
INSERT INTO residency(uuid,location_uuid,individual_uuid,startDate,startType,endDate,endType,collectedBy_uuid,deleted,status,insertDate,insertBy_uuid) VALUES ('Residency6','Location1','Indiv 5','1993-02-01','ENU',NULL,'NA','FieldWorker1',false,'A','2012-04-17','User 1')

INSERT INTO socialgroup(uuid, extId, deleted, insertdate, groupName, collectedby_uuid, insertby_uuid, grouphead_uuid,groupType,status) VALUES ('SocialGroup1','SG01',false,'2012-04-17','Harold','FieldWorker1','User 1','Indiv 1','FAM','P')
INSERT INTO socialgroup(uuid, extId, deleted, insertdate, groupName, collectedby_uuid, insertby_uuid, grouphead_uuid,groupType,status) VALUES ('SocialGroup2','SG02',false,'2012-04-17','Marsh','FieldWorker1','User 1','Indiv 5','FAM','P')
INSERT INTO socialgroup(uuid, extId, deleted, insertdate, groupName, collectedby_uuid, insertby_uuid, grouphead_uuid,groupType,status) VALUES ('SocialGroup3','SG03',false,'2012-04-17','Ross','FieldWorker1','User 1','Indiv 3','FAM','P')
INSERT INTO socialgroup(uuid, extId, deleted, insertdate, groupName, collectedby_uuid, insertby_uuid, grouphead_uuid,groupType,status) VALUES ('SocialGroup4','SG04',false,'2012-04-17','Bash','FieldWorker1','User 1','Indiv 8','FAM','P')

INSERT INTO death (uuid,deleted,insertDate,status,voidDate,voidReason,deathCause,deathDate,deathPlace,collectedBy_uuid,insertBy_uuid,voidBy_uuid,individual_uuid,visitDeath_uuid,house_uuid,household_uuid) VALUES ('death1','','2010-06-09','P',NULL,NULL,'Cause','2010-06-01','Place','FieldWorker1','User 1',NULL,'Indiv 8','Visit1','Location1','SocialGroup1')

INSERT INTO inmigration(uuid,deleted,insertDate,recordedDate,voidDate,voidReason,status,origin,reason,migType,insertBy_uuid,voidBy_uuid,collectedBy_uuid,house_uuid,household_uuid,individual_uuid,residency_uuid,visit_uuid) VALUES ('Inmigration1',false,'2012-04-17','2011-01-05',NULL,NULL,'P',1,1,'INTERNAL_INMIGRATION','User 1',NULL,'FieldWorker1','Location1','SocialGroup1','Indiv 5','Residency6','Visit1')
INSERT INTO outmigration(uuid,deleted,insertDate,voidDate,voidReason,status,destination,reason,recordedDate,insertBy_uuid,voidBy_uuid,collectedBy_uuid,individual_uuid,residency_uuid,visit_uuid) VALUES ('Outmigration1',false,'2012-04-17',NULL,NULL,'P','name','reason','1997-05-05','User 1',NULL,'FieldWorker1','Indiv 5','Residency6','Visit1')
