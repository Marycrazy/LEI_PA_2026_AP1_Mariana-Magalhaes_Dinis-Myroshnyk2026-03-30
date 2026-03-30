-- ------------------------------
-- OPTION
-- ------------------------------

OPTION IMPORT;

-- ------------------------------
-- TABLE: client
-- ------------------------------

DEFINE TABLE client TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD scale ON client TYPE string ASSERT $value INSIDE ['A', 'B', 'C', 'D'] PERMISSIONS FULL;
DEFINE FIELD sector ON client TYPE string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: client
-- ------------------------------

INSERT [ { id: client:ri02s3ajbxii7qnrkym2, scale: 'A', sector: 'Test Sector' } ];

-- ------------------------------
-- TABLE: contains
-- ------------------------------

DEFINE TABLE contains TYPE RELATION IN equipment OUT repair SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON contains TYPE record<equipment> PERMISSIONS FULL;
DEFINE FIELD out ON contains TYPE record<repair> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: contains
-- ------------------------------


-- ------------------------------
-- TABLE: employee
-- ------------------------------

DEFINE TABLE employee TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD specialization ON employee TYPE string ASSERT $value INSIDE ['1', '2', '3', '4', '5'] PERMISSIONS FULL;
DEFINE FIELD start_date ON employee TYPE datetime DEFAULT time::now() PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: employee
-- ------------------------------

INSERT [ { id: employee:8fvkn1do7z7f1bwk0j1q, specialization: '1', start_date: d'2026-03-30T21:09:59.090681069Z' } ];

-- ------------------------------
-- TABLE: equipment
-- ------------------------------

DEFINE TABLE equipment TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD batch ON equipment TYPE string PERMISSIONS FULL;
DEFINE FIELD brand ON equipment TYPE string PERMISSIONS FULL;
DEFINE FIELD last_repair_date ON equipment TYPE datetime PERMISSIONS FULL;
DEFINE FIELD last_submission_date ON equipment TYPE datetime PERMISSIONS FULL;
DEFINE FIELD manufacturing_date ON equipment TYPE datetime PERMISSIONS FULL;
DEFINE FIELD model ON equipment TYPE string PERMISSIONS FULL;
DEFINE FIELD sku ON equipment TYPE int DEFAULT `rand`::int(1, 1000000) PERMISSIONS FULL;

DEFINE INDEX unique_sku ON equipment FIELDS sku UNIQUE;


-- ------------------------------
-- TABLE DATA: equipment
-- ------------------------------


-- ------------------------------
-- TABLE: has
-- ------------------------------

DEFINE TABLE has TYPE RELATION IN repair OUT test SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON has TYPE record<repair> PERMISSIONS FULL;
DEFINE FIELD out ON has TYPE record<test> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: has
-- ------------------------------


-- ------------------------------
-- TABLE: inserts
-- ------------------------------

DEFINE TABLE inserts TYPE RELATION IN client OUT equipment SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON inserts TYPE record<client> PERMISSIONS FULL;
DEFINE FIELD out ON inserts TYPE record<equipment> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: inserts
-- ------------------------------


-- ------------------------------
-- TABLE: is_a
-- ------------------------------

DEFINE TABLE is_a TYPE RELATION IN user OUT registrable_user SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON is_a TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON is_a TYPE record<registrable_user> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: is_a
-- ------------------------------

INSERT RELATION [ { id: is_a:cvlagheb59vuhn9e51nr, in: user:8pl4kdxo4an6j5vkrgop, out: registrable_user:tej25f22qkg84xcqfyyy }, { id: is_a:qsyawq0juqgus0kwqqqr, in: user:mqarklizl4rwxnjq9xdj, out: registrable_user:sezn94b9mt9cer9qyux9 } ];

-- ------------------------------
-- TABLE: log
-- ------------------------------

DEFINE TABLE log TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD action ON log TYPE string PERMISSIONS FULL;
DEFINE FIELD created_at ON log TYPE datetime PERMISSIONS FULL;
DEFINE FIELD details ON log TYPE string PERMISSIONS FULL;
DEFINE FIELD user ON log TYPE record<user> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: log
-- ------------------------------


-- ------------------------------
-- TABLE: notifications
-- ------------------------------

DEFINE TABLE notifications TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD content ON notifications TYPE string PERMISSIONS FULL;
DEFINE FIELD created_at ON notifications TYPE datetime PERMISSIONS FULL;
DEFINE FIELD target ON notifications TYPE record | string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: notifications
-- ------------------------------


-- ------------------------------
-- TABLE: of_type
-- ------------------------------

DEFINE TABLE of_type TYPE RELATION IN registrable_user OUT client | employee SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON of_type TYPE record<registrable_user> PERMISSIONS FULL;
DEFINE FIELD out ON of_type TYPE record<client | employee> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: of_type
-- ------------------------------

INSERT RELATION [ { id: of_type:e01zppzrv99mpphei563, in: registrable_user:tej25f22qkg84xcqfyyy, out: employee:8fvkn1do7z7f1bwk0j1q }, { id: of_type:jwsokigqetgdp4aoxfob, in: registrable_user:sezn94b9mt9cer9qyux9, out: client:ri02s3ajbxii7qnrkym2 } ];

