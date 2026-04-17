-- ------------------------------
-- OPTION
-- ------------------------------

OPTION IMPORT;

-- ------------------------------
-- FUNCTIONS
-- ------------------------------

DEFINE FUNCTION fn::check_email($input: string) -> bool { IF !string::is_email($input) { THROW 'Invalid email format' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_nif($input: string) -> bool { IF $input != /^[1-9]\d{8}$/ { THROW 'Malformed NIF' }; IF (SELECT id FROM registrable_user WHERE nif = $input)[0] { THROW 'NIF already exists' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_phone($input: string) -> bool { IF $input != /^[923]\d{8}$/ { THROW 'Invalid phone number' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_scale($input: string) -> bool { IF $input NOTINSIDE ['A', 'B', 'C', 'D'] { THROW 'Scale not of value [A, B, C, D]' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_specialization($input: string) -> bool { IF $input NOTINSIDE ['1', '2', '3', '4', '5'] { THROW 'Scale not of value [1, 2, 3, 4, 5]' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_username($input: string) -> bool { IF (SELECT id FROM user WHERE username = $input)[0] { THROW 'Username is already taken' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::get_user_type($user: string) -> string { RETURN (SELECT VALUE type FROM user WHERE username = $user)[0] } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::user_exists($user: string, $pass: string) -> bool { IF (SELECT id FROM user WHERE username = $user AND password = $pass)[0] { RETURN true }; RETURN false; } COMMENT '' PERMISSIONS FULL;

-- ------------------------------
-- TABLE: client
-- ------------------------------

DEFINE TABLE client TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD scale ON client TYPE string ASSERT $value INSIDE ['A', 'B', 'C', 'D'] PERMISSIONS FULL;
DEFINE FIELD sector ON client TYPE string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: client
-- ------------------------------

INSERT [ { id: client:zc2mfyvyp5aghg0m4c1v, scale: 'B', sector: 'abc' } ];

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

INSERT RELATION [ { id: is_a:zat4aiep9n84o1c1lnne, in: user:y3ea297c5cwk9d30k7aa, out: registrable_user:zipcsugqkunrf8vu06cv } ];

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

INSERT [ { action: 'INSERT', created_at: d'2026-04-16T19:02:16.745039924Z', details: "User 'client' (CLIENT) was registered in the system.", id: log:0dceiay4hkt1qmqtbkrc, user: user:jska125ufshgcj8tbokb }, { action: 'INSERT', created_at: d'2026-04-16T17:41:15.555710086Z', details: "User 'mary' (EMPLOYEE) was registered in the system.", id: log:0s7w2w3oqmied2ecbpoz, user: user:6zmfjx5ur12sbbqt8spl }, { action: 'INSERT', created_at: d'2026-04-16T18:03:15.147750554Z', details: "User 'testclient' (CLIENT) was registered in the system.", id: log:4zdv9kustu9gm6pc8yvh, user: user:wsqjsdmja7vh5rxutzeh }, { action: 'INSERT', created_at: d'2026-04-16T18:54:24.928128356Z', details: "User 'testuser' (CLIENT) was registered in the system.", id: log:g0t07qgvkymzjq6bacn0, user: user:x6o6cvyg3x7ua696bacr }, { action: 'INSERT', created_at: d'2026-04-16T21:33:29.862640236Z', details: "User 'client' (CLIENT) was registered in the system.", id: log:gpb85ms3qa75c9bt3xcv, user: user:y3ea297c5cwk9d30k7aa }, { action: 'INSERT', created_at: d'2026-04-16T19:06:36.784995906Z', details: "User 'client' (CLIENT) was registered in the system.", id: log:phzg3e4fuzo3aw1peqdl, user: user:fbnkoyvc5326gwr6yzu2 }, { action: 'INSERT', created_at: d'2026-04-16T19:01:04.572193148Z', details: "User 'admin' (ADMIN) was registered in the system.", id: log:ukix8r3iwpmb3yfeluuv, user: user:3xn26pscybryt47nhopo }, { action: 'INSERT', created_at: d'2026-04-16T18:39:10.693627848Z', details: "User 'fffff' (EMPLOYEE) was registered in the system.", id: log:ul5800i0kdl69btviepr, user: user:qabvyh3c7d61hpxhtxiu } ];

-- ------------------------------
-- TABLE: notification
-- ------------------------------

DEFINE TABLE notification TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD content ON notification TYPE string PERMISSIONS FULL;
DEFINE FIELD created_at ON notification TYPE datetime DEFAULT time::now() PERMISSIONS FULL;
DEFINE FIELD target ON notification TYPE record | string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: notification
-- ------------------------------

INSERT [ { content: "User 'client' awaiting approval", created_at: d'2026-04-16T21:33:30.348484060Z', id: notification:p6indc5kfodbkfs9iba5, target: 'ADMIN' } ];

-- ------------------------------
-- TABLE: of_type
-- ------------------------------

DEFINE TABLE of_type TYPE RELATION IN registrable_user OUT client | employee SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON of_type TYPE record<registrable_user> PERMISSIONS FULL;
DEFINE FIELD out ON of_type TYPE record<client | employee> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: of_type
-- ------------------------------

INSERT RELATION [ { id: of_type:nebih1vn6d64xkugxmtj, in: registrable_user:zipcsugqkunrf8vu06cv, out: client:zc2mfyvyp5aghg0m4c1v } ];

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

INSERT [ { address: 'Bla Bla', id: registrable_user:zipcsugqkunrf8vu06cv, nif: '261734892', phone: '923100957' } ];

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

DEFINE EVENT log_user_create ON user WHEN $event = 'CREATE' THEN { CREATE log SET action = 'INSERT', created_at = time::now(), details = string::concat("User '", $after.username, "' (", $after.type, ') was registered in the system.'), user = $after.id };
DEFINE EVENT log_user_update ON user WHEN $event = 'UPDATE' THEN { LET $changes = []; LET $fields = ['name', 'password', 'username', 'email', 'status']; FOR $field IN $fields { IF $before[$field] = DISTINCT OR $after[$field] {
LET $old = $before[$field] ?? '';
LET $new = $after[$field] ?? '';
LET $change = string::concat($field, ": '", $old, "' -> '", $new, "'");
LET $changes = array::push($changes, $change);
} }; LET $details = (IF array::len($changes) > 0 { string::concat('User updated. Changes: ', array::join($changes, ', ')) } ELSE { 'User updated. No changes in monitored fields.' }); CREATE log SET action = 'UPDATE', created_at = time::now(), details = $details, user = $after.id; };

-- ------------------------------
-- TABLE DATA: user
-- ------------------------------

INSERT [ { email: 'admin@admin.com', id: user:3xn26pscybryt47nhopo, name: 'Admin', password: 'admin', status: 'ACTIVE', type: 'ADMIN', username: 'porra' }, { email: 'client@client.com', id: user:y3ea297c5cwk9d30k7aa, name: 'Test Client', password: 'client', status: 'PENDING', type: 'CLIENT', username: 'client' } ];

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
-- TABLE: viewed_notification
-- ------------------------------

DEFINE TABLE viewed_notification TYPE RELATION IN user OUT notification SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON viewed_notification TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON viewed_notification TYPE record<notification> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: viewed_notification
-- ------------------------------