-- ------------------------------
-- TABLE: part
-- ------------------------------

DEFINE TABLE part TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD designation ON part TYPE string PERMISSIONS FULL;
DEFINE FIELD manufacturer ON part TYPE string PERMISSIONS FULL;
DEFINE FIELD stock_quantity ON part TYPE int PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: part
-- ------------------------------


-- ------------------------------
-- TABLE: registrable_user
-- ------------------------------

DEFINE TABLE registrable_user TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD address ON registrable_user TYPE string PERMISSIONS FULL;
DEFINE FIELD nif ON registrable_user TYPE string ASSERT $value = /^[1-9]\d{8}$/ PERMISSIONS FULL;
DEFINE FIELD phone ON registrable_user TYPE string ASSERT $value = /^[923]\d{8}$/ PERMISSIONS FULL;

DEFINE INDEX unique_nif ON registrable_user FIELDS nif UNIQUE;


-- ------------------------------
-- TABLE DATA: registrable_user
-- ------------------------------

INSERT [ { address: 'Test Address', id: registrable_user:sezn94b9mt9cer9qyux9, nif: '223456789', phone: '923456789' }, { address: 'Test Address', id: registrable_user:tej25f22qkg84xcqfyyy, nif: '123456789', phone: '923456789' } ];

-- ------------------------------
-- TABLE: repair
-- ------------------------------

DEFINE TABLE repair TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD cost ON repair TYPE decimal DEFAULT NULL PERMISSIONS FULL;
DEFINE FIELD duration ON repair TYPE duration DEFAULT NULL PERMISSIONS FULL;
DEFINE FIELD end_date ON repair TYPE datetime PERMISSIONS FULL;
DEFINE FIELD observations ON repair TYPE string PERMISSIONS FULL;
DEFINE FIELD repair_code ON repair TYPE string PERMISSIONS FULL;
DEFINE FIELD start_date ON repair TYPE datetime PERMISSIONS FULL;
DEFINE FIELD state ON repair TYPE string ASSERT $value INSIDE ['PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED', 'ARCHIVED', 'REJECTED_BY_ADMIN', 'REJECTED_BY_EMPLOYEE'] PERMISSIONS FULL;

DEFINE INDEX unique_repair_code ON repair FIELDS repair_code UNIQUE;


-- ------------------------------
-- TABLE DATA: repair
-- ------------------------------


-- ------------------------------
-- TABLE: test
-- ------------------------------

DEFINE TABLE test TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD date ON test TYPE datetime PERMISSIONS FULL;
DEFINE FIELD description ON test TYPE string PERMISSIONS FULL;
DEFINE FIELD designation ON test TYPE string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: test
-- ------------------------------


-- ------------------------------
-- TABLE: used_parts
-- ------------------------------

DEFINE TABLE used_parts TYPE RELATION IN repair OUT part SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON used_parts TYPE record<repair> PERMISSIONS FULL;
DEFINE FIELD out ON used_parts TYPE record<part> PERMISSIONS FULL;
DEFINE FIELD quantity ON used_parts TYPE int PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: used_parts
-- ------------------------------


-- ------------------------------
-- TABLE: user
-- ------------------------------

DEFINE TABLE user TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD email ON user TYPE string ASSERT string::is_email($value) PERMISSIONS FULL;
DEFINE FIELD name ON user TYPE string PERMISSIONS FULL;
DEFINE FIELD password ON user TYPE string PERMISSIONS FULL;
DEFINE FIELD status ON user TYPE string DEFAULT 'PENDING' ASSERT $value INSIDE ['ACTIVE', 'INACTIVE', 'REJECTED', 'PENDING'] PERMISSIONS FULL;
DEFINE FIELD type ON user TYPE string ASSERT $value INSIDE ['CLIENT', 'EMPLOYEE', 'ADMIN'] PERMISSIONS FULL;
DEFINE FIELD username ON user TYPE string PERMISSIONS FULL;

DEFINE INDEX unique_username ON user FIELDS username UNIQUE;


-- ------------------------------
-- TABLE DATA: user
-- ------------------------------

INSERT [ { email: 'test_employee@test.com', id: user:8pl4kdxo4an6j5vkrgop, name: 'Test Employee', password: 'test_employee', status: 'PENDING', type: 'EMPLOYEE', username: 'test_employee' }, { email: 'test_client@test.com', id: user:mqarklizl4rwxnjq9xdj, name: 'Test Client', password: 'test_client', status: 'PENDING', type: 'CLIENT', username: 'test_client' }, { email: 'asdas@asd.asd', id: user:znr5ldr3ljcpzuf1i8ov, name: 'asdsad', password: 'asdasd', status: 'ACTIVE', type: 'ADMIN', username: 'asdasd' } ];

-- ------------------------------
-- TABLE: user_repair
-- ------------------------------

DEFINE TABLE user_repair TYPE RELATION IN user OUT repair SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON user_repair TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON user_repair TYPE record<repair> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: user_repair
-- ------------------------------


-- ------------------------------
-- TABLE: viewed_notifications
-- ------------------------------

DEFINE TABLE viewed_notifications TYPE RELATION IN user OUT notifications SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON viewed_notifications TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON viewed_notifications TYPE record<notifications> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: viewed_notifications
-- ------------------------------


